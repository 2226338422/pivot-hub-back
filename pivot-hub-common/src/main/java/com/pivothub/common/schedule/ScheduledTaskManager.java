package com.pivothub.common.schedule;

import com.pivothub.commoncore.enums.ScheduledTaskRunStatus;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunFinishDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunStartDto;
import com.pivothub.pojo.vo.system.ScheduledTaskDefinitionVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/** 根据系统任务表注册并执行本服务的定时任务。抽离出来，BusinessScheduledTaskManager和SystemScheduledTaskManager使用这个方法
 * ScheduledTaskManager大部分不对外，BusinessScheduledTaskManager和SystemScheduledTaskManager对外
 * */
@Component
public class ScheduledTaskManager {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTaskManager.class);
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final int MAX_ERROR_LENGTH = 500;
    private static final int LOAD_RETRY_SECONDS = 30;

    @Resource(name = "scheduledTaskScheduler")
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ObjectProvider<ScheduledTaskHandler> handlers;

    public void registerTasks(Supplier<List<ScheduledTaskDefinitionVo>> taskLoader,
                              Function<ScheduledTaskRunStartDto, String> startRun,
                              BiConsumer<String, ScheduledTaskRunFinishDto> finishRun) {
        List<ScheduledTaskDefinitionVo> tasks;
        try {
            tasks = taskLoader.get();
        } catch (RuntimeException failed) {
            LOG.error("定时任务加载失败", failed);
            taskScheduler.schedule(() -> registerTasks(taskLoader, startRun, finishRun),
                    Instant.now().plusSeconds(LOAD_RETRY_SECONDS));
            return;
        }
        Map<String, ScheduledTaskHandler> handlerMap = new HashMap<>();
        handlers.orderedStream().forEach(handler -> handlerMap.putIfAbsent(handler.taskCode(), handler));
        for (ScheduledTaskDefinitionVo task : tasks) {
            ScheduledTaskHandler handler = handlerMap.get(task.getTaskCode());
            if (handler == null) {
                LOG.warn("定时任务未找到处理器 taskCode={}", task.getTaskCode());
                continue;
            }
            try {
                taskScheduler.schedule(() -> execute(handler, startRun, finishRun), new CronTrigger(task.getCron(), ZONE));
            } catch (IllegalArgumentException invalidCron) {
                LOG.error("定时任务Cron无效 taskCode={}, cron={}", task.getTaskCode(), task.getCron(), invalidCron);
            }
        }
    }

    public int runNow(String taskCode, Function<ScheduledTaskRunStartDto, String> startRun,
                      BiConsumer<String, ScheduledTaskRunFinishDto> finishRun,
                      BiFunction<Integer, String, RuntimeException> taskError) {
        ScheduledTaskHandler handler = handlers.orderedStream()
                .filter(candidate -> candidate.taskCode().equals(taskCode))
                .findFirst()
                .orElseThrow(() -> taskError.apply(404, "定时任务处理器不存在"));
        ScheduledTaskRunStatus status = execute(handler, startRun, finishRun);
        if (status == null) {
            throw taskError.apply(409, "任务正在执行");
        }
        return status.getValue();
    }

    private ScheduledTaskRunStatus execute(ScheduledTaskHandler handler,
                                           Function<ScheduledTaskRunStartDto, String> startRun,
                                           BiConsumer<String, ScheduledTaskRunFinishDto> finishRun) {
        RLock lock = redissonClient.getLock(handler.lockKey());
        if (!lock.tryLock()) {
            return null;
        }
        String runId = null;
        Instant startedAt = Instant.now();
        try {
            ScheduledTaskRunStartDto startDto = new ScheduledTaskRunStartDto();
            startDto.setTaskCode(handler.taskCode());
            startDto.setStartedTime(Date.from(startedAt));
            runId = startRun.apply(startDto);
            if (!StringUtils.hasText(runId)) {
                throw new IllegalStateException("定时任务执行记录未创建");
            }
            ScheduledTaskExecutionResult result = handler.execute(runId, startedAt);
            if (result == null) {
                throw new IllegalStateException("定时任务未返回执行结果");
            }
            ScheduledTaskRunStatus status = result.getErrorMessage() == null
                    ? ScheduledTaskRunStatus.SUCCEEDED : ScheduledTaskRunStatus.FAILED;
            finish(finishRun, runId, status, result);
            return status;
        } catch (RuntimeException failed) {
            LOG.warn("定时任务执行失败 taskCode={}, runId={}", handler.taskCode(), runId, failed);
            if (StringUtils.hasText(runId)) {
                ScheduledTaskExecutionResult result = new ScheduledTaskExecutionResult();
                result.setResultSummary("任务执行失败");
                result.setErrorMessage(errorMessage(failed));
                try {
                    finish(finishRun, runId, ScheduledTaskRunStatus.FAILED, result);
                    return ScheduledTaskRunStatus.FAILED;
                } catch (RuntimeException finishFailed) {
                    LOG.warn("定时任务失败状态保存失败 taskCode={}, runId={}",
                            handler.taskCode(), runId, finishFailed);
                }
            }
            throw failed;
        } finally {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (RuntimeException failed) {
                LOG.warn("定时任务锁释放失败 taskCode={}", handler.taskCode(), failed);
            }
        }
    }

    private void finish(BiConsumer<String, ScheduledTaskRunFinishDto> finishRun, String runId, ScheduledTaskRunStatus status,
                        ScheduledTaskExecutionResult result) {
        ScheduledTaskRunFinishDto dto = new ScheduledTaskRunFinishDto();
        dto.setStatus(status.getValue());
        dto.setResultSummary(result.getResultSummary());
        dto.setResultJson(result.getResultJson());
        dto.setErrorMessage(result.getErrorMessage());
        dto.setFinishedTime(new Date());
        finishRun.accept(runId, dto);
    }

    private String errorMessage(RuntimeException failed) {
        String message = failed.getMessage();
        String result = message == null || message.isBlank() ? failed.getClass().getSimpleName() : message;
        return result.length() > MAX_ERROR_LENGTH ? result.substring(0, MAX_ERROR_LENGTH) : result;
    }
}

package com.pivothub.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pivothub.common.exception.SystemException;
import com.pivothub.common.feign.BusinessScheduledTaskClient;
import com.pivothub.commoncore.constants.system.ScheduledTaskConstants;
import com.pivothub.commoncore.enums.ScheduledTaskRunStatus;
import com.pivothub.pojo.dto.PageDto;
import com.pivothub.pojo.dto.system.ScheduledTaskPageDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunFinishDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunStartDto;
import com.pivothub.pojo.po.system.SysScheduledTask;
import com.pivothub.pojo.po.system.SysScheduledTaskRun;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.ScheduledTaskDefinitionVo;
import com.pivothub.pojo.vo.system.ScheduledTaskRunVo;
import com.pivothub.pojo.vo.system.ScheduledTaskVo;
import com.pivothub.system.mapper.SysScheduledTaskMapper;
import com.pivothub.system.mapper.SysScheduledTaskRunMapper;
import com.pivothub.system.schedule.SystemScheduledTaskManager;
import com.pivothub.system.service.ScheduledTaskService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {
    @Autowired
    private SysScheduledTaskMapper taskMapper;
    @Autowired
    private SysScheduledTaskRunMapper runMapper;
    @Autowired
    private SystemScheduledTaskManager scheduledTaskManager;
    @Lazy
    @Autowired
    private BusinessScheduledTaskClient businessScheduledTaskClient;

    @Override
    public int runNow(String taskCode) {
        SysScheduledTask task = requireTask(taskCode, true);
        return switch (task.getServiceName()) {
            case ScheduledTaskConstants.SYSTEM_SERVICE -> scheduledTaskManager.runNow(task.getTaskCode());
            case ScheduledTaskConstants.BUSINESS_SERVICE -> runBusinessTask(task.getTaskCode());
            default -> throw new SystemException(400, "该服务暂不支持手动执行定时任务");
        };
    }

    private int runBusinessTask(String taskCode) {
        try {
            return businessScheduledTaskClient.run(taskCode);
        } catch (FeignException.Conflict ex) {
            throw new SystemException(409, "任务正在执行");
        } catch (FeignException.NotFound ex) {
            throw new SystemException(404, "定时任务处理器不存在");
        }
    }

    @Override
    public PageVo<ScheduledTaskVo> page(ScheduledTaskPageDto query) {
        validatePage(query);
        query.setName(StringUtils.hasText(query.getName()) ? query.getName().trim() : null);
        query.setCode(StringUtils.hasText(query.getCode()) ? query.getCode().trim() : null);
        if (query.getStatus() != null && query.getStatus() != 0 && query.getStatus() != 1) {
            throw new SystemException(400, "任务状态只能是0或1");
        }
        List<ScheduledTaskVo> tasks;
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        try {
            tasks = taskMapper.selectPage(query);
        } finally {
            PageHelper.clearPage();
        }
        long total = new PageInfo<>(tasks).getTotal();
        return PageVo.of(query.getPageNum(), query.getPageSize(), total, tasks);
    }

    @Override
    public PageVo<ScheduledTaskRunVo> pageRuns(String taskCode, PageDto query) {
        validatePage(query);
        SysScheduledTask task = requireTask(taskCode, false);
        List<ScheduledTaskRunVo> runs;
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        try {
            runs = runMapper.selectPageByTaskId(task.getUuid());
        } finally {
            PageHelper.clearPage();
        }
        long total = new PageInfo<>(runs).getTotal();
        return PageVo.of(query.getPageNum(), query.getPageSize(), total, runs);
    }

    @Override
    public ScheduledTaskRunVo runDetail(String runId) {
        if (!StringUtils.hasText(runId)) {
            throw new SystemException(400, "执行记录标识不能为空");
        }
        ScheduledTaskRunVo run = runMapper.selectById(runId.trim());
        if (run == null) {
            throw new SystemException(404, "定时任务执行记录不存在");
        }
        return run;
    }

    @Override
    public List<ScheduledTaskDefinitionVo> listEnabled(String serviceName) {
        if (!StringUtils.hasText(serviceName)) {
            throw new SystemException(400, "服务名称不能为空");
        }
        return taskMapper.selectEnabledByService(serviceName.trim());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startRun(ScheduledTaskRunStartDto dto) {
        SysScheduledTask task = requireTask(dto.getTaskCode(), true);
        Date now = new Date();
        SysScheduledTaskRun run = new SysScheduledTaskRun();
        run.setUuid(IdWorker.getIdStr());
        run.setTaskId(task.getUuid());
        run.setStatus(ScheduledTaskRunStatus.RUNNING.getValue());
        run.setStartedTime(dto.getStartedTime());
        run.setCreateTime(now);
        run.setUpdateTime(now);
        if (runMapper.insert(run) != 1) {
            throw new SystemException(500, "定时任务执行记录创建失败");
        }
        return run.getUuid();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishRun(String runId, ScheduledTaskRunFinishDto dto) {
        if (!StringUtils.hasText(runId)) {
            throw new SystemException(400, "执行记录标识不能为空");
        }
        if (!ScheduledTaskRunStatus.SUCCEEDED.getValue().equals(dto.getStatus())
                && !ScheduledTaskRunStatus.FAILED.getValue().equals(dto.getStatus())) {
            throw new SystemException(400, "定时任务完成状态不正确");
        }
        SysScheduledTaskRun run = new SysScheduledTaskRun();
        run.setUuid(runId.trim());
        run.setStatus(dto.getStatus());
        run.setResultSummary(dto.getResultSummary());
        run.setResultJson(dto.getResultJson());
        run.setErrorMessage(dto.getErrorMessage());
        run.setFinishedTime(dto.getFinishedTime());
        if (runMapper.finish(run) != 1) {
            throw new SystemException(409, "定时任务执行记录已完成或不存在");
        }
    }

    private SysScheduledTask requireTask(String taskCode, boolean active) {
        if (!StringUtils.hasText(taskCode)) {
            throw new SystemException(400, "任务编码不能为空");
        }
        String code = taskCode.trim();
        SysScheduledTask task = active ? taskMapper.selectActiveByCode(code) : taskMapper.selectByCode(code);
        if (task == null) {
            throw new SystemException(404, active ? "定时任务不存在或已停用" : "定时任务不存在");
        }
        return task;
    }

    private void validatePage(PageDto query) {
        if (query == null || query.getPageNum() == null || query.getPageNum() < 1
                || query.getPageSize() == null || query.getPageSize() < 1 || query.getPageSize() > 100) {
            throw new SystemException(400, "页码至少为1，每页条数为1到100");
        }
    }
}

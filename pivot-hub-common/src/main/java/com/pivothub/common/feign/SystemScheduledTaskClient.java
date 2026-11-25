package com.pivothub.common.feign;

import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunFinishDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunStartDto;
import com.pivothub.pojo.vo.system.ScheduledTaskDefinitionVo;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** 系统定时任务的内部服务接口。 */
@FeignClient(name = "pivot-hub-system", contextId = "systemScheduledTaskClient", path = "/system/feign/scheduled-tasks")
public interface SystemScheduledTaskClient {
    /** 查询指定服务需要注册的启用任务。 */
    @GetMapping("/enabled")
    List<ScheduledTaskDefinitionVo> listEnabled(@RequestParam("serviceName") String serviceName);

    /** 创建执行中的任务记录。 */
    @PostMapping("/runs/start")
    String startRun(@Valid @RequestBody ScheduledTaskRunStartDto dto);

    /** 完成任务执行记录。 */
    @PutMapping("/runs/{runId}/finish")
    void finishRun(@PathVariable("runId") String runId,
                   @Valid @RequestBody ScheduledTaskRunFinishDto dto);
}

package com.pivothub.system.controller.feign;

import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunFinishDto;
import com.pivothub.pojo.dto.system.feign.ScheduledTaskRunStartDto;
import com.pivothub.pojo.vo.system.ScheduledTaskDefinitionVo;
import com.pivothub.system.service.ScheduledTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/feign/scheduled-tasks")
public class SystemScheduledTaskFeignController {
    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @GetMapping("/enabled")
    public List<ScheduledTaskDefinitionVo> listEnabled(@RequestParam("serviceName") String serviceName) {
        return scheduledTaskService.listEnabled(serviceName);
    }

    @PostMapping("/runs/start")
    public String startRun(@Valid @RequestBody ScheduledTaskRunStartDto dto) {
        return scheduledTaskService.startRun(dto);
    }

    @PutMapping("/runs/{runId}/finish")
    public void finishRun(@PathVariable("runId") String runId,
                          @Valid @RequestBody ScheduledTaskRunFinishDto dto) {
        scheduledTaskService.finishRun(runId, dto);
    }
}

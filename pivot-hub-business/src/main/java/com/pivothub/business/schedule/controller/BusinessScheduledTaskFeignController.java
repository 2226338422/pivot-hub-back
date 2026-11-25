package com.pivothub.business.schedule.controller;

import com.pivothub.business.schedule.BusinessScheduledTaskManager;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@RequestMapping("/business/feign/scheduled-tasks")
public class BusinessScheduledTaskFeignController {
    @Autowired
    private BusinessScheduledTaskManager scheduledTaskManager;

    @PostMapping("/run")
    public int run(@RequestParam("taskCode") String taskCode) {
        return scheduledTaskManager.runNow(taskCode);
    }
}

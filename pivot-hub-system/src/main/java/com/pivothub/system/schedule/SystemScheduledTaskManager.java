package com.pivothub.system.schedule;

import com.pivothub.common.exception.SystemException;
import com.pivothub.common.schedule.ScheduledTaskManager;
import com.pivothub.commoncore.constants.system.ScheduledTaskConstants;
import com.pivothub.system.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SystemScheduledTaskManager {
    @Autowired
    private ScheduledTaskManager taskManager;
    @Lazy
    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @EventListener(ApplicationReadyEvent.class)
    public void registerTasks() {
        taskManager.registerTasks(() -> scheduledTaskService.listEnabled(ScheduledTaskConstants.SYSTEM_SERVICE),
                scheduledTaskService::startRun, scheduledTaskService::finishRun);
    }

    public int runNow(String taskCode) {
        return taskManager.runNow(taskCode, scheduledTaskService::startRun, scheduledTaskService::finishRun,
                SystemException::new);
    }
}

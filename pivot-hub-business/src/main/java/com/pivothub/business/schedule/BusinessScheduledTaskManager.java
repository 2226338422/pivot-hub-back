package com.pivothub.business.schedule;

import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.feign.SystemScheduledTaskClient;
import com.pivothub.common.schedule.ScheduledTaskManager;
import com.pivothub.commoncore.constants.system.ScheduledTaskConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BusinessScheduledTaskManager {
    @Autowired
    private ScheduledTaskManager taskManager;
    @Lazy
    @Autowired
    private SystemScheduledTaskClient scheduledTaskClient;

    @EventListener(ApplicationReadyEvent.class)
    public void registerTasks() {
        taskManager.registerTasks(() -> scheduledTaskClient.listEnabled(ScheduledTaskConstants.BUSINESS_SERVICE),
                scheduledTaskClient::startRun, scheduledTaskClient::finishRun);
    }

    public int runNow(String taskCode) {
        return taskManager.runNow(taskCode, scheduledTaskClient::startRun, scheduledTaskClient::finishRun,
                BusinessException::new);
    }
}

package com.pivothub.common.schedule;

import java.time.Instant;

/** 定时任务处理器。新增处理器需实现这个接口 */
public interface ScheduledTaskHandler {
    String taskCode();

    String lockKey();

    ScheduledTaskExecutionResult execute(String runId, Instant startedAt);
}

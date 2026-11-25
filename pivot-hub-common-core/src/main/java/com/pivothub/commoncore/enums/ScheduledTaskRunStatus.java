package com.pivothub.commoncore.enums;

/** 定时任务执行状态。 */
public enum ScheduledTaskRunStatus implements ValueEnum<Integer> {
    RUNNING(0, "执行中"),
    SUCCEEDED(1, "成功"),
    FAILED(2, "失败");

    private final Integer value;
    private final String name;

    ScheduledTaskRunStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}

package com.pivothub.common.schedule;

import lombok.Data;

/** 定时任务执行结果。 */
@Data
public class ScheduledTaskExecutionResult {
    /** 执行结果摘要，用于执行记录列表展示。 */
    private String resultSummary;

    /** 执行结果明细，以 JSON 字符串保存。 */
    private String resultJson;

    /** 失败原因，为 null 时表示任务执行成功。 */
    private String errorMessage;
}

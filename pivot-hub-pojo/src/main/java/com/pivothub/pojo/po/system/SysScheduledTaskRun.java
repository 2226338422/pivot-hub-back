package com.pivothub.pojo.po.system;

import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/** 系统定时任务执行记录。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysScheduledTaskRun extends BasePo {
    private String uuid;

    /** 任务 UUID。 */
    private String taskId;

    /** 执行状态 0-执行中 1-成功 2-失败。 */
    private Integer status;

    /** 执行结果摘要。 */
    private String resultSummary;

    /** 执行结果 JSON。 */
    private String resultJson;

    /** 失败原因。 */
    private String errorMessage;

    /** 开始时间。 */
    private Date startedTime;

    /** 结束时间。 */
    private Date finishedTime;
}

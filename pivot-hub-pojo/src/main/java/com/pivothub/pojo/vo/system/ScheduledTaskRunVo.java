package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "定时任务执行记录")
public class ScheduledTaskRunVo {
    @Schema(description = "执行记录UUID")
    private String uuid;

    @Schema(description = "任务UUID")
    private String taskId;

    @Schema(description = "执行状态 0-执行中 1-成功 2-失败")
    private Integer status;

    @Schema(description = "执行结果摘要")
    private String resultSummary;

    @Schema(description = "执行结果JSON")
    private String resultJson;

    @Schema(description = "失败原因")
    private String errorMessage;

    @Schema(description = "开始时间")
    private Date startedTime;

    @Schema(description = "结束时间")
    private Date finishedTime;
}

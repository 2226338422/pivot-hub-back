package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "定时任务列表项")
public class ScheduledTaskVo {
    @Schema(description = "任务UUID")
    private String uuid;

    @Schema(description = "任务编码")
    private String taskCode;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "执行任务的服务名称")
    private String serviceName;

    @Schema(description = "Spring Cron表达式")
    private String cron;

    @Schema(description = "状态 0-停用 1-启用")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "最近执行状态 0-执行中 1-成功 2-失败")
    private Integer latestRunStatus;

    @Schema(description = "最近执行结果摘要")
    private String latestResultSummary;

    @Schema(description = "最近开始时间")
    private Date latestStartedTime;

    @Schema(description = "最近结束时间")
    private Date latestFinishedTime;
}

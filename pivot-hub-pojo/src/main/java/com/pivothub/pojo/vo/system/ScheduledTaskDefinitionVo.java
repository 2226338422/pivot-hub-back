package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "定时任务定义")
public class ScheduledTaskDefinitionVo {
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
}

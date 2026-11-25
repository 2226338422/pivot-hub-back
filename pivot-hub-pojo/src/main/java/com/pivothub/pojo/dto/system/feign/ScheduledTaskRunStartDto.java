package com.pivothub.pojo.dto.system.feign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "内部服务创建定时任务执行记录")
public class ScheduledTaskRunStartDto {
    @Schema(description = "任务编码")
    @NotBlank(message = "任务编码不能为空")
    @Size(max = 64, message = "任务编码最多64字")
    private String taskCode;

    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private Date startedTime;
}

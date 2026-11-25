package com.pivothub.pojo.dto.system.feign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "内部服务完成定时任务执行记录")
public class ScheduledTaskRunFinishDto {
    @Schema(description = "执行状态 1-成功 2-失败")
    @NotNull(message = "执行状态不能为空")
    private Integer status;

    @Schema(description = "执行结果摘要")
    @Size(max = 500, message = "执行结果摘要最多500字")
    private String resultSummary;

    @Schema(description = "执行结果JSON")
    private String resultJson;

    @Schema(description = "失败原因")
    @Size(max = 500, message = "失败原因最多500字")
    private String errorMessage;

    @Schema(description = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private Date finishedTime;
}

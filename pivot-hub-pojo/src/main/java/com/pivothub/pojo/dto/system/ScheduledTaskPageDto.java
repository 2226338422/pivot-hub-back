package com.pivothub.pojo.dto.system;

import com.pivothub.pojo.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务分页查询")
public class ScheduledTaskPageDto extends PageDto {
    @Schema(description = "任务名称，包含匹配")
    private String name;

    @Schema(description = "任务编码，包含匹配")
    private String code;

    @Schema(description = "状态 0-停用 1-启用")
    private Integer status;
}

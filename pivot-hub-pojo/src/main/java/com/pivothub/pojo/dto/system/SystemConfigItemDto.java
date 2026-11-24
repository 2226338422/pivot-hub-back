package com.pivothub.pojo.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "待保存的系统配置项")
public class SystemConfigItemDto {
    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置键不能为空")
    private String key;

    @Schema(description = "配置值，按文本传输", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配置值不能为空")
    private String value;
}

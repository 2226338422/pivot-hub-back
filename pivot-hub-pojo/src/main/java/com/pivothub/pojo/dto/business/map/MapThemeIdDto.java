package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "地图主题标识请求")
public class MapThemeIdDto {
    @NotBlank(message = "主题唯一标识不能为空")
    @Size(max = 32, message = "主题唯一标识最多32字")
    @Schema(description = "主题唯一标识")
    private String themeId;
}

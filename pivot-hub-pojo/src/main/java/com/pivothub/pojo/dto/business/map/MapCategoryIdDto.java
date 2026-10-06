package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "地图地点分类标识请求")
public class MapCategoryIdDto {
    @NotBlank(message = "分类唯一标识不能为空")
    @Size(max = 32, message = "分类唯一标识最多32字")
    @Schema(description = "分类唯一标识")
    private String categoryId;
}

package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "修改地图地点分类")
public class MapCategoryUpdateDto {
    @NotBlank(message = "分类唯一标识不能为空")
    @Size(max = 32, message = "分类唯一标识最多32字")
    @Schema(description = "分类唯一标识")
    private String categoryId;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称最多64字")
    @Schema(description = "分类名称，最多64字")
    private String name;

    @Size(max = 32, message = "关联图标素材标识最多32字")
    @Schema(description = "关联图标素材标识，当前图标绑定未开放，需为空")
    private String iconId;

    @NotNull(message = "分类展示排序不能为空")
    @Schema(description = "分类展示排序，默认0")
    private Integer sort = 0;
}

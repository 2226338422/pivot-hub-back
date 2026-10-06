package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "地图已保存地点筛选条件")
public class MapPointQueryDto {
    @NotBlank(message = "所属主题唯一标识不能为空")
    @Size(max = 32, message = "所属主题唯一标识最多32字")
    @Schema(description = "所属主题唯一标识")
    private String themeId;

    @Size(max = 200, message = "地点名称模糊查询关键词最多200字")
    @Schema(description = "地点名称模糊查询关键词，最多200字")
    private String keyword;

    @Size(max = 32, message = "分类筛选标识最多32字")
    @Schema(description = "分类筛选标识，不传则不按分类筛选")
    private String categoryId;

    @Schema(description = "是否仅查询未分类地点，默认false，不能与分类标识同时指定")
    private Boolean uncategorized = false;

    @AssertTrue(message = "分类与未分类筛选不能同时指定")
    @Schema(hidden = true)
    public boolean isCategoryFilterValid() {
        return !Boolean.TRUE.equals(uncategorized) || categoryId == null || categoryId.isBlank();
    }
}

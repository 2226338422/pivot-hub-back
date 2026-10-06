package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询高德地点详情")
public class AmapPoiIdDto {
    @Schema(description = "高德POI唯一标识")
    private String poiId;
}

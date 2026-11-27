package com.pivothub.pojo.vo.business.map.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "地图坐标")
public class MapCoordinateVo {
    @Schema(description = "经度")
    private double longitude;

    @Schema(description = "纬度")
    private double latitude;
}

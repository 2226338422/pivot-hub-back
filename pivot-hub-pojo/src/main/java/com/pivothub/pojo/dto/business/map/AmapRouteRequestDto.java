package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "高德路线规划请求")
public class AmapRouteRequestDto {
    @Schema(description = "出行方式：car-驾车，walk-步行，ride-骑行，bus-公交")
    private String mode;

    @Schema(description = "用户明确选择的路线起点坐标")
    private AmapCoordinateDto start;

    @Schema(description = "路线终点坐标")
    private AmapCoordinateDto end;

    @Schema(description = "公交起点城市编码，不是行政区编码，不传时根据起点查询")
    private String originCityCode;

    @Schema(description = "公交终点城市编码，不是行政区编码，不传时根据终点查询")
    private String destinationCityCode;
}

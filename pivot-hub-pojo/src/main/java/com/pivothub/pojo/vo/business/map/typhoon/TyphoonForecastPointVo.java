package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "台风未来时刻预报点")
public class TyphoonForecastPointVo {
    @Schema(description = "预报有效时间，ISO8601")
    private String time;

    @Schema(description = "WGS84经度")
    private Double longitude;

    @Schema(description = "WGS84纬度")
    private Double latitude;

    @Schema(description = "来源强度等级")
    private String level;

    @Schema(description = "最大风速，米每秒")
    private Double windSpeed;

    @Schema(description = "中心气压，百帕")
    private Double pressure;

    @Schema(description = "移动速度，公里每小时")
    private Double moveSpeed;

    @Schema(description = "移动方向")
    private String moveDirection;
}

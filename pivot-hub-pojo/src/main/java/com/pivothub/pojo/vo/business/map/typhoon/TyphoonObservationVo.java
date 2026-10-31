package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "台风实况观测点")
public class TyphoonObservationVo {
    @Schema(description = "观测时间，ISO8601")
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

    @Schema(description = "来源提供的风圈")
    private List<TyphoonWindCircleVo> windCircles;
}

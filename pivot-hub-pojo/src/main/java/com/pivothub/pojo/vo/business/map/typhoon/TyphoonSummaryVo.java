package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "台风列表中的基本信息")
public class TyphoonSummaryVo {
    @Schema(description = "数据源台风标识")
    private String id;

    @Schema(description = "台风名称")
    private String name;

    @Schema(description = "台风编号")
    private String number;

    @Schema(description = "是否活跃")
    private boolean active;

    @Schema(description = "海域")
    private String basin;

    @Schema(description = "来源强度等级")
    private String level;

    @Schema(description = "最新观测时间，ISO8601")
    private String observedAt;

    @Schema(description = "WGS84经度")
    private Double longitude;

    @Schema(description = "WGS84纬度")
    private Double latitude;
}

package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "台风最新观测对应的预报")
public class TyphoonForecastVo {
    @Schema(description = "预报机构")
    private String agency;

    @Schema(description = "来源提供的起报时间，ISO8601")
    private String issuedAt;

    @Schema(description = "来源更新时间，ISO8601")
    private String updatedAt;

    @Schema(description = "未来预报点")
    private List<TyphoonForecastPointVo> points;
}

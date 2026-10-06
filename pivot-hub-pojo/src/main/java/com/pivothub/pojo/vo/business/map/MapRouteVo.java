package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "高德路线规划结果")
public class MapRouteVo {
    @Schema(description = "路线分段折线列表")
    private List<MapRoutePathVo> paths = new ArrayList<>();

    @Schema(description = "路线总距离，单位米，未知时为空")
    private BigDecimal distance;

    @Schema(description = "预计用时，单位秒，未知时为空")
    private BigDecimal duration;

    @Schema(description = "路线步骤文字说明")
    private List<String> steps = new ArrayList<>();
}

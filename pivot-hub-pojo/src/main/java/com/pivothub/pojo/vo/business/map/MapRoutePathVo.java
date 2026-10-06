package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "路线分段折线信息")
public class MapRoutePathVo {
    @Schema(description = "GCJ-02折线坐标，格式为经度、纬度组成的坐标数组")
    private List<List<BigDecimal>> coordinates;

    @Schema(description = "分段类型：drive-驾车，walk-步行，ride-骑行，bus-公交，subway-地铁")
    private String kind;
}

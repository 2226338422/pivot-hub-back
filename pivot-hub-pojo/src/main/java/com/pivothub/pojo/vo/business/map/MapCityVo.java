package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "网络IP城市识别结果")
public class MapCityVo {
    @Schema(description = "识别到的城市名称")
    private String cityName;

    @Schema(description = "城市范围，southWest为西南坐标，northEast为东北坐标，坐标顺序为经度、纬度")
    private Map<String, List<BigDecimal>> bounds;

    @Schema(description = "城市识别来源，固定为ip，不代表设备精确定位")
    private String source = "ip";

    @Schema(description = "是否成功识别网络城市")
    private boolean available;
}

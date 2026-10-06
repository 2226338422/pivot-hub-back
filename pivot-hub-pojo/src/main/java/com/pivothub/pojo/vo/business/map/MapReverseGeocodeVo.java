package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "地图选点逆地理编码结果")
public class MapReverseGeocodeVo {
    @Schema(description = "原点击位置经度，GCJ-02坐标")
    private BigDecimal longitude;

    @Schema(description = "原点击位置纬度，GCJ-02坐标")
    private BigDecimal latitude;

    @Schema(description = "高德返回的点击位置地址描述")
    private String address;

    @Schema(description = "省、市、区组成的行政区域描述")
    private String district;

    @Schema(description = "附近地点候选，最多10个，需用户明确选择")
    private List<SearchPoiVo> pois = new ArrayList<>();
}

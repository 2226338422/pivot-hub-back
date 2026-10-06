package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "高德地点搜索及详情信息")
public class SearchPoiVo {
    @Schema(description = "高德POI唯一标识")
    private String poiId;

    @Schema(description = "高德地点名称")
    private String name;

    @Schema(description = "地点地址")
    private String address;

    @Schema(description = "所属行政区域描述")
    private String district;

    @Schema(description = "高德城市编码，公交路线使用，不是行政区编码")
    private String cityCode;

    @Schema(description = "地点经度，GCJ-02坐标")
    private BigDecimal longitude;

    @Schema(description = "地点纬度，GCJ-02坐标")
    private BigDecimal latitude;
}

package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "地图地点列表与标记摘要")
public class MapPointSummaryVo {
    @Schema(description = "地点唯一标识")
    private String uuid;

    @Schema(description = "所属主题唯一标识")
    private String themeId;

    @Schema(description = "所属分类标识，未分类时为空")
    private String categoryId;

    @Schema(description = "地点名称")
    private String name;

    @Schema(description = "地点地址描述")
    private String address;

    @Schema(description = "地点经度，GCJ-02坐标")
    private BigDecimal longitude;

    @Schema(description = "地点纬度，GCJ-02坐标")
    private BigDecimal latitude;

    @Schema(description = "地点来源：0-手工或地图选点，1-高德POI")
    private Integer source;

    @Schema(description = "高德POI标识，手工来源时为空")
    private String amapPoiId;

    @Schema(description = "地点分类关联的图标素材标识，未绑定时为空")
    private String iconId;
}

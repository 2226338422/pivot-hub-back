package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "私有地图主题信息")
public class MapThemeVo {
    @Schema(description = "主题唯一标识")
    private String uuid;

    @Schema(description = "主题名称")
    private String name;

    @Schema(description = "主题描述")
    private String description;

    @Schema(description = "默认中心经度，GCJ-02坐标，未设置时为空")
    private BigDecimal centerLongitude;

    @Schema(description = "默认中心纬度，GCJ-02坐标，未设置时为空")
    private BigDecimal centerLatitude;

    @Schema(description = "默认地图缩放级别")
    private Integer defaultZoom;

    @Schema(description = "主题展示排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "最后更新时间")
    private Date updateTime;
}

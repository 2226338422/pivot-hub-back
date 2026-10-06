package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "高德地址输入提示候选")
public class SearchTipVo {
    @Schema(description = "高德POI唯一标识，无具体地点标识时可为空")
    private String poiId;

    @Schema(description = "候选地点名称")
    private String name;

    @Schema(description = "候选地点地址")
    private String address;

    @Schema(description = "所属行政区域描述")
    private String district;

    @Schema(description = "高德城市编码，公交路线使用，不是行政区编码")
    private String cityCode;

    @Schema(description = "候选经度，GCJ-02坐标，无有效坐标时为空")
    private BigDecimal longitude;

    @Schema(description = "候选纬度，GCJ-02坐标，无有效坐标时为空")
    private BigDecimal latitude;
}

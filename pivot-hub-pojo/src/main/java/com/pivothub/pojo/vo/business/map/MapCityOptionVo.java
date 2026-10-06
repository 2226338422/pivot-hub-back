package com.pivothub.pojo.vo.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "地图地址搜索城市选项")
public class MapCityOptionVo {
    @Schema(description = "所属省、自治区、直辖市或特别行政区名称")
    private String province;

    @Schema(description = "城市名称")
    private String name;

    @Schema(description = "行政区编码，用于城市搜索范围限制")
    private String adcode;

    @Schema(description = "所属地级行政区名称，直辖市或省直属城市可为空")
    private String parentCity;

    @Schema(description = "行政区级别：province-直辖市或特别行政区，city-地级行政区，district-县级市")
    private String level;
}

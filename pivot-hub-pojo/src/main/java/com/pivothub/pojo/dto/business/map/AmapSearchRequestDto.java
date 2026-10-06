package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "高德地址输入提示查询")
public class AmapSearchRequestDto {
    @Schema(description = "搜索关键词，长度2至200字")
    private String keyword;

    @Schema(description = "搜索城市名称或行政区编码，不传则不限定城市")
    private String city;
}

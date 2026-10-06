package com.pivothub.pojo.dto.business.map;

import com.pivothub.pojo.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "高德地点分页搜索，每页最多20条，页码最多100")
public class AmapPoiPageDto extends PageDto {
    @Schema(description = "搜索关键词，长度2至200字")
    private String keyword;

    @Schema(description = "搜索城市名称或行政区编码，不传则不限定城市")
    private String city;
}

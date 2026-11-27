package com.pivothub.pojo.dto.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "单个台风对一个省份的命中结果")
public class TyphoonProvinceMatchDto {
    @Schema(description = "省级行政区名称")
    private String provinceName;
}

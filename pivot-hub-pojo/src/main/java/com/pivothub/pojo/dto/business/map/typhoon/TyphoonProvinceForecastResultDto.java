package com.pivothub.pojo.dto.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
@Schema(description = "台风预测路径与省份的匹配结果")
public class TyphoonProvinceForecastResultDto {
    @Schema(description = "命中结果，外层键为台风 ID，内层键为省级行政区划编码")
    private Map<String, Map<String, TyphoonProvinceMatchDto>> matches = new LinkedHashMap<>();

    @Schema(description = "预测路径获取或坐标转换失败的台风 ID")
    private Set<String> unavailableStormIds = new LinkedHashSet<>();

    @Schema(description = "省界数据获取或处理失败的省份编码")
    private Set<String> unavailableProvinceCodes = new LinkedHashSet<>();
}

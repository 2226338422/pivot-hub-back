package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "高德坐标请求")
public class AmapCoordinateDto {
    @Schema(description = "经度，GCJ-02坐标，范围-180至180，最多6位小数")
    private BigDecimal longitude;

    @Schema(description = "纬度，GCJ-02坐标，范围-90至90，最多6位小数")
    private BigDecimal latitude;
}

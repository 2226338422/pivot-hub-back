package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "新增私有地图主题")
public class MapThemeCreateDto {
    @NotBlank(message = "主题名称不能为空")
    @Size(max = 100, message = "主题名称最多100字")
    @Schema(description = "主题名称，最多100字")
    private String name;

    @Size(max = 500, message = "主题描述最多500字")
    @Schema(description = "主题描述，最多500字")
    private String description;

    @DecimalMin(value = "-180", message = "默认中心经度不能小于-180")
    @DecimalMax(value = "180", message = "默认中心经度不能大于180")
    @Digits(integer = 3, fraction = 6, message = "默认中心经度整数部分最多3位，小数部分最多6位")
    @Schema(description = "默认中心经度，GCJ-02坐标，最多6位小数，与纬度同时设置或同时为空")
    private BigDecimal centerLongitude;

    @DecimalMin(value = "-90", message = "默认中心纬度不能小于-90")
    @DecimalMax(value = "90", message = "默认中心纬度不能大于90")
    @Digits(integer = 2, fraction = 6, message = "默认中心纬度整数部分最多2位，小数部分最多6位")
    @Schema(description = "默认中心纬度，GCJ-02坐标，最多6位小数，与经度同时设置或同时为空")
    private BigDecimal centerLatitude;

    @NotNull(message = "默认地图缩放级别不能为空")
    @Min(value = 3, message = "默认地图缩放级别不能小于3")
    @Max(value = 20, message = "默认地图缩放级别不能大于20")
    @Schema(description = "默认地图缩放级别，取值3至20，默认14")
    private Integer defaultZoom = 14;

    @NotNull(message = "主题展示排序不能为空")
    @Schema(description = "主题展示排序，默认0")
    private Integer sort = 0;

    @AssertTrue(message = "中心经纬度必须同时设置或同时为空")
    @Schema(hidden = true)
    public boolean isCenterPairValid() {
        return (centerLongitude == null) == (centerLatitude == null);
    }
}

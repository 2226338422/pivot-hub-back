package com.pivothub.pojo.dto.business.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Data
@Schema(description = "新增地图地点")
public class MapPointCreateDto {
    @NotBlank(message = "所属主题唯一标识不能为空")
    @Size(max = 32, message = "所属主题唯一标识最多32字")
    @Schema(description = "所属主题唯一标识")
    private String themeId;

    @Size(max = 32, message = "所属分类标识最多32字")
    @Schema(description = "所属分类标识，空表示未分类")
    private String categoryId;

    @NotBlank(message = "地点名称不能为空")
    @Size(max = 200, message = "地点名称最多200字")
    @Schema(description = "地点名称，最多200字")
    private String name;

    @Size(max = 500, message = "地点地址描述最多500字")
    @Schema(description = "地点地址描述，最多500字")
    private String address;

    @NotNull(message = "地点经度不能为空")
    @DecimalMin(value = "-180", message = "地点经度不能小于-180")
    @DecimalMax(value = "180", message = "地点经度不能大于180")
    @Digits(integer = 3, fraction = 6, message = "地点经度整数部分最多3位，小数部分最多6位")
    @Schema(description = "地点经度，GCJ-02坐标，范围-180至180，最多6位小数")
    private BigDecimal longitude;

    @NotNull(message = "地点纬度不能为空")
    @DecimalMin(value = "-90", message = "地点纬度不能小于-90")
    @DecimalMax(value = "90", message = "地点纬度不能大于90")
    @Digits(integer = 2, fraction = 6, message = "地点纬度整数部分最多2位，小数部分最多6位")
    @Schema(description = "地点纬度，GCJ-02坐标，范围-90至90，最多6位小数")
    private BigDecimal latitude;

    @Size(max = 10000, message = "地点文字笔记最多10000字")
    @Schema(description = "地点文字笔记，最多10000字")
    private String note;

    @NotNull(message = "地点来源不能为空")
    @Min(value = 0, message = "地点来源只能为0或1")
    @Max(value = 1, message = "地点来源只能为0或1")
    @Schema(description = "地点来源：0-手工或地图选点，1-高德POI，默认0")
    private Integer source = 0;

    @Size(max = 100, message = "高德POI标识最多100字")
    @Schema(description = "高德POI标识，高德来源时必填")
    private String amapPoiId;
}

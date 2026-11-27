package com.pivothub.pojo.vo.business.map.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德省级行政区边界")
public class MapProvinceBoundaryVo {
    @Schema(description = "省级行政区名称")
    private String name;

    @Schema(description = "高德坐标系下的边界轮廓列表")
    private List<List<MapCoordinateVo>> outlines;
}

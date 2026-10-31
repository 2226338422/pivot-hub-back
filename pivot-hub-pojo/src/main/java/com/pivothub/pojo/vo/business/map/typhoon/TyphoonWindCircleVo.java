package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "风圈各象限半径")
public class TyphoonWindCircleVo {
    @Schema(description = "风力等级")
    private Integer level;

    @Schema(description = "东北象限半径，公里")
    private Double northeast;

    @Schema(description = "东南象限半径，公里")
    private Double southeast;

    @Schema(description = "西南象限半径，公里")
    private Double southwest;

    @Schema(description = "西北象限半径，公里")
    private Double northwest;
}

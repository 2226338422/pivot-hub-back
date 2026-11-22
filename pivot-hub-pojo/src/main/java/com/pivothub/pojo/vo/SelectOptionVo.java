package com.pivothub.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 下拉选择框选项。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "下拉选项")
public class SelectOptionVo {
    @Schema(description = "选项编码")
    private String value;

    @Schema(description = "显示名称")
    private String label;
}

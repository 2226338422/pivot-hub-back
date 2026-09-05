package com.pivothub.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模块实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysModule extends BasePo {
    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "模块编码")
    private String moduleCode;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;
}

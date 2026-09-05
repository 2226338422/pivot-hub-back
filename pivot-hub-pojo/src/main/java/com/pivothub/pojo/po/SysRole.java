package com.pivothub.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BasePo {
    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}

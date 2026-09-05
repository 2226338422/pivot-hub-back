package com.pivothub.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体（可关联角色或用户）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPermission extends BasePo {
    @Schema(description = "权限类型 1-角色 2-用户")
    private Integer permissionType;

    @Schema(description = "角色ID或用户ID")
    private Long targetId;

    @Schema(description = "菜单ID")
    private Long menuId;
}

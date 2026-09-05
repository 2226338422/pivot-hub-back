package com.pivothub.pojo.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BasePo {
    @Schema(description = "父菜单ID，0为顶级")
    private Long parentId;

    @Schema(description = "模块ID")
    private Long moduleId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "类型 0-目录 1-菜单 2-按钮")
    private Integer menuType;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;
}

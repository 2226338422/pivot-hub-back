package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 菜单树VO
 */
@Data
@Schema(description = "菜单树")
public class SysMenuTreeVo {
    @Schema(description = "菜单uuid")
    private String uuid;

    @Schema(description = "父菜单uuid")
    private String parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "Web端路由")
    private String webUrl;

    @Schema(description = "App端路由")
    private String appUrl;

    @Schema(description = "权限标识")
    private String code;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "子菜单")
    private List<SysMenuTreeVo> children;
}

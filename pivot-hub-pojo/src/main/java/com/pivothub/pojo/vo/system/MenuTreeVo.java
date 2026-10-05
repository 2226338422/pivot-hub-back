package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "完整可授权菜单树节点")
public class MenuTreeVo {
    @Schema(description = "菜单唯一标识")
    private String uuid;
    @Schema(description = "菜单编码")
    private String code;
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "父菜单唯一标识")
    private String parentId;
    @Schema(description = "所属模块")
    private String moduleId;
    @Schema(description = "Web路由")
    private String webUrl;
    @Schema(description = "App路由")
    private String appUrl;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "菜单排序")
    private Integer sort;
    @Schema(description = "菜单状态 0-禁用 1-正常")
    private Integer status;
    @Schema(description = "菜单或祖先禁用时不可操作")
    private boolean disabled;
    @Schema(description = "子菜单")
    private List<MenuTreeVo> children = new ArrayList<>();
}

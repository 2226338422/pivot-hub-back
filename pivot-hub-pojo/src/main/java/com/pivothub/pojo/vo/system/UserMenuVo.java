package com.pivothub.pojo.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "当前用户有权访问的菜单节点")
public class UserMenuVo {
    @Schema(description = "菜单唯一标识")
    private String uuid;
    @Schema(description = "菜单编码")
    private String code;
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "父菜单唯一标识，0为顶级")
    private String parentId;
    @Schema(description = "所属模块唯一标识")
    private String moduleId;
    @Schema(description = "Web端路由路径")
    private String webUrl;
    @Schema(description = "App端路由路径")
    private String appUrl;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "菜单排序，数值越小越靠前")
    private Integer sort;
    @Schema(description = "有权访问的子菜单")
    private List<UserMenuVo> children = new ArrayList<>();
}

package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.vo.MenuTreeVo;
import com.pivothub.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @Operation(summary = "根据用户ID查询菜单树")
    @GetMapping("/tree/{userId}")
    public Result<List<MenuTreeVo>> getMenuTree(@PathVariable Long userId) {
        return Result.success(menuService.getMenuTreeByUserId(userId));
    }

    @Operation(summary = "根据角色ID查询菜单ID列表")
    @GetMapping("/role/{roleId}")
    public Result<List<Long>> getMenuIdsByRole(@PathVariable Long roleId) {
        return Result.success(menuService.getMenuIdsByRoleId(roleId));
    }
}

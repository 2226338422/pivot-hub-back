package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.vo.system.UserMenuVo;
import com.pivothub.pojo.vo.system.MenuTreeVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.service.MenuManagementService;
import com.pivothub.system.service.UserMenuService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.pivothub.commoncore.constants.system.RoleManagementConstants.MANAGEMENT_MENU_CODE;

@RestController
@RequestMapping("/system/menu")
public class SystemMenuController {
    @Autowired
    private UserMenuService userMenuService;
    @Autowired
    private MenuManagementService menuManagementService;

    @Operation(summary = "获取完整父子菜单授权树")
    @MenuPermission(MANAGEMENT_MENU_CODE)
    @GetMapping("/getAllMenuTree")
    public Result<List<MenuTreeVo>> getAllMenuTree() {
        return Result.success(menuManagementService.getAllMenuTree(TLUtil.get(TLUtil.USER_ID)));
    }

    @Operation(summary = "获取当前用户及其角色授权的菜单树")
    @GetMapping("/getCurrentUserMenuTree")
    public Result<List<UserMenuVo>> getCurrentUserMenuTree() {
        return Result.success(userMenuService.getUserMenuTree(TLUtil.get(TLUtil.USER_ID)));
    }
}

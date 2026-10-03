package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.vo.system.UserMenuVo;
import com.pivothub.system.service.UserMenuService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class SystemMenuController {
    @Autowired
    private UserMenuService userMenuService;

    @Operation(summary = "获取当前用户及其角色授权的菜单树")
    @GetMapping("/getCurrentUserMenuTree")
    public Result<List<UserMenuVo>> getCurrentUserMenuTree() {
        return Result.success(userMenuService.getUserMenuTree(TLUtil.get(TLUtil.USER_ID)));
    }
}

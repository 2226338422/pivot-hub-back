package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.dto.system.*;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.RoleVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.service.RoleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.pivothub.commoncore.constants.system.RoleManagementConstants.MANAGEMENT_MENU_CODE;

@RestController
@RequestMapping("/system/role")
@MenuPermission(MANAGEMENT_MENU_CODE)
@RequiredArgsConstructor
public class SystemRoleController {
    private final RoleManagementService roleService;

    @Operation(summary = "分页查询正常角色")
    @GetMapping("/page")
    public Result<PageVo<RoleVo>> page(RolePageDto query) {
        return Result.success(roleService.page(TLUtil.get(TLUtil.USER_ID), query));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/detail")
    public Result<RoleVo> detail(@RequestParam("roleId") String roleId) {
        return Result.success(roleService.detail(TLUtil.get(TLUtil.USER_ID), roleId));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/add")
    public Result<RoleVo> add(@Valid @RequestBody RoleCreateDto request) {
        return Result.success(roleService.add(TLUtil.get(TLUtil.USER_ID), request));
    }

    @Operation(summary = "修改角色信息")
    @PutMapping("/update")
    public Result<RoleVo> update(@Valid @RequestBody RoleUpdateDto request) {
        return Result.success(roleService.update(TLUtil.get(TLUtil.USER_ID), request));
    }

    @Operation(summary = "逻辑删除角色")
    @DeleteMapping("/delete")
    public Result<Object> delete(@RequestParam("roleId") String roleId) {
        roleService.delete(TLUtil.get(TLUtil.USER_ID), roleId);
        return Result.success(null);
    }

    @Operation(summary = "获取角色已授权菜单标识")
    @GetMapping("/getMenuIds")
    public Result<List<String>> getMenuIds(@RequestParam("roleId") String roleId) {
        return Result.success(roleService.getMenuIds(TLUtil.get(TLUtil.USER_ID), roleId));
    }

    @Operation(summary = "保存角色菜单权限")
    @PutMapping("/saveMenuPermissions")
    public Result<List<String>> saveMenuPermissions(@Valid @RequestBody RoleMenuPermissionDto request) {
        return Result.success(roleService.saveMenuPermissions(TLUtil.get(TLUtil.USER_ID), request));
    }
}

package com.pivothub.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pivothub.common.result.Result;
import com.pivothub.pojo.po.SysRole;
import com.pivothub.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    public Result<Page<SysRole>> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(roleService.page(new Page<>(page, size)));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result add(@RequestBody SysRole role) {
        roleService.save(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public Result update(@RequestBody SysRole role) {
        roleService.updateById(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success();
    }
}

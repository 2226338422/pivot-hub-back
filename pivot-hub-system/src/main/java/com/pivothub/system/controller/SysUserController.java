package com.pivothub.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pivothub.common.result.Result;
import com.pivothub.pojo.po.SysUser;
import com.pivothub.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/list")
    public Result<Page<SysUser>> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.page(new Page<>(page, size)));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result add(@RequestBody SysUser user) {
        userService.save(user);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public Result update(@RequestBody SysUser user) {
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}

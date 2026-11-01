package com.pivothub.system.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.common.result.Result;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.dto.system.RebindEmailDto;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserInfoVo;
import com.pivothub.system.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 个人中心相关接口（登录鉴权，不挂菜单权限）。 */
@RestController
@RequestMapping("/system/user")
public class SystemUserController {

    @Autowired
    private UserProfileService profileService;

    @GetMapping("/getUserInfo")
    public Result<UserInfoVo> getUserInfo() {
        return Result.success(profileService.getUserInfo(TLUtil.get(TLUtil.USER_ID)));
    }

    @PutMapping("/updateUserInfo")
    public Result<UserInfoVo> updateUserInfo(@Valid @RequestBody SysUser request) {
        request.setUuid(TLUtil.get(TLUtil.USER_ID));
        return Result.success(profileService.updateProfile(request));
    }

    @PutMapping("/rebindEmail")
    public Result<Object> rebindEmail(@Valid @RequestBody RebindEmailDto request) {
        profileService.rebindEmail(TLUtil.get(TLUtil.USER_ID), request.getEmail(), request.getCode());
        return Result.success("邮箱换绑成功");
    }

}

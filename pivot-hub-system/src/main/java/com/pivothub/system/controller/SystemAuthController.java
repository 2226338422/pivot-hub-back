package com.pivothub.system.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.EmailLoginDto;
import com.pivothub.pojo.dto.system.TokenDto;
import com.pivothub.pojo.vo.TokenVo;
import com.pivothub.system.service.SystemAuthService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/auth")
public class SystemAuthController {
    @Autowired
    private SystemAuthService authService;

    @PostMapping("/login/email")
    public Result<TokenVo> loginByEmail(@Valid @RequestBody EmailLoginDto dto) {
        return Result.success(authService.loginByEmail(dto));
    }

    @PostMapping("/refresh")
    public Result<TokenVo> refresh(@Validated(TokenDto.Refresh.class) @RequestBody TokenDto dto) {
        return Result.success(authService.refresh(dto));
    }
}

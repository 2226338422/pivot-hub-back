package com.pivothub.system.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.common.result.Result;
import com.pivothub.system.service.MailCodeService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/system/auth/mail")
public class SystemMailController {
    @Autowired
    private MailCodeService mailCodeService;

    @PostMapping("/code")
    public Result<Object> sendCode(@RequestParam("email") @Email(message = "邮箱格式不正确")
                                   @NotBlank(message = "邮箱不能为空") String email,
                                   @RequestParam(value = "scene", defaultValue = "login") String scene) {
        mailCodeService.sendCode(email, scene);
        return Result.success("验证码已发送");
    }
}

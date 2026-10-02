package com.pivothub.system.controller.feign;

import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.pojo.dto.system.feign.VerifyAccessDto;
import com.pivothub.system.service.AuthSessionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/feign/auth")
public class SystemAuthFeignController {
    @Autowired
    private AuthSessionService authSessionService;

    @Operation(summary = "校验当前端访问令牌登录记录",
            description = "仅校验Redis登录记录，调用方须先验证JWT签名、有效期及访问令牌用途")
    @PostMapping("/verifyAccess")
    public Boolean verifyAccess(@Valid @RequestBody VerifyAccessDto dto) {
        return authSessionService.verifyAccess(dto.getUserId(),
                ClientType.fromValue(dto.getClientType()), dto.getAccessToken());
    }
}

package com.pivothub.system.controller.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.pojo.dto.system.feign.CheckMenuAccessDto;
import com.pivothub.system.service.SystemMenuAccessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 内部服务菜单访问资格查询控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@RequestMapping("/system/feign/menu")
public class SystemMenuFeignController {
    @Autowired
    private SystemMenuAccessService menuAccessService;

    @Operation(summary = "内部服务查询用户菜单访问资格")
    @PostMapping("/checkAccess")
    public Boolean checkAccess(@Valid @RequestBody CheckMenuAccessDto dto) {
        return menuAccessService.checkAccess(dto.getUserId(), dto.getMenuCode());
    }
}

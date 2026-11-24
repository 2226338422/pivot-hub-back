package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.SystemConfigSaveDto;
import com.pivothub.pojo.vo.system.SystemConfigVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.pivothub.commoncore.constants.system.SystemSettingsConstants.SETTINGS_MENU_CODE;

@RestController
@RequestMapping("/system/settings")
@MenuPermission(SETTINGS_MENU_CODE)
public class SystemSettingsController {
    @Autowired
    private SystemConfigService configService;

    @Operation(summary = "查询当前用户有权查看的系统设置")
    @GetMapping
    public Result<List<SystemConfigVo>> list() {
        return Result.success(configService.listAuthorizedConfigs());
    }

    @Operation(summary = "批量保存系统设置")
    @PutMapping
    public Result<Object> save(@Valid @RequestBody SystemConfigSaveDto dto) {
        configService.saveConfigs(dto.getItems());
        return Result.success();
    }
}

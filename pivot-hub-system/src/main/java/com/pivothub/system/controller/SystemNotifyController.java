package com.pivothub.system.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.notify.NotifySettingDto;
import com.pivothub.pojo.vo.system.notify.NotifySettingsVo;
import com.pivothub.system.service.SystemNotifySettingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 用户通知设置（登录鉴权，不挂菜单权限）。 */
@RestController
@RequestMapping("/system/notify")
public class SystemNotifyController {

    @Autowired
    private SystemNotifySettingService notifySettingService;

    @Operation(summary = "查询通知设置")
    @GetMapping("/settings")
    public Result<NotifySettingsVo> settings() {
        return Result.success(notifySettingService.getSettings());
    }

    @Operation(summary = "保存通知设置")
    @PutMapping("/settings")
    public Result<Object> update(@Valid @RequestBody NotifySettingDto dto) {
        notifySettingService.updateSettings(dto);
        return Result.success();
    }
}

package com.pivothub.system.notify.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.notify.MessageSubscriptionSaveDto;
import com.pivothub.pojo.vo.system.notify.MessageSubscriptionVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.notify.service.MessageSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 当前用户的消息订阅管理。 */
@RestController
@RequestMapping("/system/notify/subscriptions")
@MenuPermission("message_subscription")
public class MessageSubscriptionController {
    @Autowired
    private MessageSubscriptionService messageSubscriptionService;

    @Operation(summary = "查询当前用户的消息订阅")
    @GetMapping
    public Result<List<MessageSubscriptionVo>> list() {
        return Result.success(messageSubscriptionService.list());
    }

    @Operation(summary = "保存当前用户的消息订阅开关")
    @PutMapping
    public Result<Object> save(@Valid @RequestBody MessageSubscriptionSaveDto request) {
        messageSubscriptionService.save(request);
        return Result.success();
    }
}

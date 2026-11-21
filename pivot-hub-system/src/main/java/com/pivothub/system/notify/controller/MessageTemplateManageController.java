package com.pivothub.system.notify.controller;

import com.pivothub.common.result.Result;
import com.pivothub.pojo.dto.system.notify.MessageTemplatePageDto;
import com.pivothub.pojo.dto.system.notify.MessageTemplateSaveDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.notify.MessageTemplateManageVo;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.notify.service.MessageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 消息模板管理页面的接口。 */
@RestController
@RequestMapping("/system/notify/templates")
@MenuPermission("message_template")
public class MessageTemplateManageController {
    @Autowired
    private MessageTemplateService messageTemplateService;

    /** 分页查询消息模板。 */
    @Operation(summary = "分页查询消息模板")
    @GetMapping
    public Result<PageVo<MessageTemplateManageVo>> page(MessageTemplatePageDto query) {
        return Result.success(messageTemplateService.page(query));
    }

    /** 按 UUID 查询完整模板。 */
    @Operation(summary = "查询消息模板详情")
    @GetMapping("/{uuid}")
    public Result<MessageTemplateManageVo> detail(@PathVariable("uuid") String uuid) {
        return Result.success(messageTemplateService.detail(uuid));
    }

    /** 保存指定消息模板的正文和明细模板。 */
    @Operation(summary = "保存消息模板内容")
    @PutMapping
    public Result<Object> update(@Valid @RequestBody MessageTemplateSaveDto request) {
        messageTemplateService.update(request);
        return Result.success();
    }
}

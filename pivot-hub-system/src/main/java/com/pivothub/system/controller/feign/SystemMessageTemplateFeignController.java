package com.pivothub.system.controller.feign;

import com.pivothub.pojo.dto.system.notify.MessageTemplateQueryDto;
import com.pivothub.pojo.vo.system.notify.MessageTemplateVo;
import com.pivothub.system.notify.service.MessageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 内部服务消息模板查询。 */
@RestController
@RequestMapping("/system/feign/notify/template")
public class SystemMessageTemplateFeignController {
    @Autowired
    private MessageTemplateService messageTemplateService;

    /** 按消息类型返回模板。 */
    @Operation(summary = "内部服务查询消息模板")
    @PostMapping("/get")
    public MessageTemplateVo get(@Valid @RequestBody MessageTemplateQueryDto query) {
        return messageTemplateService.find(query.getMessageType());
    }
}

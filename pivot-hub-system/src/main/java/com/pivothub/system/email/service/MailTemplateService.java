package com.pivothub.system.email.service;

import com.pivothub.pojo.dto.system.email.MailTemplateContentDto;

import java.util.Map;

/** 加载并渲染邮件模板。 */
public interface MailTemplateService {
    /** 使用具名参数渲染指定邮件模板。 */
    MailTemplateContentDto render(String templateCode, Map<String, ?> variables);
}

package com.pivothub.system.email.service;

import java.util.List;
import java.util.Map;

/** 邮件发送服务。 */
public interface EmailService {
    /** 按用户 UUID 批量密送，返回各用户的提交结果，无可用邮箱的用户不返回。 */
    Map<String, Boolean> sendBatchEmail(String subject, String content, List<String> userIds);
}

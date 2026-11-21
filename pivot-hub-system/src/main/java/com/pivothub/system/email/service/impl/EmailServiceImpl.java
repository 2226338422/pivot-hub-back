package com.pivothub.system.email.service.impl;

import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.system.email.service.EmailService;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.util.EmailApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 查询用户邮箱并密送同一封邮件。 */
@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EmailApiUtil emailApiUtil;

    @Override
    public Map<String, Boolean> sendBatchEmail(String subject, String content, List<String> userIds) {
        List<String> validUserIds = userIds == null ? List.of()
                : userIds.stream().filter(StringUtils::hasText).distinct().toList();
        if (validUserIds.isEmpty()) {
            return Map.of();
        }

        List<SysUser> recipients = sysUserMapper.selectEmailsByUserIds(validUserIds).stream()
                .filter(user -> StringUtils.hasText(user.getEmail()))
                .toList();
        if (recipients.isEmpty()) {
            return Map.of();
        }

        List<String> emails = recipients.stream().map(user -> user.getEmail().trim()).distinct().toList();
        Set<String> acceptedEmails = Set.of();
        try {
            acceptedEmails = emailApiUtil.sendBccEmail(subject, content, emails);
        } catch (RuntimeException failure) {
            LOG.warn("密送失败 recipientCount={} errorType={}", emails.size(), failure.getClass().getSimpleName());
        }

        Map<String, Boolean> results = new HashMap<>();
        for (SysUser recipient : recipients) {
            results.put(recipient.getUuid(), acceptedEmails.contains(recipient.getEmail().trim()));
        }
        return results;
    }
}

package com.pivothub.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.commoncore.constants.redis.SystemRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import com.pivothub.system.util.EmailApiUtil;
import com.pivothub.system.mapper.SysMailCodeMapper;
import com.pivothub.system.service.MailCodeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class MailCodeServiceImpl implements MailCodeService {
    @Autowired
    private SysMailCodeMapper mapper;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private EmailApiUtil emailApiUtil;

    @Override
    public void sendCode(String email, String scene) {
        if (!StringUtils.hasText(email) || !Set.of("login", "register", "reset").contains(scene)) {
            throw new IllegalArgumentException("邮箱或验证码场景无效");
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String rateKey = SystemRedisConstants.MAIL_CODE_RATE_LIMIT_KEY_PREFIX + normalizedEmail;
        Long sendCount = cacheUtil.increment(rateKey);
        if (Long.valueOf(1L).equals(sendCount)) {
            cacheUtil.expire(rateKey, 10L, TimeUnit.MINUTES);
        }
        if (sendCount != null && sendCount > 5) {
            throw new IllegalArgumentException("验证码发送过于频繁");
        }
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        Date now = new Date();
        com.pivothub.pojo.po.system.SysMailCode mailCode = new com.pivothub.pojo.po.system.SysMailCode();
        mailCode.setUuid(IdWorker.getIdStr());
        mailCode.setEmail(normalizedEmail);
        mailCode.setCode(code);
        mailCode.setScene(scene);
        mailCode.setUsed(0);
        mailCode.setExpireTime(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(SystemRedisConstants.MAIL_CODE_TTL)));
        mailCode.setCreateTime(now);
        mailCode.setUpdateTime(now);
        if (mapper.insert(mailCode) != 1) {
            throw new IllegalStateException("验证码保存失败");
        }
        try {
            if (!emailApiUtil.sendEmailCode(code, SystemRedisConstants.MAIL_CODE_TTL, normalizedEmail)) {
                throw new IllegalStateException("验证码邮件发送失败");
            }
        } catch (Exception ex) {
            mapper.markUsed(mailCode.getUuid());
            throw new IllegalStateException("验证码邮件发送失败", ex);
        }
    }

    @Override
    public boolean verifyAndConsume(String email, String scene, String code) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(scene) || !StringUtils.hasText(code)) {
            return false;
        }
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        var mailCode = mapper.selectUsable(normalizedEmail, scene.trim(), new Date());
        if (mailCode == null || !code.trim().equals(mailCode.getCode())) {
            return false;
        }
        if (mailCode.getExpireTime() != null && mailCode.getExpireTime().before(new Date())) {
            return false;
        }
        return mapper.markUsed(mailCode.getUuid()) == 1;
    }
}

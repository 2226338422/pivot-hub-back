package com.pivothub.system.email.service.impl;

import com.pivothub.common.exception.SystemException;
import com.pivothub.commoncore.constants.redis.SystemRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import com.pivothub.pojo.po.system.SysMailTemplate;
import com.pivothub.pojo.dto.system.email.MailTemplateContentDto;
import com.pivothub.system.email.mapper.SysMailTemplateMapper;
import com.pivothub.system.email.service.MailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 从 Redis 或数据库加载邮件模板并严格渲染具名占位符。 */
@Service
public class MailTemplateServiceImpl implements MailTemplateService {
    private static final Logger LOG = LoggerFactory.getLogger(MailTemplateServiceImpl.class);
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z_][A-Za-z0-9_]*)}");

    @Autowired
    private SysMailTemplateMapper sysMailTemplateMapper;
    @Autowired
    private CacheUtil cacheUtil;

    @Override
    public MailTemplateContentDto render(String templateCode, Map<String, ?> variables) {
        if (!StringUtils.hasText(templateCode) || variables == null) {
            throw new SystemException(400, "邮件模板参数无效");
        }
        MailTemplateContentDto template = load(templateCode.trim());

        MailTemplateContentDto result = new MailTemplateContentDto();
        result.setSubject(renderPart(template.getSubject(), variables, false));
        result.setBody(renderPart(template.getBody(), variables, true));
        return result;
    }

    private MailTemplateContentDto load(String templateCode) {
        String cacheKey = SystemRedisConstants.MAIL_TEMPLATE_KEY_PREFIX + templateCode;
        MailTemplateContentDto content = null;
        boolean cacheMiss = false;
        try {
            content = cacheUtil.getObject(cacheKey, MailTemplateContentDto.class);
            cacheMiss = content == null;
        } catch (RuntimeException failure) {
            cacheMiss = true;
            LOG.warn("读取邮件模板缓存失败 templateCode={} errorType={}",
                    templateCode, failure.getClass().getSimpleName());
        }

        if (cacheMiss) {
            SysMailTemplate template = sysMailTemplateMapper.selectEnabledByCode(templateCode);
            if (template == null) {
                throw new SystemException(503, "邮件模板不可用");
            }
            content = new MailTemplateContentDto();
            content.setSubject(template.getSubjectTemplate());
            content.setBody(template.getBodyTemplate());
        }

        if (content == null || !StringUtils.hasText(content.getSubject())
                || !StringUtils.hasText(content.getBody()) || !content.getBody().contains("{code}")) {
            throw new SystemException(503, "邮件模板内容无效");
        }
        if (cacheMiss) {
            try {
                cacheUtil.setObject(cacheKey, content,
                        SystemRedisConstants.MAIL_TEMPLATE_TTL_HOURS, TimeUnit.HOURS);
            } catch (RuntimeException failure) {
                LOG.warn("写入邮件模板缓存失败 templateCode={} errorType={}",
                        templateCode, failure.getClass().getSimpleName());
            }
        }
        return content;
    }

    private String renderPart(String template, Map<String, ?> variables, boolean escapeHtml) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            Object value = variables.get(name);
            if (value == null) {
                throw new SystemException(500, "缺少邮件模板参数：" + name);
            }
            String replacement = value.toString();
            if (escapeHtml) {
                replacement = HtmlUtils.htmlEscape(replacement);
            }
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }
}

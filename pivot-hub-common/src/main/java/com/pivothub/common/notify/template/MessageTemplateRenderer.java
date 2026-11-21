package com.pivothub.common.notify.template;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 将业务数据填入通知模板的占位符。 */
public final class MessageTemplateRenderer {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z_][A-Za-z0-9_]*)\\}");

    private MessageTemplateRenderer() {
    }

    /** 替换形如 {name} 的占位符，缺少任何值时报告对应的占位符。 */
    public static String render(String template, Map<String, ?> values) {
        Objects.requireNonNull(template, "消息模板不能为空");
        Objects.requireNonNull(values, "消息模板参数不能为空");

        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            Object value = values.get(name);
            if (value == null) {
                throw new IllegalArgumentException("缺少消息模板参数：" + name);
            }
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(value.toString()));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }
}

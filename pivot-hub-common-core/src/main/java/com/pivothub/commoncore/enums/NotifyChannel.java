package com.pivothub.commoncore.enums;

import java.util.Arrays;

/** 通知渠道。 */
public enum NotifyChannel implements ValueEnum<String> {
    INBOX("inbox", "站内信"),
    EMAIL("email", "邮箱"),
    WXPUSHER("wxpusher", "WxPusher 消息推送平台");

    private final String value;
    private final String name;

    NotifyChannel(String value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    public static NotifyChannel fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("通知渠道不能为空");
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的通知渠道：" + value));
    }
}

package com.pivothub.commoncore.enums;

import java.util.Arrays;

/** 可订阅的业务消息类型。 */
public enum NotifyMessageType implements ValueEnum<String> {
    ANNOUNCEMENT("public_push", "公共推送"),
    TYPHOON("typhoon_forecast", "台风预测");

    private final String value;
    private final String name;

    NotifyMessageType(String value, String name) {
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

    /** 按消息类型编码查找，未知编码返回 null。 */
    public static NotifyMessageType findByValue(String value) {
        return Arrays.stream(values()).filter(type -> type.value.equals(value)).findFirst().orElse(null);
    }

    /** 判断消息类型是否受支持。 */
    public static boolean supports(String value) {
        return findByValue(value) != null;
    }
}

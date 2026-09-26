package com.pivothub.commoncore.enums;

import java.util.Arrays;

/** 登录客户端类型。 */
public enum ClientType implements ValueEnum<Integer> {
    WEB(1, "web"),
    APP(2, "app");

    private final Integer value;
    private final String name;

    ClientType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    public static ClientType fromValue(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("客户端类型不能为空");
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的客户端类型：" + value));
    }
}

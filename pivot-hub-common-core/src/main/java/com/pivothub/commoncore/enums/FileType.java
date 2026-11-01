package com.pivothub.commoncore.enums;

import java.util.Arrays;

/** 文件类型。 */
public enum FileType implements ValueEnum<String> {
    AVATAR("avatar", "用户头像"),
    DEFAULT_AVATAR("default_avatar", "默认头像");

    private final String value;
    private final String name;

    FileType(String value, String name) {
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

    public static FileType fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的文件类型：" + value));
    }
}

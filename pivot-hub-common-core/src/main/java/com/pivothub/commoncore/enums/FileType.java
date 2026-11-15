package com.pivothub.commoncore.enums;

import java.util.Arrays;

/** 文件类型。 */
public enum FileType implements ValueEnum<String>, ValueTwoEnum<String> {
    AVATAR("avatar", "用户头像", "Avatar"),
    DEFAULT_AVATAR("default_avatar", "默认头像", "Avatar"),
    ANNOUNCEMENT_FILE("announcement_file", "公告附件", "Announcement");

    private final String value;
    private final String name;
    private final String module;

    FileType(String value, String name, String module) {
        this.value = value;
        this.name = name;
        this.module = module;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFirstName() {
        return name;
    }

    @Override
    public String getSecondName() {
        return module;
    }

    public String getModule() {
        return module;
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

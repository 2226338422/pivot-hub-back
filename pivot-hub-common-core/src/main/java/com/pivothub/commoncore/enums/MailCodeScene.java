package com.pivothub.commoncore.enums;

import java.util.Arrays;

/** 邮箱验证码使用场景。 */
public enum MailCodeScene implements ValueEnum<String> {
    LOGIN("login", "登录"),
    REGISTER("register", "注册"),
    RESET("reset", "重置密码"),
    REBIND("rebind", "重新绑定邮箱");

    private final String value;
    private final String name;

    MailCodeScene(String value, String name) {
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

    /** 按场景编码查找，未知编码返回 null。 */
    public static MailCodeScene findByValue(String value) {
        return Arrays.stream(values()).filter(scene -> scene.value.equals(value)).findFirst().orElse(null);
    }
}

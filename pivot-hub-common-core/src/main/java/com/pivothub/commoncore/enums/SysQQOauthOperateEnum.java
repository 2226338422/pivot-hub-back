package com.pivothub.commoncore.enums;

/**
 * QQ OAuth操作类型枚举
 */
public enum SysQQOauthOperateEnum implements ValueEnum<Integer> {
    LOGIN(1, "登录"),
    BIND(2, "绑定"),
    UNBIND(3, "解绑");

    private final Integer value;
    private final String name;

    SysQQOauthOperateEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static boolean isMember(Integer value) {
        for (SysQQOauthOperateEnum e : values()) {
            if (e.getValue().equals(value)) return true;
        }
        return false;
    }

    @Override
    public Integer getValue() { return value; }
    @Override
    public String getName() { return name; }
}

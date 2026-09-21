package com.pivothub.commoncore.enums;

/**
 * 权限归属类型枚举
 */
public enum SysPermissionPersonTypeEnum implements ValueEnum<Integer> {
    ROLE(1, "角色"),
    USER(2, "用户");

    private final Integer value;
    private final String name;

    SysPermissionPersonTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static boolean isMember(Integer value) {
        for (SysPermissionPersonTypeEnum e : values()) {
            if (e.getValue().equals(value)) return true;
        }
        return false;
    }

    @Override
    public Integer getValue() { return value; }
    @Override
    public String getName() { return name; }
}

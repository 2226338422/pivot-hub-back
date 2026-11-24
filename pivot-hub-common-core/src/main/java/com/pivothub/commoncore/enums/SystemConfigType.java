package com.pivothub.commoncore.enums;

/** 系统设置的表单类型。 */
public enum SystemConfigType implements ValueEnum<Integer> {
    TEXT(1, "文本"),
    SWITCH(2, "开关"),
    INTEGER(3, "整数"),
    DECIMAL(4, "小数"),
    SINGLE_SELECT(5, "单选"),
    MULTI_SELECT(6, "多选");

    private final Integer value;
    private final String name;

    SystemConfigType(Integer value, String name) {
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

    public static SystemConfigType findByValue(Integer value) {
        for (SystemConfigType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}

package com.pivothub.commoncore.enums;

public enum MallRoleEnum implements ValueEnum<Integer> {
    CUSTOMER(1, "消费者"),
    MERCHANT(2, "商家"),
    MANAGER(3, "管理员");

    private final Integer value;
    private final String name;

    MallRoleEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() { return value; }
    @Override
    public String getName() { return name; }
}

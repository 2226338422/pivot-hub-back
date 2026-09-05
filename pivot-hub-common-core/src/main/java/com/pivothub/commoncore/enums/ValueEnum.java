package com.pivothub.commoncore.enums;

/**
 * @InterfaceName: ValueEnum
 * @Description: 枚举行为规范接口
 */
public interface ValueEnum<V> {
    V getValue();
    String getName();
}

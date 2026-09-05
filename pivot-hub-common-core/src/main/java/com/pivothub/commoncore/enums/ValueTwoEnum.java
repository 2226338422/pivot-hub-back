package com.pivothub.commoncore.enums;

/**
 * @InterfaceName: ValueTwoEnum
 * @Description: 双值枚举行为规范接口
 */
public interface ValueTwoEnum<V> {
    V getValue();
    String getFirstName();
    String getSecondName();
}

package com.pivothub.commoncore.constants.redis;

/**
 * @ClassName: BusinessRedisConstants
 * @Description: 业务服务Redis key常量
 * @Author: lhb
 * @Date: 2026/10/7
 */

public class BusinessRedisConstants {
    public static final String KEY_BUSINESS_UNITE_PREFIX = "pivot:business:";

    /** 高德请求限流计数key前缀，后接用户标识:自然分钟窗口。 */
    public static final String MAP_AMAP_RATE_LIMIT_KEY_PREFIX = KEY_BUSINESS_UNITE_PREFIX + "map:amap:rate:";
}

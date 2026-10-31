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

    public static final String MAP_TYPHOON_CACHE_PREFIX = KEY_BUSINESS_UNITE_PREFIX + "map:typhoon:nmc:v1:";
    public static final String MAP_TYPHOON_LIST_CACHE_KEY = MAP_TYPHOON_CACHE_PREFIX + "list";
    public static final String MAP_TYPHOON_DETAIL_CACHE_KEY_PREFIX = MAP_TYPHOON_CACHE_PREFIX + "detail:";
    public static final String MAP_TYPHOON_LOCK_SUFFIX = ":rlock";
    public static final String MAP_TYPHOON_BACKOFF_SUFFIX = ":backoff";
}

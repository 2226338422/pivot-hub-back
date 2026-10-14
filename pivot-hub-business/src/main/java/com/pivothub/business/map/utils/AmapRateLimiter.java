package com.pivothub.business.map.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.map.config.AmapProperties;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.commoncore.constants.redis.BusinessRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 高德请求频率限制工具，通过CacheUtil按用户及自然分钟统计服务调用次数
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@Component
public class AmapRateLimiter {
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private AmapProperties properties;

    public void acquire(String operatorId) {
        if (operatorId == null || operatorId.isBlank()) throw new BusinessException(500, "用户身份上下文不可用");
        int limit = Math.min(properties.getProxy().getRequestsPerMinute(), 120);
        if (limit <= 0) throw new BusinessException(503, "地图服务配置不可用");
        Long count;
        try {
            String key = BusinessRedisConstants.MAP_AMAP_RATE_LIMIT_KEY_PREFIX + operatorId + ":"
                    + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
            count = cacheUtil.increment(key);
            if (Long.valueOf(1L).equals(count)) {
                cacheUtil.expire(key, 2L, TimeUnit.MINUTES);
            }
        } catch (Exception unavailable) {
            throw new BusinessException(503, "地图服务暂不可用");
        }
        if (count == null) throw new BusinessException(503, "地图服务暂不可用");
        if (count > limit) throw new BusinessException(429, "地图请求过于频繁，请稍后再试");
    }
}

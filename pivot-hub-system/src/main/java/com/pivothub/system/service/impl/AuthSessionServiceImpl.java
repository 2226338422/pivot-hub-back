package com.pivothub.system.service.impl;

import com.pivothub.commoncore.config.AuthConfig;
import com.pivothub.commoncore.constants.redis.SystemRedisConstants;
import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.commoncore.util.TokenHashUtil;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.dto.redis.system.TokenRedisDto;
import com.pivothub.system.service.AuthSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthSessionServiceImpl implements AuthSessionService {
    private static final String ACCESS_HASH = "accessTokenHash";
    private static final String REFRESH_HASH = "refreshTokenHash";
    private static final String USER_ID = "userId";
    private static final String CLIENT_TYPE = "clientType";
    private static final String ACCESS_EXPIRES_AT = "accessExpiresAt";
    private static final String REFRESH_EXPIRES_AT = "refreshExpiresAt";
    private static final String USER_STATUS = "userStatus";
    private static final String UPDATED_AT = "updatedAt";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AuthConfig authConfig;

    @Override
    public void replaceLogin(String userId, ClientType clientType, String accessToken,
                              String refreshToken, SysUser user) {
        String key = SystemRedisConstants.AUTH_SESSION_KEY_PREFIX + clientType.getName() + ":" + userId;
        redisTemplate.opsForHash().putAll(key, sessionValues(userId, clientType, accessToken, refreshToken, user));
        redisTemplate.expire(key, Duration.ofMillis(requiredRefreshTtl()));
    }

    @Override
    public boolean verifyAccess(String userId, ClientType clientType, String accessToken) {
        if (userId == null || clientType == null || accessToken == null) {
            return false;
        }
        Map<Object, Object> values = redisTemplate.opsForHash().entries(SystemRedisConstants.AUTH_SESSION_KEY_PREFIX + clientType.getName() + ":" + userId);
        if (values.isEmpty() || !TokenHashUtil.sha256(accessToken).equals(values.get(ACCESS_HASH))) {
            return false;
        }
        Integer status = parseInteger(values.get(USER_STATUS));
        return status == null || status == 1;
    }

    @Override
    public boolean verifyRefresh(String userId, ClientType clientType, String refreshToken) {
        Object current = redisTemplate.opsForHash().get(SystemRedisConstants.AUTH_SESSION_KEY_PREFIX + clientType.getName() + ":" + userId, REFRESH_HASH);
        return current != null && TokenHashUtil.sha256(refreshToken).equals(current.toString());
    }

    @Override
    public boolean rotateRefresh(String userId, ClientType clientType, String oldRefreshToken,
                                 TokenRedisDto newPair, SysUser user) {
        String key = SystemRedisConstants.AUTH_SESSION_KEY_PREFIX + clientType.getName() + ":" + userId;
        Map<String, String> values = sessionValues(userId, clientType,
                newPair.getAccessToken(), newPair.getRefreshToken(), user);
        String oldHash = TokenHashUtil.sha256(oldRefreshToken);
        SessionCallback<List<Object>> callback = new SessionCallback<>() {
            @Override
            @SuppressWarnings("unchecked")
            public <K, V> List<Object> execute(RedisOperations<K, V> operations) {
                RedisOperations<String, String> stringOperations =
                        (RedisOperations<String, String>) (RedisOperations<?, ?>) operations;
                stringOperations.watch(key);
                Object current = stringOperations.opsForHash().get(key, REFRESH_HASH);
                if (current == null || !oldHash.equals(current.toString())) {
                    stringOperations.unwatch();
                    return null;
                }
                stringOperations.multi();
                stringOperations.opsForHash().putAll(key, values);
                stringOperations.expire(key, requiredRefreshTtl(), TimeUnit.MILLISECONDS);
                return stringOperations.exec();
            }
        };
        List<Object> result = redisTemplate.execute(callback);
        return result != null && !result.isEmpty();
    }

    private Map<String, String> sessionValues(String userId, ClientType clientType,
                                               String accessToken, String refreshToken, SysUser user) {
        long now = System.currentTimeMillis();
        Map<String, String> values = new HashMap<>();
        values.put(ACCESS_HASH, TokenHashUtil.sha256(accessToken));
        values.put(REFRESH_HASH, TokenHashUtil.sha256(refreshToken));
        values.put(USER_ID, userId);
        values.put(CLIENT_TYPE, String.valueOf(clientType.getValue()));
        values.put(ACCESS_EXPIRES_AT, String.valueOf(now + requiredAccessTtl()));
        values.put(REFRESH_EXPIRES_AT, String.valueOf(now + requiredRefreshTtl()));
        values.put(USER_STATUS, String.valueOf(user.getStatus() == null ? 1 : user.getStatus()));
        values.put(UPDATED_AT, String.valueOf(now));
        return values;
    }


    private long requiredAccessTtl() {
        Long accessTtlMillis = authConfig.getJwtAccessTime();
        if (accessTtlMillis == null || accessTtlMillis <= 0) {
            throw new IllegalStateException("访问令牌有效期未配置");
        }
        return accessTtlMillis;
    }

    private long requiredRefreshTtl() {
        Long refreshTtlMillis = authConfig.getJwtRefreshTime();
        if (refreshTtlMillis == null || refreshTtlMillis <= 0) {
            throw new IllegalStateException("刷新令牌有效期未配置");
        }
        return refreshTtlMillis;
    }

    private Integer parseInteger(Object value) {
        return value == null ? null : Integer.valueOf(value.toString());
    }
}

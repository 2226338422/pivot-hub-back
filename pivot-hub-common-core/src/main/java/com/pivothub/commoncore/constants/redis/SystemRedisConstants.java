package com.pivothub.commoncore.constants.redis;

/** system 认证、用户资料和邮箱验证码 Redis key 常量。 */
public final class SystemRedisConstants {
    private SystemRedisConstants() {
    }

    public static final String KEY_SYSTEM_UNITE_PREFIX = "pivot:system:";
    /** Web/App 登录会话 key 前缀，后接 clientType:userId。 */
    public static final String AUTH_SESSION_KEY_PREFIX = KEY_SYSTEM_UNITE_PREFIX + "auth:session:";
    /** 不区分 Web/App 的用户资料 key 前缀，后接 userId。 */
    public static final String USER_PROFILE_KEY_PREFIX = KEY_SYSTEM_UNITE_PREFIX + "user:";
    /** 邮箱验证码辅助 key 前缀，后接 scene:email。 */
    public static final String MAIL_CODE_KEY_PREFIX = KEY_SYSTEM_UNITE_PREFIX + "mail:code:";
    /** 邮箱验证码发送限流 key 前缀，后接 email。 */
    public static final String MAIL_CODE_RATE_LIMIT_KEY_PREFIX = KEY_SYSTEM_UNITE_PREFIX + "mail:rate:";
    public static final Long MAIL_CODE_TTL = 5L;
}

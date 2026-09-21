package com.pivothub.commoncore.constants.redis;

/**
 * System模块Redis常量
 */
public class SystemRedisConstants {

    public static final String KEY_SYSTEM_UNITE_PREFIX = "pivot:web:system:";

    /** QQ OAuth state key前缀 */
    public static final String QQ_OAUTH_STATE_KEY = KEY_SYSTEM_UNITE_PREFIX + "oauth:qq:state:";
    public static final Long QQ_OAUTH_STATE_TTL = 5L;

    /** QQ OAuth结果key前缀 */
    public static final String QQ_CODE_RESULT_KEY = KEY_SYSTEM_UNITE_PREFIX + "oauth:qq:result:";
    public static final Long QQ_CODE_RESULT_TTL = 3L;

    /** QQ绑定code对应userId的key前缀 */
    public static final String QQ_BIND_CODE_USERID_KEY = KEY_SYSTEM_UNITE_PREFIX + "oauth:qq:bind:";
    public static final Long QQ_BIND_CODE_USERID_TTL = 1L;

    /** 邮箱验证码key前缀 */
    public static final String MAIL_CODE_KEY = KEY_SYSTEM_UNITE_PREFIX + "mail:code:";
    public static final Long MAIL_CODE_TTL = 5L;

    /** 用户菜单缓存key前缀 */
    public static final String USER_MENU_KEY = KEY_SYSTEM_UNITE_PREFIX + "user:";
    public static final Long USER_MENU_TTL = 24L;

    /** 角色菜单缓存key前缀 */
    public static final String ROLE_MENU_KEY = KEY_SYSTEM_UNITE_PREFIX + "role:";
    public static final Long ROLE_MENU_TTL = 24L;
}

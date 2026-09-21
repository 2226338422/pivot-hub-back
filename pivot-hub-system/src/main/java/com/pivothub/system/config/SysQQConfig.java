package com.pivothub.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * QQ OAuth配置
 */
@Component
@ConfigurationProperties(prefix = "qq")
@Data
public class SysQQConfig {
    /** OAuth回调基础URL */
    private String oauthHttp;
    /** QQ应用AppID */
    private String appid;
    /** QQ应用AppKey */
    private String appkey;
}

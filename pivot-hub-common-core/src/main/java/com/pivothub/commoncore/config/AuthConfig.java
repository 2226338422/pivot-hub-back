package com.pivothub.commoncore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: AuthConfig
 * @Description: 认证配置
 * @Author: lhb
 */
@Component
@ConfigurationProperties(prefix = "author")
@Data
public class AuthConfig {
    private String phoneSecret;
    private String passwordSecret;
    private String mailSecret;
    private Long jwtAccessTime;
    private Long jwtRefreshTime;
    private String jwtRefreshSecret;
    private String jwtAccessSecret;
    private String jwtTemporarySecret;
    private Long jwtTemporaryTime;
    private String jwtIdSecret;
}

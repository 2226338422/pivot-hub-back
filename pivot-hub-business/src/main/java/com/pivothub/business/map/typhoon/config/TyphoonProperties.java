package com.pivothub.business.map.typhoon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 台风网站数据入口、超时和缓存配置，不保存台风业务数据。 */
@Data
@Component
@ConfigurationProperties(prefix = "typhoon")
public class TyphoonProperties {
    private boolean enabled;
    private String baseUrl;
    private int timeoutSeconds;
    private int cacheSeconds;
    private int staleSeconds;
    private int retrySeconds;
}

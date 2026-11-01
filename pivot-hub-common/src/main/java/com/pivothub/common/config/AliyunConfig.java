package com.pivothub.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 阿里云 OSS 配置：共用 AK/SK/endpoint/region，区分公共读/私有 bucket。 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliyunConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String region;
    /** 公共读 bucket */
    private String publicBucket;
    /** 私有 bucket */
    private String privateBucket;
    /** 私有 URL 有效期（秒），默认 6 小时 */
    private Long privateUrlTtlSeconds = 21600L;
}

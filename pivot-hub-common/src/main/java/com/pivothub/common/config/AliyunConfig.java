package com.pivothub.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 复用 Nacos 已有 aliyun 配置，不输出访问凭证。 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliyunConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String roleArn;
    private String endpoint;
    private String bucketName;
    private String region;
}

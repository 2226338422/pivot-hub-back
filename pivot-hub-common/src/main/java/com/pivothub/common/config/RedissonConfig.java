package com.pivothub.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: RedissonConfig
 * @Description: Redisson配置类
 * @Author: lhb
 */

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private String port;

    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setPassword(password.isEmpty() ? null : password)
                .setAddress(host.contains("://") ? "" : "redis://" + host + ":" + port);
        return Redisson.create(config);
    }
}

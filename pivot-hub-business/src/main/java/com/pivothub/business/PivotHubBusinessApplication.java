package com.pivothub.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {"com.pivothub.business", "com.pivothub.common", "com.pivothub.commoncore"}
)
@EnableScheduling
@EnableFeignClients(basePackages = "com.pivothub.common.feign")
@MapperScan({"com.pivothub.common.mapper", "com.pivothub.business.mapper"})
public class PivotHubBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(PivotHubBusinessApplication.class, args);
    }
}

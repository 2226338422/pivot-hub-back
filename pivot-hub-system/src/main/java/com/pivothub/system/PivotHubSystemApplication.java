package com.pivothub.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {"com.pivothub.system", "com.pivothub.common", "com.pivothub.commoncore"}
)
@EnableScheduling
@EnableFeignClients(basePackages = "com.pivothub.common.feign")
@MapperScan(basePackages = "com.pivothub.system", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class PivotHubSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PivotHubSystemApplication.class, args);
    }
}

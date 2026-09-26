package com.pivothub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.pivothub.gateway", "com.pivothub.commoncore"}
)
public class PivotHubGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PivotHubGatewayApplication.class, args);
    }
}

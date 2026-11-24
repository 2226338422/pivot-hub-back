package com.pivothub.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pivot-hub-system", contextId = "systemConfigClient", path = "/system/feign/config")
public interface SystemConfigClient {
    @GetMapping("/value")
    String getConfigValue(@RequestParam("key") String key);
}

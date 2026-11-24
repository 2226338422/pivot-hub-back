package com.pivothub.system.controller.feign;

import com.pivothub.system.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/feign/config")
public class SystemConfigFeignController {
    @Autowired
    private SystemConfigService configService;

    @GetMapping("/value")
    public String getConfigValue(@RequestParam("key") String key) {
        return configService.getConfigValue(key);
    }
}

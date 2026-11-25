package com.pivothub.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "pivot-hub-business", contextId = "businessScheduledTaskClient", path = "/business/feign/scheduled-tasks")
public interface BusinessScheduledTaskClient {
    @PostMapping("/run")
    int run(@RequestParam("taskCode") String taskCode);
}

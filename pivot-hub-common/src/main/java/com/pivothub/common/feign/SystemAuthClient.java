package com.pivothub.common.feign;

import com.pivothub.pojo.dto.system.feign.VerifyAccessDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pivot-hub-system", contextId = "systemAuthClient", path = "/system/feign/auth")
public interface SystemAuthClient {
    @PostMapping("/verifyAccess")
    Boolean verifyAccess(@RequestBody VerifyAccessDto dto);
}

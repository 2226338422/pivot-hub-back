package com.pivothub.common.feign;

import com.pivothub.pojo.dto.system.feign.CheckMenuAccessDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pivot-hub-system", contextId = "systemMenuClient", path = "/system/feign/menu")
public interface SystemMenuClient {
    @PostMapping("/checkAccess")
    Boolean checkAccess(@RequestBody CheckMenuAccessDto dto);
}

package com.pivothub.common.feign;

import com.pivothub.pojo.vo.system.UserProvinceVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/** 系统用户信息的内部查询接口。 */
@FeignClient(name = "pivot-hub-system", contextId = "systemUserClient", path = "/system/feign/user")
public interface SystemUserClient {
    /** 查询已启用账号的用户 UUID 和省份编码列表。 */
    @GetMapping("/provinces")
    List<UserProvinceVo> listUserProvinces();
}

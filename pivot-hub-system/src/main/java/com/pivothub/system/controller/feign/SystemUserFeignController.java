package com.pivothub.system.controller.feign;

import com.pivothub.pojo.vo.system.UserProvinceVo;
import com.pivothub.system.service.UserProfileService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 系统用户信息的内部查询接口。 */
@Hidden
@RestController
@RequestMapping("/system/feign/user")
public class SystemUserFeignController {
    @Autowired
    private UserProfileService userProfileService;

    /** 查询已启用账号的用户 UUID 和省份编码列表。 */
    @GetMapping("/provinces")
    public List<UserProvinceVo> listUserProvinces() {
        return userProfileService.listUserProvinces();
    }
}

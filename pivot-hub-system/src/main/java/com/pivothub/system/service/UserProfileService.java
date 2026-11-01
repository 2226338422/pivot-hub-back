package com.pivothub.system.service;

import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserInfoVo;

public interface UserProfileService {
    SysUser getUser(String userId);

    UserInfoVo getUserInfo(String userId);

    UserInfoVo refreshCache(String userId);

    UserInfoVo updateProfile(SysUser user);

    /**
     * 换绑邮箱：校验验证码后更新登录邮箱。
     */
    void rebindEmail(String userId, String newEmail, String code);
}

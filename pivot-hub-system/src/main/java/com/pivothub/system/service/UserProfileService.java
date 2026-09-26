package com.pivothub.system.service;

import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserInfoVo;

public interface UserProfileService {
    SysUser getUser(String userId);

    UserInfoVo getUserInfo(String userId);

    UserInfoVo refreshCache(String userId);

    UserInfoVo updateProfile(SysUser user);
}

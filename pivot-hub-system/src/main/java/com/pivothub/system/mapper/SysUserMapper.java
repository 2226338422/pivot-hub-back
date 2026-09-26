package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper {
    SysUser selectByEmail(@Param("email") String email);

    SysUser selectById(@Param("uuid") String uuid);

    int insertUser(SysUser user);

    int updateProfile(SysUser user);
}

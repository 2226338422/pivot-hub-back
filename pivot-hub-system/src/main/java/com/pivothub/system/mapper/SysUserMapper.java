package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
    SysUser selectByEmail(@Param("email") String email);

    SysUser selectById(@Param("uuid") String uuid);

    /** 批量查询指定用户的邮箱。 */
    List<SysUser> selectEmailsByUserIds(@Param("userIds") List<String> userIds);

    int insertUser(SysUser user);

    int updateProfile(SysUser user);

    int updateEmail(@Param("uuid") String uuid, @Param("email") String email);
}

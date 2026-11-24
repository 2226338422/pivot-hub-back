package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserProvinceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {
    SysUser selectByEmail(@Param("email") String email);

    SysUser selectById(@Param("uuid") String uuid);

    /** 批量查询指定用户的邮箱。 */
    List<SysUser> selectEmailsByUserIds(@Param("userIds") List<String> userIds);

    /** 查询已启用账号的用户 UUID 和省份编码列表。 */
    List<UserProvinceVo> selectUserProvinces();

    int insertUser(SysUser user);

    int updateProfile(SysUser user);

    int updateEmail(@Param("uuid") String uuid, @Param("email") String email);
}

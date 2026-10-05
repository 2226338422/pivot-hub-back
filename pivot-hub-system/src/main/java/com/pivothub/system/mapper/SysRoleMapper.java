package com.pivothub.system.mapper;

import com.pivothub.pojo.dto.system.RolePageDto;
import com.pivothub.pojo.po.system.SysRole;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMapper {
    List<SysRole> selectPage(RolePageDto query);
    SysRole selectLiveById(@Param("roleId") String roleId);
    SysRole selectByIdForUpdate(@Param("roleId") String roleId);
    List<String> selectRoleIdsForUpdate();
    int clearDefaultRoles(@Param("operatorId") String operatorId);
    int countByCode(@Param("code") String code);
    long countUserReferences(@Param("roleId") String roleId);
    int insertRole(SysRole role);
    int updateRole(SysRole role);
    int markDeleted(@Param("roleId") String roleId, @Param("operatorId") String operatorId);
}

package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysPermission;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysPermissionMapper {
    List<SysPermission> selectRolePermissions(@Param("roleId") String roleId);
    List<String> selectActiveRoleMenuIds(@Param("roleId") String roleId);
    int disableRolePermissions(@Param("roleId") String roleId, @Param("operatorId") String operatorId);
    int activatePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId,
                           @Param("operatorId") String operatorId);
    int insertPermission(SysPermission permission);
}

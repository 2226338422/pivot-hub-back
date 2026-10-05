package com.pivothub.system.service;

import com.pivothub.pojo.dto.system.*;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.RoleVo;
import java.util.List;

public interface RoleManagementService {
    PageVo<RoleVo> page(String operatorId, RolePageDto query);
    RoleVo detail(String operatorId, String roleId);
    RoleVo add(String operatorId, RoleCreateDto request);
    RoleVo update(String operatorId, RoleUpdateDto request);
    void delete(String operatorId, String roleId);
    List<String> getMenuIds(String operatorId, String roleId);
    List<String> saveMenuPermissions(String operatorId, RoleMenuPermissionDto request);
}

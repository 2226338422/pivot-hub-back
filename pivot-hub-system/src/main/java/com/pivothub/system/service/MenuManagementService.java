package com.pivothub.system.service;

import com.pivothub.pojo.po.system.SysMenu;
import com.pivothub.pojo.vo.system.MenuTreeVo;
import java.util.List;
import java.util.Set;

public interface MenuManagementService {
    List<MenuTreeVo> getAllMenuTree(String operatorId);
    Set<String> normalizePermissionSelection(List<SysMenu> menus, Set<String> requestedIds, Set<String> existingIds);
}

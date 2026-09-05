package com.pivothub.system.service;

import com.pivothub.pojo.vo.MenuTreeVo;
import java.util.List;

public interface SysMenuService {

    /**
     * 根据用户ID查询菜单树
     */
    List<MenuTreeVo> getMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}

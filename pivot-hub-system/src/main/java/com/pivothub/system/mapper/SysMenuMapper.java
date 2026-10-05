package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuMapper {
    List<SysMenu> selectUserMenus(@Param("userId") String userId);
    List<SysMenu> selectAllMenus();
    List<SysMenu> selectAllMenusForUpdate();
    int countUserMenuAccess(@Param("userId") String userId, @Param("menuCode") String menuCode);
}

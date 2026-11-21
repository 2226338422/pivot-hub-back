package com.pivothub.system.mapper;

import com.pivothub.pojo.po.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper {
    List<SysMenu> selectUserMenus(@Param("userId") String userId);
    List<SysMenu> selectAllMenus();
    List<SysMenu> selectAllMenusForUpdate();
    int countUserMenuAccess(@Param("userId") String userId, @Param("menuCode") String menuCode);
    /** 从候选用户中筛选有来源菜单权限的活跃用户。 */
    List<String> selectUserIdsWithMenuAccess(@Param("menuId") String menuId, @Param("userIds") List<String> userIds);
}

package com.pivothub.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pivothub.pojo.po.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询菜单树（含按钮权限）
     */
    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);
}

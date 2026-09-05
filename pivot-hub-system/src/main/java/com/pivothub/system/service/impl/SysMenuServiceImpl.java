package com.pivothub.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pivothub.commoncore.constants.redis.ManagerRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import com.pivothub.pojo.po.SysMenu;
import com.pivothub.pojo.po.SysPermission;
import com.pivothub.pojo.po.SysUserRole;
import com.pivothub.pojo.vo.MenuTreeVo;
import com.pivothub.system.mapper.*;
import com.pivothub.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysPermissionMapper permissionMapper;
    @Autowired
    private CacheUtil cacheUtil;

    @Override
    public List<MenuTreeVo> getMenuTreeByUserId(Long userId) {
        String cacheKey = ManagerRedisConstants.USER_MANAGER_KEY + userId + ":menu";
        String cached = cacheUtil.get(cacheKey);
        if (cached != null) {
            return com.alibaba.fastjson.JSON.parseArray(cached, MenuTreeVo.class);
        }

        // 查用户角色
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId).collect(Collectors.toSet());

        // 查角色拥有的菜单ID（通过权限表）
        Set<Long> menuIds = new HashSet<>();
        for (Long roleId : roleIds) {
            List<SysPermission> perms = permissionMapper.selectList(
                    new LambdaQueryWrapper<SysPermission>()
                            .eq(SysPermission::getPermissionType, 1)
                            .eq(SysPermission::getTargetId, roleId));
            perms.forEach(p -> menuIds.add(p.getMenuId()));
        }

        // 查用户直接分配的权限
        List<SysPermission> directPerms = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getPermissionType, 2)
                        .eq(SysPermission::getTargetId, userId));
        directPerms.forEach(p -> menuIds.add(p.getMenuId()));

        // 查菜单
        List<SysMenu> menus = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, menuIds)
                        .eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSort));

        // 构建树
        List<MenuTreeVo> tree = menus.stream().map(this::toTreeVo).collect(Collectors.toList());
        List<MenuTreeVo> root = buildTree(tree);

        // 写入缓存
        cacheUtil.set(cacheKey, com.alibaba.fastjson.JSON.toJSONString(root), 30L, java.util.concurrent.TimeUnit.HOURS);

        return root;
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<SysPermission> perms = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getPermissionType, 1)
                        .eq(SysPermission::getTargetId, roleId));
        return perms.stream().map(SysPermission::getMenuId).collect(Collectors.toList());
    }

    private MenuTreeVo toTreeVo(SysMenu menu) {
        MenuTreeVo vo = new MenuTreeVo();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        return vo;
    }

    private List<MenuTreeVo> buildTree(List<MenuTreeVo> all) {
        // 找根节点
        List<MenuTreeVo> root = all.stream()
                .filter(m -> m.getParentId() == null || m.getParentId() == 0)
                .collect(Collectors.toList());
        // 为每个根节点递归挂子节点
        root.forEach(r -> r.setChildren(findChildren(r.getId(), all)));
        return root;
    }

    private List<MenuTreeVo> findChildren(Long parentId, List<MenuTreeVo> all) {
        List<MenuTreeVo> children = all.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .collect(Collectors.toList());
        children.forEach(c -> c.setChildren(findChildren(c.getId(), all)));
        return children;
    }
}

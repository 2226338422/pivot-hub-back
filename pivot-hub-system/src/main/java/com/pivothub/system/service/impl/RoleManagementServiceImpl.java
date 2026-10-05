package com.pivothub.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pivothub.common.exception.SystemException;
import com.pivothub.pojo.dto.system.*;
import com.pivothub.pojo.po.system.*;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.RoleVo;
import com.pivothub.system.mapper.*;
import com.pivothub.system.service.MenuManagementService;
import com.pivothub.system.service.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.*;
import static com.pivothub.commoncore.constants.system.RoleManagementConstants.*;

@Service
@RequiredArgsConstructor
public class RoleManagementServiceImpl implements RoleManagementService {
    private final MenuManagementService menuService;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysMenuMapper menuMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageVo<RoleVo> page(String operatorId, RolePageDto query) {
        if (query == null || query.getPageNum() == null || query.getPageNum() < 1
                || query.getPageSize() == null || query.getPageSize() < 1 || query.getPageSize() > 100) {
            throw new SystemException(400, "页码至少为1，每页条数为1到100");
        }
        query.setName(query.getName() == null ? null : query.getName().trim());
        query.setCode(query.getCode() == null ? null : query.getCode().trim());
        List<SysRole> roles;
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        try {
            roles = roleMapper.selectPage(query);
        } finally {
            PageHelper.clearPage();
        }
        long total = new PageInfo<>(roles).getTotal();
        return PageVo.of(query.getPageNum(), query.getPageSize(), total, roles.stream().map(this::toVo).toList());
    }

    @Override
    public RoleVo detail(String operatorId, String roleId) {
        return toVo(requireLiveRole(roleMapper.selectLiveById(requireId(roleId))));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVo add(String operatorId, RoleCreateDto request) {
        if (request == null || !StringUtils.hasText(request.getCode())
                || !request.getCode().trim().matches("^[A-Za-z0-9_-]{1,64}$")) {
            throw new SystemException(400, "角色编码格式不正确");
        }
        String code = request.getCode().trim();
        String name = requireName(request.getName());
        requireFields(request.getSort(), request.getRemark());
        Integer defa = requireDefaultFlag(request.getDefa());
        if (ENABLED.equals(defa)) {
            // 默认角色切换先统一锁定角色，避免并发请求各自成为默认角色。
            roleMapper.selectRoleIdsForUpdate();
        }
        if (roleMapper.countByCode(code) > 0) {
            throw new SystemException(409, "角色编码已存在，已删除角色的编码也不能复用");
        }
        SysRole role = new SysRole();
        role.setUuid(IdWorker.getIdStr());
        role.setCode(code);
        role.setName(name);
        role.setDefa(defa);
        role.setStatus(ROLE_NORMAL);
        role.setSort(request.getSort());
        role.setRemark(request.getRemark());
        Date now = new Date();
        role.setCreateUserId(operatorId);
        role.setUpdateUserId(operatorId);
        role.setCreateTime(now);
        role.setUpdateTime(now);
        try {
            if (ENABLED.equals(defa)) {
                roleMapper.clearDefaultRoles(operatorId);
            }
            if (roleMapper.insertRole(role) != 1) {
                throw new SystemException(500, "新增角色失败");
            }
        } catch (DuplicateKeyException ex) {
            throw new SystemException(409, "角色编码已存在，已删除角色的编码也不能复用");
        }
        return toVo(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVo update(String operatorId, RoleUpdateDto request) {
        if (request == null) {
            throw new SystemException(400, "角色信息不能为空");
        }
        String name = requireName(request.getName());
        requireFields(request.getSort(), request.getRemark());
        Integer defa = request.getDefa();
        if (defa != null) {
            requireDefaultFlag(defa);
        }
        if (ENABLED.equals(defa)) {
            roleMapper.selectRoleIdsForUpdate();
        }
        SysRole role = requireLiveRole(roleMapper.selectByIdForUpdate(requireId(request.getRoleId())));
        role.setName(name);
        if (defa != null) {
            role.setDefa(defa);
        }
        role.setSort(request.getSort());
        role.setRemark(request.getRemark());
        role.setUpdateUserId(operatorId);
        role.setUpdateTime(new Date());
        if (ENABLED.equals(defa)) {
            roleMapper.clearDefaultRoles(operatorId);
        }
        if (roleMapper.updateRole(role) != 1) {
            throw new SystemException(409, "角色已变更，请重新加载");
        }
        return toVo(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String operatorId, String roleId) {
        roleId = requireId(roleId);
        SysRole role = roleMapper.selectByIdForUpdate(roleId);
        if (role == null) {
            throw new SystemException(404, "角色不存在");
        }
        if (ROLE_DELETED.equals(role.getStatus())) {
            return;
        }
        requireLiveRole(role);
        if (ENABLED.equals(role.getDefa())) {
            throw new SystemException(409, "默认角色不能删除");
        }
        if (roleMapper.countUserReferences(roleId) > 0) {
            throw new SystemException(409, "该角色已关联用户，不能删除");
        }
        if (roleMapper.markDeleted(roleId, operatorId) != 1) {
            throw new SystemException(409, "角色已变更，请重新加载");
        }
    }

    @Override
    public List<String> getMenuIds(String operatorId, String roleId) {
        roleId = requireId(roleId);
        requireLiveRole(roleMapper.selectLiveById(roleId));
        return permissionMapper.selectActiveRoleMenuIds(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> saveMenuPermissions(String operatorId, RoleMenuPermissionDto request) {
        if (request == null || request.getMenuIds() == null) {
            throw new SystemException(400, "菜单权限列表不能为空，清空时传空数组");
        }
        String roleId = requireId(request.getRoleId());
        requireLiveRole(roleMapper.selectByIdForUpdate(roleId));
        Set<String> requested = new LinkedHashSet<>();
        for (String id : request.getMenuIds()) {
            requested.add(requireId(id));
        }
        List<SysMenu> menus = menuMapper.selectAllMenusForUpdate();
        List<SysPermission> previous = permissionMapper.selectRolePermissions(roleId);
        Set<String> existing = new LinkedHashSet<>();
        Map<String, SysPermission> canonical = new LinkedHashMap<>();
        for (SysPermission permission : previous) {
            canonical.putIfAbsent(permission.getMenuId(), permission);
            if (ENABLED.equals(permission.getStatus())) {
                existing.add(permission.getMenuId());
            }
        }
        Set<String> selected = menuService.normalizePermissionSelection(menus, requested, existing);
        SysUser operator = userMapper.selectById(operatorId);
        if (operator != null && roleId.equals(operator.getRoleId())) {
            for (SysMenu menu : menus) {
                if (MANAGEMENT_MENU_CODE.equals(menu.getCode()) && existing.contains(menu.getUuid())
                        && !selected.contains(menu.getUuid())) {
                    throw new SystemException(409, "不能移除当前角色已有的角色管理权限");
                }
            }
        }
        permissionMapper.disableRolePermissions(roleId, operatorId);
        Map<String, SysMenu> menuById = new HashMap<>();
        menus.forEach(menu -> menuById.put(menu.getUuid(), menu));
        for (String menuId : selected) {
            SysPermission old = canonical.get(menuId);
            if (old != null) {
                if (permissionMapper.activatePermission(roleId, old.getUuid(), operatorId) != 1) {
                    throw new SystemException(409, "权限记录已变更，请重新加载");
                }
            } else {
                SysPermission permission = new SysPermission();
                permission.setUuid(IdWorker.getIdStr());
                permission.setName(menuById.get(menuId).getMenuName());
                permission.setMenuId(menuId);
                permission.setRoleId(roleId);
                permission.setPersonType(ROLE_PERMISSION);
                permission.setStatus(ENABLED);
                Date now = new Date();
                permission.setCreateUserId(operatorId);
                permission.setUpdateUserId(operatorId);
                permission.setCreateTime(now);
                permission.setUpdateTime(now);
                if (permissionMapper.insertPermission(permission) != 1) {
                    throw new SystemException(500, "保存菜单权限失败");
                }
            }
        }
        return permissionMapper.selectActiveRoleMenuIds(roleId);
    }

    private String requireId(String id) {
        if (!StringUtils.hasText(id) || id.length() > 32) {
            throw new SystemException(400, "角色或菜单标识不能为空且不能超过32位");
        }
        return id.trim();
    }

    private String requireName(String name) {
        if (!StringUtils.hasText(name) || name.trim().length() > 64) {
            throw new SystemException(400, "角色名称必须为1到64字");
        }
        return name.trim();
    }

    private Integer requireDefaultFlag(Integer defa) {
        if (!DISABLED.equals(defa) && !ENABLED.equals(defa)) {
            throw new SystemException(400, "默认角色标记只能为0或1");
        }
        return defa;
    }

    private void requireFields(Integer sort, String remark) {
        if (sort == null || sort < 0 || (remark != null && remark.length() > 500)) {
            throw new SystemException(400, "排序不能小于0，备注不能超过500字");
        }
    }

    private SysRole requireLiveRole(SysRole role) {
        if (role == null || !ROLE_NORMAL.equals(role.getStatus())) {
            throw new SystemException(404, "角色不存在或已删除");
        }
        return role;
    }

    private RoleVo toVo(SysRole role) {
        RoleVo vo = new RoleVo();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}

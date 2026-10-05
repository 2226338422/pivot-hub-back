package com.pivothub.system.service.impl;

import com.pivothub.common.exception.SystemException;
import com.pivothub.pojo.po.system.SysMenu;
import com.pivothub.pojo.vo.system.MenuTreeVo;
import com.pivothub.system.mapper.SysMenuMapper;
import com.pivothub.system.service.MenuManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.*;
import static com.pivothub.commoncore.constants.system.RoleManagementConstants.*;

@Service
@RequiredArgsConstructor
public class MenuManagementServiceImpl implements MenuManagementService {
    private final SysMenuMapper menuMapper;

    @Override
    public List<MenuTreeVo> getAllMenuTree(String operatorId) {
        return buildMenuTree(menuMapper.selectAllMenus());
    }

    @Override
    public Set<String> normalizePermissionSelection(List<SysMenu> menus, Set<String> requestedIds,
                                                     Set<String> existingIds) {
        Map<String, MenuTreeVo> nodes = new LinkedHashMap<>();
        Deque<MenuTreeVo> pending = new ArrayDeque<>(buildMenuTree(menus));
        while (!pending.isEmpty()) {
            MenuTreeVo node = pending.removeFirst();
            nodes.put(node.getUuid(), node);
            pending.addAll(node.getChildren());
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String id : requestedIds) {
            MenuTreeVo node = nodes.get(id);
            if (node == null) {
                throw new SystemException(409, "所选菜单已不存在，请重新加载");
            }
            if (node.isDisabled()) {
                if (!existingIds.contains(id)) {
                    throw new SystemException(409, "菜单或父级已禁用，不能新增授权");
                }
                normalized.add(id);
                continue;
            }
            while (node != null) {
                normalized.add(node.getUuid());
                node = nodes.get(node.getParentId());
            }
        }
        for (String id : existingIds) {
            MenuTreeVo node = nodes.get(id);
            if (node != null && node.isDisabled()) {
                normalized.add(id);
            }
        }
        return normalized;
    }

    private List<MenuTreeVo> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> sorted = new ArrayList<>(menus);
        sorted.sort(Comparator.comparing((SysMenu m) -> m.getSort() == null ? 0 : m.getSort())
                .thenComparing(SysMenu::getUuid, Comparator.nullsFirst(Comparator.naturalOrder())));
        Map<String, MenuTreeVo> nodes = new LinkedHashMap<>();
        for (SysMenu menu : sorted) {
            if (!StringUtils.hasText(menu.getUuid()) || nodes.containsKey(menu.getUuid())) {
                throw new SystemException(500, "菜单唯一标识缺失或重复");
            }
            MenuTreeVo node = new MenuTreeVo();
            BeanUtils.copyProperties(menu, node);
            node.setParentId(StringUtils.hasText(menu.getParentId()) ? menu.getParentId().trim() : ROOT_PARENT_ID);
            node.setDisabled(!ENABLED.equals(menu.getStatus()));
            nodes.put(node.getUuid(), node);
        }
        List<MenuTreeVo> roots = new ArrayList<>();
        for (MenuTreeVo node : nodes.values()) {
            if (ROOT_PARENT_ID.equals(node.getParentId())) {
                roots.add(node);
            } else {
                MenuTreeVo parent = nodes.get(node.getParentId());
                if (parent == null || node.getUuid().equals(node.getParentId())) {
                    throw new SystemException(500, "菜单父级缺失或指向自身");
                }
                parent.getChildren().add(node);
            }
        }
        Deque<MenuTreeVo> pending = new ArrayDeque<>(roots);
        int visited = 0;
        while (!pending.isEmpty()) {
            MenuTreeVo parent = pending.removeFirst();
            visited++;
            for (MenuTreeVo child : parent.getChildren()) {
                child.setDisabled(child.isDisabled() || parent.isDisabled());
                pending.addLast(child);
            }
        }
        if (visited != nodes.size()) {
            throw new SystemException(500, "菜单存在循环父级配置");
        }
        return roots;
    }
}

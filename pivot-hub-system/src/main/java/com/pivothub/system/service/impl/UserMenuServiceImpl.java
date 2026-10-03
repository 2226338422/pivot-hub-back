package com.pivothub.system.service.impl;

import com.pivothub.common.exception.AuthException;
import com.pivothub.pojo.po.system.SysMenu;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserMenuVo;
import com.pivothub.system.mapper.SysMenuMapper;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.service.UserMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Service
public class UserMenuServiceImpl implements UserMenuService {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<UserMenuVo> getUserMenuTree(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new AuthException("请先登录");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            throw new AuthException("用户不存在或已禁用");
        }

        Map<String, UserMenuVo> nodes = new LinkedHashMap<>();
        for (SysMenu menu : menuMapper.selectUserMenus(userId)) {
            UserMenuVo node = new UserMenuVo();
            BeanUtils.copyProperties(menu, node);
            nodes.put(node.getUuid(), node);
        }
        List<UserMenuVo> roots = new ArrayList<>();
        for (UserMenuVo node : nodes.values()) {
            UserMenuVo parent = "0".equals(node.getParentId()) ? null : nodes.get(node.getParentId());
            if (parent == null) {
                roots.add(node);
            } else {
                parent.getChildren().add(node);
            }
        }

        Queue<UserMenuVo> pending = new ArrayDeque<>(roots);
        int visited = 0;
        while (!pending.isEmpty()) {
            visited++;
            pending.addAll(pending.remove().getChildren());
        }
        if (visited != nodes.size()) {
            throw new IllegalStateException("菜单存在循环父级配置");
        }
        return roots;
    }
}

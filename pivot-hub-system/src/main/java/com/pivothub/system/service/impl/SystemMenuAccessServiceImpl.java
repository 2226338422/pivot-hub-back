package com.pivothub.system.service.impl;

import com.pivothub.common.exception.SystemException;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.system.mapper.SysMenuMapper;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.service.SystemMenuAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.pivothub.commoncore.constants.system.RoleManagementConstants.ENABLED;

@Service
public class SystemMenuAccessServiceImpl implements SystemMenuAccessService {
    @Autowired
    private SysMenuMapper menuMapper;
    @Autowired
    private SysUserMapper userMapper;

    @Override
    public boolean checkAccess(String userId, String menuCode) {
        if (!StringUtils.hasText(userId)) throw new SystemException(400, "用户标识不能为空");
        if (!StringUtils.hasText(menuCode)) throw new SystemException(400, "菜单编码不能为空");
        try {
            SysUser user = userMapper.selectById(userId);
            return user != null && ENABLED.equals(user.getStatus())
                    && menuMapper.countUserMenuAccess(userId, menuCode.trim()) > 0;
        } catch (Exception ex) {
            throw new SystemException(503, "菜单服务暂时不可用，请稍后重试");
        }
    }
}

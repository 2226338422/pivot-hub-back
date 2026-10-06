package com.pivothub.system.service;

/**
 * @Description: 内部服务用户菜单访问资格查询接口
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public interface SystemMenuAccessService {
    /**
     * @Description: 查询指定用户是否具备指定菜单的访问资格
     * @param userId 调用服务从已认证上下文取得的用户标识
     * @param menuCode 待查询的菜单编码
     * @return 是否具备菜单访问资格
     */
    boolean checkAccess(String userId, String menuCode);
}

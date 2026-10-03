package com.pivothub.system.service;

import com.pivothub.pojo.vo.system.UserMenuVo;

import java.util.List;

public interface UserMenuService {
    List<UserMenuVo> getUserMenuTree(String userId);
}

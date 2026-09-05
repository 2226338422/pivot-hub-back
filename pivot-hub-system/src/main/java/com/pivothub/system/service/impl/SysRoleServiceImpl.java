package com.pivothub.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pivothub.pojo.po.SysRole;
import com.pivothub.system.mapper.SysRoleMapper;
import com.pivothub.system.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}

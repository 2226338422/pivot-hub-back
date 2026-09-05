package com.pivothub.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pivothub.pojo.po.SysUser;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}

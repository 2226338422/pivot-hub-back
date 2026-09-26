package com.pivothub.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.commoncore.constants.redis.SystemRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserInfoVo;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.service.UserProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private static final long PROFILE_TTL_HOURS = 24L;

    @Autowired
    private SysUserMapper mapper;
    @Autowired
    private CacheUtil cacheUtil;

    @Override
    public SysUser getUser(String userId) {
        return mapper.selectById(userId);
    }

    @Override
    public UserInfoVo getUserInfo(String userId) {
        String key = SystemRedisConstants.USER_PROFILE_KEY_PREFIX + userId;
        UserInfoVo cached = cacheUtil.getObject(key, UserInfoVo.class);
        return cached == null ? refreshCache(userId) : cached;
    }

    @Override
    public UserInfoVo refreshCache(String userId) {
        SysUser user = mapper.selectById(userId);
        if (user == null) {
            cacheUtil.deleteIfExists(SystemRedisConstants.USER_PROFILE_KEY_PREFIX + userId);
            return null;
        }
        UserInfoVo profile = new UserInfoVo();
        BeanUtils.copyProperties(user, profile);
        cacheUtil.setObject(SystemRedisConstants.USER_PROFILE_KEY_PREFIX + userId, profile,
                PROFILE_TTL_HOURS, TimeUnit.HOURS);
        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVo updateProfile(SysUser user) {
        user.setUpdateTime(new Date());
        if (mapper.updateProfile(user) != 1) {
            throw new IllegalStateException("用户资料更新失败");
        }
        return refreshCache(user.getUuid());
    }
}

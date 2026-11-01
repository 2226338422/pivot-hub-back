package com.pivothub.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.commoncore.constants.redis.SystemRedisConstants;
import com.pivothub.commoncore.util.CacheUtil;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.system.UserInfoVo;
import com.pivothub.system.file.service.FileService;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.service.MailCodeService;
import com.pivothub.system.service.UserProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private static final long PROFILE_TTL_HOURS = 24L;

    @Autowired
    private SysUserMapper mapper;
    @Autowired
    private CacheUtil cacheUtil;
    @Autowired
    private FileService fileService;
    @Autowired
    private MailCodeService mailCodeService;

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
        fillDefaultAvatar(profile);
        cacheUtil.setObject(SystemRedisConstants.USER_PROFILE_KEY_PREFIX + userId, profile,
                PROFILE_TTL_HOURS, TimeUnit.HOURS);
        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVo updateProfile(SysUser user) {
        user.setUpdateUserId(user.getUuid());
        if (mapper.updateProfile(user) != 1) {
            throw new IllegalStateException("用户资料更新失败");
        }
        return refreshCache(user.getUuid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebindEmail(String userId, String newEmail, String code) {
        if (!StringUtils.hasText(newEmail) || !StringUtils.hasText(code)) {
            throw new IllegalArgumentException("邮箱或验证码不能为空");
        }
        String normalized = newEmail.trim().toLowerCase(Locale.ROOT);
        SysUser existing = mapper.selectByEmail(normalized);
        if (existing != null) {
            if (existing.getUuid().equals(userId)) {
                throw new IllegalArgumentException("新邮箱不能与当前邮箱相同");
            }
            throw new IllegalArgumentException("该邮箱已被其他账号使用");
        }
        if (!mailCodeService.verifyAndConsume(normalized, "rebind", code)) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }
        if (mapper.updateEmail(userId, normalized) != 1) {
            throw new IllegalStateException("邮箱换绑失败");
        }
        refreshCache(userId);
    }

    /** 无头像时把默认头像的文件 uuid 填进 avatarFileId，前端统一用 fileUrl 取地址。 */
    private void fillDefaultAvatar(UserInfoVo profile) {
        if (!StringUtils.hasText(profile.getAvatarFileId())) {
            profile.setAvatarFileId(fileService.defaultAvatarFileId());
        }
    }
}

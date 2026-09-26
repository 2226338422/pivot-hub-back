package com.pivothub.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.commoncore.config.AuthConfig;
import com.pivothub.commoncore.constants.auth.TokenConstants;
import com.pivothub.commoncore.util.JWTUtil;
import com.pivothub.commoncore.util.SecureEncryptionUtil;
import com.pivothub.pojo.dto.system.EmailLoginDto;
import com.pivothub.pojo.dto.system.TokenDto;
import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.vo.TokenVo;
import com.pivothub.system.service.AuthSessionService;
import com.pivothub.system.service.SystemAuthService;
import com.pivothub.pojo.dto.redis.system.TokenRedisDto;
import com.pivothub.system.mapper.SysUserMapper;
import com.pivothub.system.service.MailCodeService;
import com.pivothub.system.service.UserProfileService;
import io.jsonwebtoken.Claims;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Locale;

@Service
public class SystemAuthServiceImpl implements SystemAuthService {
    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private AuthSessionService authSessionService;
    @Autowired
    private MailCodeService mailCodeService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = DuplicateKeyException.class)
    public TokenVo loginByEmail(EmailLoginDto dto) {
        String email = normalizeEmail(dto.getEmail());
        ClientType clientType = ClientType.fromValue(dto.getClientType());
        if (!mailCodeService.verifyAndConsume(email, "login", dto.getCode())) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }
        SysUser user = userMapper.selectByEmail(email);
        if (user == null) {
            user = registerByEmail(email);
        }
        assertEnabled(user);
        TokenRedisDto pair = issue(user.getUuid(), clientType);
        authSessionService.replaceLogin(user.getUuid(), clientType, pair.getAccessToken(), pair.getRefreshToken(), user);
        userProfileService.refreshCache(user.getUuid());
        return toVo(pair, clientType);
    }

    @Override
    public TokenVo refresh(TokenDto dto) {
        Claims claims = JWTUtil.parseToken(dto.getRefreshToken(), authConfig.getJwtRefreshSecret());
        requireTokenType(claims, TokenConstants.TOKEN_TYPE_REFRESH);
        String userId = decryptUserId(claims.getSubject());
        ClientType clientType = ClientType.fromValue(claims.get("clientType", Integer.class));
        if (!authSessionService.verifyRefresh(userId, clientType, dto.getRefreshToken())) {
            throw new IllegalArgumentException("登录已过期");
        }
        SysUser user = userProfileService.getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        assertEnabled(user);
        TokenRedisDto pair = issue(userId, clientType);
        if (!authSessionService.rotateRefresh(userId, clientType, dto.getRefreshToken(), pair, user)) {
            throw new IllegalArgumentException("刷新令牌已失效");
        }
        userProfileService.refreshCache(userId);
        return toVo(pair, clientType);
    }

    private SysUser registerByEmail(String email) {
        SysUser user = new SysUser();
        user.setUuid(IdWorker.getIdStr());
        user.setCode("U" + user.getUuid());
        user.setNickname("用户" + user.getUuid().substring(0, Math.min(8, user.getUuid().length())));
        user.setEmail(email);
        user.setStatus(1);
        try {
            if (userMapper.insertUser(user) != 1) {
                throw new IllegalStateException("用户注册失败");
            }
            return user;
        } catch (DuplicateKeyException ex) {
            SysUser existing = userMapper.selectByEmail(email);
            if (existing == null) {
                throw ex;
            }
            return existing;
        }
    }

    private TokenRedisDto issue(String userId, ClientType clientType) {
        try {
            String subject = SecureEncryptionUtil.encrypt(userId, authConfig.getJwtIdSecret());
            Map<String, Object> claims = Map.of(
                    "clientType", clientType.getValue(),
                    "tokenType", TokenConstants.TOKEN_TYPE_ACCESS);
            String access = JWTUtil.generateToken(subject, required(authConfig.getJwtAccessTime()),
                    authConfig.getJwtAccessSecret(), claims);
            String refresh = JWTUtil.generateToken(subject, required(authConfig.getJwtRefreshTime()),
                    authConfig.getJwtRefreshSecret(), Map.<String, Object>of(
                            "clientType", clientType.getValue(),
                            "tokenType", TokenConstants.TOKEN_TYPE_REFRESH));
            return new TokenRedisDto(access, refresh);
        } catch (Exception ex) {
            throw new IllegalStateException("令牌生成失败", ex);
        }
    }

    private TokenVo toVo(TokenRedisDto pair, ClientType clientType) {
        return new TokenVo(pair.getAccessToken(), pair.getRefreshToken(),
                required(authConfig.getJwtAccessTime()) / 1000,
                required(authConfig.getJwtRefreshTime()) / 1000,
                clientType.getValue());
    }

    private String decryptUserId(String subject) {
        try {
            return SecureEncryptionUtil.decrypt(subject, authConfig.getJwtIdSecret());
        } catch (Exception ex) {
            throw new IllegalArgumentException("令牌中的用户信息无效", ex);
        }
    }

    private void requireTokenType(Claims claims, String expected) {
        if (claims == null || !expected.equals(String.valueOf(claims.get("tokenType")))) {
            throw new IllegalArgumentException("令牌类型无效");
        }
    }

    private void assertEnabled(SysUser user) {
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new IllegalArgumentException("用户已禁用");
        }
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private long required(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalStateException("令牌有效期未配置");
        }
        return value;
    }
}

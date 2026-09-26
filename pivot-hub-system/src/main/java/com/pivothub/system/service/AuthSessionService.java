package com.pivothub.system.service;

import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.dto.redis.system.TokenRedisDto;

public interface AuthSessionService {
    void replaceLogin(String userId, ClientType clientType, String accessToken, String refreshToken, SysUser user);

    boolean verifyAccess(String userId, ClientType clientType, String accessToken);

    boolean verifyRefresh(String userId, ClientType clientType, String refreshToken);

    boolean rotateRefresh(String userId, ClientType clientType, String oldRefreshToken,
                          TokenRedisDto newPair, SysUser user);
}

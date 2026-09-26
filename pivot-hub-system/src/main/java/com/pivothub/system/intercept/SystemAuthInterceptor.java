package com.pivothub.system.intercept;

import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.commoncore.config.AuthConfig;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.constants.auth.TokenConstants;
import com.pivothub.commoncore.util.AuthHeaderUtil;
import com.pivothub.commoncore.util.JWTUtil;
import com.pivothub.commoncore.util.SecureEncryptionUtil;
import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.system.service.AuthSessionService;
import org.springframework.http.HttpHeaders;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SystemAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private AuthSessionService authSessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        TLUtil.remove(TLUtil.USER_ID);
        try {
            String token = AuthHeaderUtil.extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            if (token == null) {
                throw new IllegalArgumentException("请先登录");
            }
            Claims claims = JWTUtil.parseToken(token, authConfig.getJwtAccessSecret());
            if (!TokenConstants.TOKEN_TYPE_ACCESS.equals(String.valueOf(claims.get("tokenType")))) {
                throw new IllegalArgumentException("访问令牌类型无效");
            }
            String tokenUserId = SecureEncryptionUtil.decrypt(claims.getSubject(), authConfig.getJwtIdSecret());
            ClientType tokenClientType = ClientType.fromValue(claims.get("clientType", Integer.class));
            if (!authSessionService.verifyAccess(tokenUserId, tokenClientType, token)) {
                throw new IllegalArgumentException("身份认证失败");
            }
            TLUtil.set(TLUtil.USER_ID, tokenUserId);
            return true;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("身份认证失败", ex);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        TLUtil.remove(TLUtil.USER_ID);
    }
}

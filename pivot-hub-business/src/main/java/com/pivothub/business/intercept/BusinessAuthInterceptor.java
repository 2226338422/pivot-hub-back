package com.pivothub.business.intercept;

import com.pivothub.common.exception.AuthException;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.feign.SystemAuthClient;
import com.pivothub.common.util.TLUtil;
import com.pivothub.commoncore.config.AuthConfig;
import com.pivothub.commoncore.constants.auth.TokenConstants;
import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.commoncore.util.JWTUtil;
import com.pivothub.commoncore.util.AuthHeaderUtil;
import com.pivothub.commoncore.util.SecureEncryptionUtil;
import com.pivothub.pojo.dto.system.feign.VerifyAccessDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** 业务服务验证访问令牌及 System 中的活动登录记录。 */
@Component
public class BusinessAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    @Lazy
    private SystemAuthClient authClient;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        TLUtil.remove(TLUtil.USER_ID);
        String token = AuthHeaderUtil.extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (token == null) throw new AuthException("请先登录");
        String userId;
        ClientType clientType;
        try {
            Claims claims = JWTUtil.parseToken(token, authConfig.getJwtAccessSecret());
            if (!TokenConstants.TOKEN_TYPE_ACCESS.equals(claims.get("tokenType", String.class))) {
                throw new AuthException("访问令牌类型无效");
            }
            userId = SecureEncryptionUtil.decrypt(claims.getSubject(), authConfig.getJwtIdSecret());
            clientType = ClientType.fromValue(claims.get("clientType", Integer.class));
        } catch (AuthException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthException("身份认证失败", ex);
        }
        VerifyAccessDto dto = new VerifyAccessDto();
        dto.setUserId(userId);
        dto.setClientType(clientType.getValue());
        dto.setAccessToken(token);
        Boolean active;
        try {
            active = authClient.verifyAccess(dto);
        } catch (Exception ex) {
            throw new BusinessException(503, "认证服务暂时不可用，请稍后重试");
        }
        if (active == null) throw new BusinessException(503, "认证服务暂时不可用，请稍后重试");
        if (!active) throw new AuthException("身份认证失败");
        TLUtil.set(TLUtil.USER_ID, userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        TLUtil.remove(TLUtil.USER_ID);
    }
}

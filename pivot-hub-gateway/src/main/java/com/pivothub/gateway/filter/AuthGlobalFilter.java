package com.pivothub.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.pivothub.commoncore.config.AuthConfig;
import com.pivothub.commoncore.constants.auth.TokenConstants;
import com.pivothub.commoncore.enums.ClientType;
import com.pivothub.commoncore.util.JWTUtil;
import com.pivothub.commoncore.util.SecureEncryptionUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private static final List<String> EXEMPT_PATHS = Arrays.asList(
            "/system/auth/login/email",
            "/system/auth/refresh",
            "/system/auth/mail/**",
            "/system/mail/**",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/favicon.ico"
    );

    @Autowired
    private AuthConfig authConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = normalizePath(request.getURI().getPath());
        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name()) || isExemptPath(path)) {
            return chain.filter(exchange);
        }

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            return errorResponse(exchange.getResponse(), "身份认证失败");
        }
        try {
            Claims claims = JWTUtil.parseToken(authorization, authConfig.getJwtAccessSecret());
            if (!TokenConstants.TOKEN_TYPE_ACCESS.equals(String.valueOf(claims.get("tokenType")))) {
                return errorResponse(exchange.getResponse(), "身份认证失败");
            }
            String userId = SecureEncryptionUtil.decrypt(claims.getSubject(), authConfig.getJwtIdSecret());
            ClientType.fromValue(claims.get("clientType", Integer.class));
            if (StringUtils.isBlank(userId)) {
                return errorResponse(exchange.getResponse(), "身份认证失败");
            }
            return chain.filter(exchange);
        } catch (Exception ex) {
            log.debug("网关拒绝访问令牌，请求路径：{}", path);
            return errorResponse(exchange.getResponse(), "身份认证失败");
        }
    }

    boolean isExemptPath(String path) {
        for (String exempt : EXEMPT_PATHS) {
            if (exempt.endsWith("/**")) {
                if (path.startsWith(exempt.substring(0, exempt.length() - 3))) {
                    return true;
                }
            } else if (path.equals(exempt)) {
                return true;
            }
        }
        return false;
    }

    private String normalizePath(String path) {
        if (path != null && path.startsWith("/api/")) {
            return path.substring(4);
        }
        return path == null ? "" : path;
    }

    private Mono<Void> errorResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.UNAUTHORIZED.value());
        body.put("msg", message);
        body.put("data", null);
        DataBuffer buffer = response.bufferFactory()
                .wrap(JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

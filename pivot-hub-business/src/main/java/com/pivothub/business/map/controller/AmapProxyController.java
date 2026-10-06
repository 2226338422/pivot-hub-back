package com.pivothub.business.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.annotation.MenuPermission;

import com.pivothub.business.map.service.AmapProxyService;
import com.pivothub.business.map.utils.AmapHttpClient;
import com.pivothub.common.util.TLUtil;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.commoncore.constants.map.MapConstants;
import com.pivothub.commoncore.util.MapProxyPathUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 高德JS API服务代理控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@MenuPermission("map")
@RequestMapping(MapConstants.SDK_PROXY_PREFIX)
public class AmapProxyController {
    @Autowired
    private AmapProxyService service;
    @Operation(summary = "代理转发高德JS API请求")
    @GetMapping({"", "/**"})
    public ResponseEntity<byte[]> forward(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        if (!MapProxyPathUtil.isSdkProxyGet(uri, request.getMethod())) {
            throw new BusinessException(404, "地图代理路径不受支持");
        }
        String path = uri.startsWith(MapConstants.SDK_PROXY_PREFIX) ? uri.substring(MapConstants.SDK_PROXY_PREFIX.length()) : "";
        AmapHttpClient.Response upstream = service.forward(path, request.getParameterMap(), TLUtil.USER_ID.get(), request);
        return ResponseEntity.status(upstream.status()).header(HttpHeaders.CONTENT_TYPE, upstream.contentType())
                .header(HttpHeaders.CACHE_CONTROL, "no-store").header("X-Content-Type-Options", "nosniff").body(upstream.bytes());
    }
}

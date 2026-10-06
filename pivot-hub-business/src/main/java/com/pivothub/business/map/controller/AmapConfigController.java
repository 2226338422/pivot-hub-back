package com.pivothub.business.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import com.pivothub.business.annotation.MenuPermission;

import com.pivothub.business.map.config.AmapProperties;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.result.Result;
import com.pivothub.commoncore.constants.map.MapConstants;
import com.pivothub.pojo.vo.business.map.MapRuntimeConfigVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 高德地图运行配置控制器
 * @Author lhb
 * @CreateTime 2026/10/5
 */
@RestController
@MenuPermission("map")
@RequestMapping("/business/map/amap")
public class AmapConfigController {
    @Autowired
    private AmapProperties properties;
    @Operation(summary = "获取高德JS API运行配置")
    @GetMapping("/config")
    public Result<MapRuntimeConfigVo> config(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        AmapProperties.Proxy proxy = properties.getProxy();
        if (proxy == null || proxy.getAllowedOrigins() == null || proxy.getAllowedOrigins().isEmpty()) {
            throw new BusinessException(503, "地图代理来源配置不可用");
        }
        String path = proxy.getPublicPath();
        if (path == null || !path.matches("/[A-Za-z0-9_/-]+") || path.contains("//")
                || !path.endsWith(MapConstants.SDK_PROXY_PREFIX)
                || properties.getJs().getKey() == null || properties.getJs().getKey().isBlank()
                || properties.getJs().getSecurityJsCode() == null || properties.getJs().getSecurityJsCode().isBlank()) {
            throw new BusinessException(503, "地图服务配置不可用");
        }
        MapRuntimeConfigVo vo = new MapRuntimeConfigVo();
        vo.setJsKey(properties.getJs().getKey());
        vo.setServiceHost(path);
        return Result.success(vo);
    }
}

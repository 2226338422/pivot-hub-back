package com.pivothub.business.map.utils;

import com.pivothub.commoncore.constants.map.MapConstants;

/**
 * @Description: 高德SDK代理请求路径校验工具，检查GET及代理前缀并拒绝编码和路径规范化歧义
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public final class MapProxyPathUtil {
    private MapProxyPathUtil() {}

    public static boolean isSdkProxyGet(String path, String method) {
        if (!"GET".equalsIgnoreCase(method) || path == null) return false;
        if (path.contains("%") || path.contains("\\") || path.contains("//") || path.contains(";")) return false;
        for (String segment : path.split("/")) {
            if (".".equals(segment) || "..".equals(segment)) return false;
        }
        return path.startsWith(MapConstants.SDK_PROXY_PREFIX + "/")
                && path.length() > MapConstants.SDK_PROXY_PREFIX.length() + 1;
    }

}

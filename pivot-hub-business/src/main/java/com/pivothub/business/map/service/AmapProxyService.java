package com.pivothub.business.map.service;

import com.pivothub.business.map.utils.AmapHttpClient;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description: 高德JS API代理服务接口
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public interface AmapProxyService {
    /**
     * @Description: 校验白名单路径与参数后转发高德JS API请求
     * @param path 待校验的上游服务相对路径
     * @param query 查询参数，同名参数以数组表示
     * @param operatorId 当前登录用户标识，用于请求额度校验
     * @param requestContext 原始请求，用于校验来源及解析客户端IP
     * @return 上游状态码、内容类型及原始响应字节
     */
    AmapHttpClient.Response forward(String path, Map<String, String[]> query, String operatorId, HttpServletRequest requestContext);
}

package com.pivothub.business.map.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivothub.business.map.config.AmapProperties;
import com.pivothub.business.map.service.AmapProxyService;
import com.pivothub.business.map.utils.AmapHttpClient;
import com.pivothub.business.map.utils.AmapHttpClient.Endpoint;
import com.pivothub.business.map.utils.AmapRateLimiter;
import com.pivothub.business.map.utils.ClientIpResolver;
import com.pivothub.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class AmapProxyServiceImpl implements AmapProxyService {
    private static final Map<String, Endpoint> ALLOWED_PATHS = Map.ofEntries(
            Map.entry("/v4/map/styles", Endpoint.STYLES), Map.entry("/v3/ip", Endpoint.IP),
            Map.entry("/v5/ip", Endpoint.IP_V5), Map.entry("/v3/geocode/regeo", Endpoint.REGEO),
            Map.entry("/v3/geocode/geo", Endpoint.GEO), Map.entry("/v3/coordinate/convert", Endpoint.CONVERT),
            Map.entry("/v3/assistant/inputtips", Endpoint.TIPS), Map.entry("/v3/place/text", Endpoint.POIS),
            Map.entry("/v3/place/detail", Endpoint.POI_DETAIL));
    @Autowired
    private AmapProperties properties;
    @Autowired
    private AmapHttpClient http;
    @Autowired
    private AmapRateLimiter limiter;
    @Autowired
    private ClientIpResolver ips;
    @Autowired
    private ObjectMapper json;

    @Override
    public AmapHttpClient.Response forward(String path, Map<String, String[]> query, String operatorId, HttpServletRequest request) {
        Endpoint endpoint = ALLOWED_PATHS.get(path);
        if (endpoint == null) throw new BusinessException(404, "地图代理路径不受支持");
        String referer = validateOrigin(request);
        TreeMap<String, String> parameters = parameters(query);
        if (properties.getJs().getKey() == null || properties.getJs().getKey().isBlank()
                || properties.getJs().getSecurityJsCode() == null || properties.getJs().getSecurityJsCode().isBlank()) {
            throw new BusinessException(503, "地图服务配置不可用");
        }
        parameters.put("key", properties.getJs().getKey());
        parameters.put("jscode", properties.getJs().getSecurityJsCode());
        if (endpoint == Endpoint.IP || endpoint == Endpoint.IP_V5) {
            parameters.put("ip", ips.publicIpv4(request).orElseThrow(() -> new BusinessException(400, "无法识别公网 IPv4")));
        }
        limiter.acquire(operatorId);
        AmapHttpClient.Response response = http.get(endpoint, parameters, referer);
        checkResponse(response.bytes(), parameters.get("callback"), endpoint);
        return response;
    }

    private TreeMap<String, String> parameters(Map<String, String[]> query) {
        if (query.size() > 100) throw new BusinessException(400, "地图请求参数无效");
        TreeMap<String, String> result = new TreeMap<>();
        int total = 0;
        for (Map.Entry<String, String[]> entry : query.entrySet()) {
            String name = entry.getKey();
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.contains("url") || lower.contains("host")) throw new BusinessException(400, "地图请求参数无效");
            if (Set.of("key", "jscode", "sig").contains(lower)) continue;
            if (!name.matches("[A-Za-z0-9_]{1,64}") || entry.getValue() == null || entry.getValue().length != 1) {
                throw new BusinessException(400, "地图请求参数无效");
            }
            String value = entry.getValue()[0];
            if (value == null || value.length() > 4096 || value.chars().anyMatch(character -> character < 32 || character == 127)) {
                throw new BusinessException(400, "地图请求参数无效");
            }
            total += name.length() + value.length();
            if (total > 16384) throw new BusinessException(400, "地图请求参数无效");
            if (lower.equals("callback")) {
                if (!name.equals("callback") || value.length() > 200
                        || !value.matches("[A-Za-z_$][A-Za-z0-9_$]*(\\.[A-Za-z_$][A-Za-z0-9_$]*)*")) {
                    throw new BusinessException(400, "JSONP 回调无效");
                }
            }
            result.put(name, value);
        }
        return result;
    }

    private String validateOrigin(HttpServletRequest request) {
        AmapProperties.Proxy proxy = properties.getProxy();
        if (proxy == null || proxy.getAllowedOrigins() == null || proxy.getAllowedOrigins().isEmpty()) {
            throw new BusinessException(503, "地图代理来源配置不可用");
        }
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String originKey = origin == null ? null : allowedOrigin(origin, true);
        String refererKey = referer == null ? null : allowedOrigin(referer, false);
        if (originKey != null && refererKey != null && !originKey.equals(refererKey)) {
            throw new BusinessException(403, "地图请求来源无效");
        }
        return referer;
    }

    private String allowedOrigin(String value, boolean originHeader) {
        try {
            if (value.length() > 2048 || value.contains("\r") || value.contains("\n")) throw new IllegalArgumentException();
            URI uri = URI.create(value);
            if (uri.getRawUserInfo() != null || uri.getFragment() != null || uri.getHost() == null
                    || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || (originHeader && ((uri.getRawPath() != null && !uri.getRawPath().isEmpty()) || uri.getRawQuery() != null))) {
                throw new IllegalArgumentException();
            }
            String key = originKey(uri);
            if (properties.getProxy().getAllowedOrigins() != null) {
                for (String configured : properties.getProxy().getAllowedOrigins()) {
                    try { if (key.equals(originKey(URI.create(configured)))) return key; }
                    catch (Exception invalidConfiguration) { /* Invalid entries grant no origin access. */ }
                }
            }
        } catch (Exception invalid) {
            throw new BusinessException(403, "地图请求来源无效");
        }
        throw new BusinessException(403, "地图请求来源无效");
    }

    private String originKey(URI uri) {
        int port = uri.getPort() < 0 ? ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80) : uri.getPort();
        return uri.getScheme().toLowerCase(Locale.ROOT) + "://" + uri.getHost().toLowerCase(Locale.ROOT) + ":" + port;
    }

    private void checkResponse(byte[] bytes, String callback, Endpoint endpoint) {
        String body = new String(bytes, StandardCharsets.UTF_8);
        if (containsSecret(body, properties.getJs().getSecurityJsCode()) || containsSecret(body, properties.getWeb().getPrivateKey())
                || containsSecret(body, properties.getWeb().getKey())) {
            throw new BusinessException(503, "地图服务暂不可用");
        }
        String document = body.trim();
        if (callback != null) {
            String prefix = callback + "(";
            int closing = document.lastIndexOf(')');
            if (!document.startsWith(prefix) || closing < prefix.length()
                    || !document.substring(closing + 1).matches(";?\\s*")) throw new BusinessException(503, "地图服务响应无效");
            document = document.substring(prefix.length(), closing);
        }
        try {
            if (endpoint == Endpoint.STYLES && !document.startsWith("{")) return;
            JsonNode root = json.readTree(document);
            if (root == null || !root.isObject() || (root.has("status") && !"1".equals(root.path("status").asText()))
                    || (root.has("infocode") && !"10000".equals(root.path("infocode").asText()))) {
                throw new BusinessException(503, "地图服务暂不可用");
            }
        } catch (Exception invalid) { throw new BusinessException(503, "地图服务暂不可用"); }
    }

    private boolean containsSecret(String body, String secret) { return secret != null && !secret.isBlank() && body.contains(secret); }
}

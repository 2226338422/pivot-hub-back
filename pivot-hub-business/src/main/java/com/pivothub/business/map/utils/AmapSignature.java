package com.pivothub.business.map.utils;

import com.pivothub.common.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @Description: 高德Web服务签名工具，将请求参数按名称排序并追加私钥后生成UTF-8 MD5签名
 * @Author lhb
 * @CreateTime 2026/10/5
 */
public final class AmapSignature {
    private AmapSignature() {}

    public static String sign(Map<String, String> parameters, String privateKey) {
        if (privateKey == null || privateKey.isBlank()) throw new BusinessException(503, "地图服务配置不可用");
        TreeMap<String, String> sorted = new TreeMap<>(parameters);
        sorted.remove("sig");
        String raw = sorted.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&")) + privateKey;
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception unavailable) {
            throw new BusinessException(503, "地图服务暂不可用");
        }
    }
}

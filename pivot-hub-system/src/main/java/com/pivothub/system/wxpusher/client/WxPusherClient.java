package com.pivothub.system.wxpusher.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.system.wxpusher.config.WxPusherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * WxPusher 服务端接口客户端，负责发送消息与创建参数二维码。
 */
@Component
public class WxPusherClient {
    private static final Logger LOG = LoggerFactory.getLogger(WxPusherClient.class);

    @Autowired
    private WxPusherProperties properties;

    @Autowired
    private RestTemplate restTemplate;

    /** 给指定 uid 用户发送自定义消息，content 支持 Markdown（contentType=3）。 */
    public void sendMessage(String uid, String content) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("appToken", properties.getAppToken());
        body.put("content", content);
        body.put("summary", content.length() > 20 ? content.substring(0, 20) : content);
        body.put("contentType", 3);
        body.put("uids", Collections.singletonList(uid));
        JSONObject resp = post(properties.getSendUrl(), body);
        if (resp == null || resp.getInteger("code") == null || resp.getInteger("code") != 1000) {
            String msg = resp == null ? "未知错误" : resp.getString("msg");
            throw new BusinessException(502, "WxPusher 发送失败：" + msg);
        }
    }

    /** 创建带参数二维码，extra 携带业务标识（用户uuid），返回二维码短链接。 */
    public String createQrcode(String extra) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("appToken", properties.getAppToken());
        body.put("extra", extra);
        body.put("validTime", 1800);
        JSONObject resp = post(properties.getQrcodeUrl(), body);
        if (resp == null || resp.getInteger("code") == null || resp.getInteger("code") != 1000) {
            String msg = resp == null ? "未知错误" : resp.getString("msg");
            throw new BusinessException(502, "WxPusher 创建二维码失败：" + msg);
        }
        JSONObject data = resp.getJSONObject("data");
        String url = data == null ? null : data.getString("url");
        if (url == null || url.isBlank()) {
            throw new BusinessException(502, "WxPusher 创建二维码失败：未返回链接");
        }
        return url;
    }

    private JSONObject post(String url, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(body), headers);
            String result = restTemplate.postForObject(url, entity, String.class);
            return JSON.parseObject(result);
        } catch (Exception ex) {
            LOG.warn("调用 WxPusher 接口异常：{}", ex.getMessage());
            throw new BusinessException(502, "WxPusher 服务暂不可用，请稍后重试");
        }
    }

    private void ensureConfigured() {
        if (properties.getAppToken() == null || properties.getAppToken().isBlank()) {
            throw new BusinessException(500, "WxPusher 未配置 appToken");
        }
    }
}

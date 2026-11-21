package com.pivothub.system.wxpusher.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.pojo.vo.system.wxpusher.WxPusherQrcodeVo;
import com.pivothub.system.wxpusher.config.WxPusherProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /** 在一次请求中提交同一条消息，返回平台已受理的 UID。 */
    public Set<String> sendBatchMessage(List<String> uids, String content) {
        ensureConfigured();
        Map<String, Object> body = new HashMap<>();
        body.put("appToken", properties.getAppToken());
        body.put("content", content);
        body.put("summary", content.length() > 20 ? content.substring(0, 20) : content);
        body.put("contentType", 3);
        body.put("uids", uids);
        JSONObject resp = post(properties.getSendUrl(), body);
        if (resp == null || resp.getInteger("code") == null || resp.getInteger("code") != 1000) {
            String msg = resp == null ? "未知错误" : resp.getString("msg");
            throw new BusinessException(502, "WxPusher 发送失败：" + msg);
        }
        if (!(resp.get("data") instanceof JSONArray targets)) {
            throw new BusinessException(502, "WxPusher 未返回收件人受理结果");
        }
        Set<String> requested = new HashSet<>(uids);
        Set<String> accepted = new HashSet<>();
        for (int index = 0; index < targets.size(); index++) {
            JSONObject target = targets.getJSONObject(index);
            if (target != null && Integer.valueOf(1000).equals(target.getInteger("code"))) {
                String uid = target.getString("uid");
                if (requested.contains(uid)) {
                    accepted.add(uid);
                }
            }
        }
        return accepted;
    }

    /** 创建带参数二维码，extra 携带业务标识（用户uuid），返回二维码地址与关注短链接。 */
    public WxPusherQrcodeVo createQrcode(String extra) {
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
        WxPusherQrcodeVo vo = new WxPusherQrcodeVo();
        vo.setUrl(url);
        vo.setShortUrl(data.getString("shortUrl"));
        vo.setExpiresIn(1800);
        vo.setBound(false);
        return vo;
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

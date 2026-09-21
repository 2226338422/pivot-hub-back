package com.pivothub.system.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * QQ HTTP客户端工具类
 */
public class SysQQHttpClientUtil {

    private static JSONObject parseJSONP(String jsonp) {
        int start = jsonp.indexOf("(");
        int end = jsonp.lastIndexOf(")");
        String json = jsonp.substring(start + 1, end);
        return JSONObject.parseObject(json);
    }

    /** 获取access_token，返回格式: access_token=xxx&expires_in=xxx&refresh_token=xxx */
    public static String getAccessToken(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        String token = null;
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            if (result.indexOf("access_token") >= 0) {
                for (String part : result.split("&")) {
                    if (part.indexOf("access_token") >= 0) {
                        token = part.substring(part.indexOf("=") + 1);
                        break;
                    }
                }
            }
        }
        httpGet.releaseConnection();
        return token;
    }

    /** 获取openid，返回格式: callback({ "client_id":"xxx", "openid":"xxx" }); */
    public static String getOpenID(String url) throws IOException {
        JSONObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = parseJSONP(result);
        }
        httpGet.releaseConnection();
        return jsonObject != null ? jsonObject.getString("openid") : null;
    }

    /** 获取QQ用户信息，JSON格式 */
    public static JSONObject getUserInfo(String url) throws IOException {
        JSONObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSONObject.parseObject(result);
        }
        httpGet.releaseConnection();
        return jsonObject;
    }
}

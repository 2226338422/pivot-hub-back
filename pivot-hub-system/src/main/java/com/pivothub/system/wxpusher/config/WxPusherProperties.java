package com.pivothub.system.wxpusher.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** WxPusher 推送配置，仅保存应用凭证与接口地址，不保存业务数据。配置项在 application 与 Nacos 中维护。 */
@Data
@Component
@ConfigurationProperties(prefix = "wxpusher")
public class WxPusherProperties {
    /** 应用 appToken，空表示未配置 */
    private String appToken;
    /** 消息发送接口地址 */
    private String sendUrl;
    /** 创建参数二维码接口地址 */
    private String qrcodeUrl;
}

package com.pivothub.system.wxpusher.service;

import com.pivothub.pojo.vo.system.wxpusher.WxPusherQrcodeVo;

/** WxPusher 推送绑定。 */
public interface WxPusherService {
    /** 获取当前用户的绑定二维码链接（未绑定时创建）。 */
    WxPusherQrcodeVo qrcode();

    /** 查询当前用户是否已绑定，只查本地记录，不生成二维码、不调用 WxPusher。 */
    boolean bound();

    /** 处理 WxPusher 用户关注回调，绑定 uid 与用户。 */
    void handleSubscribe(String uid, String extra);

    /** 解除当前用户的微信通知绑定，保留已绑定的 uid 作为历史记录。 */
    void unbind();
}

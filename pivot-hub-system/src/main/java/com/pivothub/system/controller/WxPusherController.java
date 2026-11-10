package com.pivothub.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.pivothub.common.result.Result;
import com.pivothub.pojo.vo.system.wxpusher.WxPusherQrcodeVo;
import com.pivothub.system.wxpusher.service.WxPusherService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/** WxPusher 推送绑定控制器。 */
@RestController
@RequestMapping("/system/wxpusher")
public class WxPusherController {
    @Autowired
    private WxPusherService service;

    @Operation(summary = "获取绑定二维码")
    @GetMapping("/qrcode")
    public Result<WxPusherQrcodeVo> qrcode() {
        return Result.success(service.qrcode());
    }

    @Operation(summary = "查询绑定状态")
    @GetMapping("/status")
    public Result<Boolean> status() {
        return Result.success(service.bound());
    }

    @Operation(summary = "解除微信通知绑定")
    @DeleteMapping("/unbind")
    public Result<Object> unbind() {
        service.unbind();
        return Result.success();
    }

    /**
     * WxPusher 用户关注回调（对外回调，网关与拦截器放行，不校验登录）。
     * 返回固定字符串 "success" 表示处理成功。
     */
    @PostMapping("/callback")
    public String callback(@RequestBody JSONObject body) {
        String action = body.getString("action");
        JSONObject data = body.getJSONObject("data");
        if ("app_subscribe".equals(action) && data != null) {
            service.handleSubscribe(data.getString("uid"), data.getString("extra"));
        }
        return "success";
    }
}

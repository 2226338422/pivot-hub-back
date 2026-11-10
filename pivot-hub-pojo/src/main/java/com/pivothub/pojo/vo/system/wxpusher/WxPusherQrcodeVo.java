package com.pivothub.pojo.vo.system.wxpusher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: WxPusherQrcodeVo
 * @Description: WxPusher 绑定二维码结果
 * @Author: lhb
 * @Date: 2026/11/11
 */
@Data
@Schema(title = "WxPusher 绑定二维码")
public class WxPusherQrcodeVo {

    @Schema(title = "二维码图片地址，前端 img 直接渲染")
    private String url;

    @Schema(title = "关注短链接，用户复制后在微信打开即可关注")
    private String shortUrl;

    @Schema(title = "二维码有效期（秒）")
    private Integer expiresIn;

    @Schema(title = "当前是否已绑定")
    private boolean bound;
}

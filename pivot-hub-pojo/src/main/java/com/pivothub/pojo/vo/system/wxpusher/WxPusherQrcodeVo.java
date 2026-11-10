package com.pivothub.pojo.vo.system.wxpusher;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** WxPusher 绑定二维码结果。 */
@Data
@Schema(description = "WxPusher 绑定二维码")
public class WxPusherQrcodeVo {
    @Schema(description = "二维码链接，用户扫码关注后完成绑定")
    private String url;

    @Schema(description = "当前是否已绑定")
    private boolean bound;
}

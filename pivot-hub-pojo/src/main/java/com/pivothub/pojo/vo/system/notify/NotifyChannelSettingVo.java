package com.pivothub.pojo.vo.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName: NotifyChannelSettingVo
 * @Description: 单个通知渠道设置
 * @Author: lhb
 * @Date: 2026/11/10
 */
@Data
@Schema(title = "单个通知渠道设置")
public class NotifyChannelSettingVo {

    @Schema(title = "渠道标识 inbox/email/wxpusher")
    private String channel;

    @Schema(title = "渠道名称")
    private String name;

    @Schema(title = "是否开启（未绑定且需绑定的渠道回退到默认值）")
    private Boolean enabled;

    @Schema(title = "是否具备开启条件（需绑定的渠道已绑定，或无需绑定）")
    private Boolean available;

    @Schema(title = "是否需要绑定")
    private Boolean requireBind;

    @Schema(title = "渠道说明：邮箱地址或绑定状态")
    private String detail;
}

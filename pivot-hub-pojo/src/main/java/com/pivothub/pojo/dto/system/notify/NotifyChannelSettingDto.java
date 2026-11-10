package com.pivothub.pojo.dto.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName: NotifyChannelSettingDto
 * @Description: 单个通知渠道开关
 * @Author: lhb
 * @Date: 2026/11/10
 */
@Data
@Schema(title = "单个通知渠道开关")
public class NotifyChannelSettingDto {

    @NotBlank(message = "通知渠道不能为空")
    @Schema(title = "渠道标识 inbox/email/wxpusher")
    private String channel;

    @NotNull(message = "开关状态不能为空")
    @Schema(title = "是否开启")
    private Boolean enabled;
}

package com.pivothub.pojo.vo.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: NotifySettingsVo
 * @Description: 通知设置
 * @Author: lhb
 * @Date: 2026/11/8
 */
@Data
@Schema(title = "通知设置")
public class NotifySettingsVo {

    @Schema(title = "渠道开关列表")
    private List<NotifyChannelSettingVo> channels;
}

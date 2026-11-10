package com.pivothub.pojo.dto.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: NotifySettingDto
 * @Description: 通知设置保存参数
 * @Author: lhb
 * @Date: 2026/11/8
 */
@Data
@Schema(title = "通知设置保存参数")
public class NotifySettingDto {

    @Valid
    @NotEmpty(message = "通知渠道不能为空")
    @Schema(title = "渠道开关列表")
    private List<NotifyChannelSettingDto> channels;
}

package com.pivothub.pojo.dto.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 保存单项消息订阅。 */
@Data
@Schema(title = "消息订阅保存参数")
public class MessageSubscriptionSaveDto {
    @NotBlank(message = "消息类型不能为空")
    @Schema(description = "消息类型")
    private String messageType;

    @NotNull(message = "订阅开关不能为空")
    @Schema(description = "是否开启订阅")
    private Boolean enabled;
}

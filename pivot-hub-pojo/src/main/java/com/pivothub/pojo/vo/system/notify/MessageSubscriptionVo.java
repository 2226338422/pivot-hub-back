package com.pivothub.pojo.vo.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 当前用户可见的消息订阅设置。 */
@Data
@Schema(title = "消息订阅设置")
public class MessageSubscriptionVo {
    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "来源菜单 UUID")
    private String menuId;

    @Schema(description = "订阅名称")
    private String name;

    @Schema(description = "订阅说明")
    private String detail;

    @Schema(description = "当前有效订阅开关")
    private Boolean enabled;
}

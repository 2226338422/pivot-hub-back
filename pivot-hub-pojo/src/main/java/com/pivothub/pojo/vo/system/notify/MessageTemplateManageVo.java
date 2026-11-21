package com.pivothub.pojo.vo.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/** 消息模板管理列表中的一项。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "消息模板管理详情")
public class MessageTemplateManageVo extends MessageTemplateVo {
    @Schema(description = "消息模板 UUID")
    private String uuid;

    @Schema(description = "消息类型应用编码，如 public_push 或 typhoon_forecast")
    private String messageType;

    @Schema(description = "消息类型名称")
    private String messageTypeName;

    @Schema(description = "更新时间")
    private Date updateTime;
}

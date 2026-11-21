package com.pivothub.pojo.vo.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 供业务服务渲染正文的模板。 */
@Data
@Schema(title = "消息模板")
public class MessageTemplateVo {
    @Schema(description = "正文模板，支持占位符")
    private String bodyTemplate;

    @Schema(description = "可选的单条明细模板")
    private String itemTemplate;
}

package com.pivothub.pojo.dto.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 保存消息模板内容的请求。 */
@Data
@Schema(title = "保存消息模板")
public class MessageTemplateSaveDto {
    @NotBlank(message = "消息模板 UUID 不能为空")
    @Schema(description = "要编辑的消息模板 UUID")
    private String uuid;

    @NotBlank(message = "请输入正文模板")
    @Size(max = 500, message = "正文模板最多 500 字")
    @Schema(description = "正文模板，支持占位符，最多 500 字")
    private String bodyTemplate;

    @Size(max = 500, message = "单条明细模板最多 500 字")
    @Schema(description = "可选的单条明细模板，最多 500 字")
    private String itemTemplate;
}

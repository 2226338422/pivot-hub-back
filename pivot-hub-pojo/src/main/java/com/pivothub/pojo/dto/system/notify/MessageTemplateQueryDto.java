package com.pivothub.pojo.dto.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 内部服务查询消息模板的请求。 */
@Data
@Schema(title = "消息模板查询")
public class MessageTemplateQueryDto {
    @NotBlank(message = "消息类型不能为空")
    @Schema(description = "消息类型应用编码，如 public_push 或 typhoon_forecast")
    private String messageType;
}

package com.pivothub.pojo.dto.system.notify;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

/** 通用通知 MQ 事件。 */
@Data
@Schema(title = "通用通知事件")
public class NotifyEventDto {
    @NotBlank(message = "消息事件 ID 不能为空")
    @Size(max = 32)
    @Schema(description = "消息事件 ID，发送重试时复用")
    private String eventId;

    @NotBlank(message = "消息类型不能为空")
    @Size(max = 32)
    @Schema(description = "消息类型应用编码，如 public_push 或 typhoon_forecast")
    private String messageType;

    @NotBlank(message = "消息标题不能为空")
    @Size(max = 20, message = "消息标题最多 20 字")
    @Schema(description = "消息标题，最多 20 字")
    private String title;

    @NotBlank(message = "消息正文不能为空")
    @Size(max = 500, message = "消息正文最多 500 字")
    @Schema(description = "业务模块填充模板后生成的消息正文，最多 500 字")
    private String body;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonAlias("imageFileIds")
    @Schema(description = "业务模块传入的附件 UUID 列表，按展示顺序排列，可为空")
    private List<String> fileUuids;

    @Schema(description = "来源业务 ID，如公告 UUID 或台风 ID")
    private String sourceId;

    @Schema(description = "来源菜单 UUID，由消费者解析后填充")
    private String menuId;

    @Schema(description = "接收用户 UUID，缺省或空列表时按订阅规则筛选")
    private List<String> receiverUserIds;

    @NotNull
    @Schema(description = "事件发生时间，未传时由生产者补当前时间")
    private Date occurredAt;
}

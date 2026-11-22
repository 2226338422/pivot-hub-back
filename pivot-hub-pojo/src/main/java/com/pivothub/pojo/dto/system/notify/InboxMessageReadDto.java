package com.pivothub.pojo.dto.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/** 标记指定站内信为已读。 */
@Data
@Schema(title = "站内信已读请求")
public class InboxMessageReadDto {
    @NotEmpty(message = "请选择要设为已读的消息")
    @Schema(description = "待设为已读的消息 UUID 列表")
    private List<String> uuids;
}

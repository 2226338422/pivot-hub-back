package com.pivothub.pojo.dto.system.notify;

import com.pivothub.pojo.dto.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 消息模板分页查询条件。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息模板分页查询条件")
public class MessageTemplatePageDto extends PageDto {
    @Schema(description = "消息类型应用编码或名称的搜索关键词")
    private String keyWord;
}

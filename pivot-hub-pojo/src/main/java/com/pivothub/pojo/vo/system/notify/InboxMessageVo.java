package com.pivothub.pojo.vo.system.notify;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/** 站内信列表和详情使用的消息信息。 */
@Data
@Schema(title = "站内信")
public class InboxMessageVo {
    @Schema(description = "站内信 UUID")
    private String uuid;

    @Schema(description = "消息类型应用编码")
    private String messageType;

    @Schema(description = "来源菜单 UUID")
    private String menuId;

    @Schema(description = "消息投递渠道")
    private String channel;

    @Schema(description = "来源业务 ID，如公告 UUID 或台风 ID")
    private String sourceId;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息正文")
    private String body;

    @Schema(description = "站内信阅读时间，未读时为空")
    private Date readTime;

    @Schema(description = "消息创建时间")
    private Date createTime;
}

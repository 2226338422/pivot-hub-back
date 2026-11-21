package com.pivothub.pojo.po.system;

import lombok.Data;

import java.util.Date;

/** 用户消息与渠道投递记录。 */
@Data
public class SysUserMessage {
    /** 主键。 */
    private String uuid;
    /** 消息事件 ID。 */
    private String eventId;
    /** 接收用户 UUID。 */
    private String userId;
    /** 消息类型。 */
    private String messageType;
    /** 来源菜单 UUID。 */
    private String menuId;
    /** 投递渠道。 */
    private String channel;
    /** 来源业务 ID。 */
    private String sourceId;
    /** 消息标题。 */
    private String title;
    /** 消息正文。 */
    private String body;
    /** 0-待投递，1-成功，2-失败。 */
    private Integer deliveryStatus;
    /** 站内信阅读时间。 */
    private Date readTime;
    /** 记录创建时间。 */
    private Date createTime;
    /** 记录更新时间。 */
    private Date updateTime;
}

package com.pivothub.pojo.po.system;

import lombok.Data;

import java.util.Date;

/** 系统消息模板。 */
@Data
public class SysMessageTemplate {
    /** 主键。 */
    private String uuid;
    /** 消息类型。 */
    private String messageType;
    /** 支持占位符的正文模板。 */
    private String bodyTemplate;
    /** 可选的单条明细模板。 */
    private String itemTemplate;
    /** 创建时间。 */
    private Date createTime;
    /** 更新时间。 */
    private Date updateTime;
}

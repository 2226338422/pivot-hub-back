package com.pivothub.pojo.po.system;

import lombok.Data;

import java.util.Date;

/** 消息订阅设置表，包含全局默认配置和用户设置。 */
@Data
public class SysMessageSubscription {
    /** 主键。 */
    private String uuid;
    /** 用户 UUID，默认行为空。 */
    private String userId;
    /** 消息类型。 */
    private String messageType;
    /** 来源菜单 UUID。 */
    private String menuId;
    /** 订阅名称。 */
    private String name;
    /** 订阅说明。 */
    private String detail;
    /** 0-关闭，1-开启。 */
    private Integer enabled;
    /** 0-用户设置，1-全局默认。 */
    private Integer isDefault;
    /** 默认行展示顺序。 */
    private Integer sort;
    /** 创建时间。 */
    private Date createTime;
    /** 更新时间。 */
    private Date updateTime;
}

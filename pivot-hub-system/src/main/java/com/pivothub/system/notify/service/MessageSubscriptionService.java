package com.pivothub.system.notify.service;

import com.pivothub.pojo.dto.system.notify.MessageSubscriptionSaveDto;
import com.pivothub.pojo.vo.system.notify.MessageSubscriptionVo;

import java.util.List;

/** 用户消息订阅设置。 */
public interface MessageSubscriptionService {
    /** 查询当前用户有权订阅的消息类型及订阅开关。 */
    List<MessageSubscriptionVo> list();

    /** 保存当前用户对一个消息类型的订阅设置。 */
    void save(MessageSubscriptionSaveDto request);
}

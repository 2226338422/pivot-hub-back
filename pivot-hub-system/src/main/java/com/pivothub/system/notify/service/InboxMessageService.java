package com.pivothub.system.notify.service;

import com.pivothub.pojo.dto.system.notify.InboxMessagePageDto;
import com.pivothub.pojo.vo.PageVo;
import com.pivothub.pojo.vo.system.notify.InboxMessageVo;

import java.util.List;

/** 查询当前用户收到的站内信和更新阅读状态。 */
public interface InboxMessageService {
    /** 按条件分页查询当前用户收到的消息。 */
    PageVo<InboxMessageVo> page(InboxMessagePageDto query);

    /** 按 UUID 查询当前用户收到的消息详情。 */
    InboxMessageVo detail(String uuid);

    /** 将当前用户指定的未读站内信设为已读。 */
    int markRead(List<String> uuids);

    /** 将当前用户的所有未读站内信设为已读。 */
    int markAllRead();
}

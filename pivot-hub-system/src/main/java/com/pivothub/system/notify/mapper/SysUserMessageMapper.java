package com.pivothub.system.notify.mapper;

import com.pivothub.pojo.dto.system.notify.InboxMessagePageDto;
import com.pivothub.pojo.po.system.SysUserMessage;
import com.pivothub.pojo.vo.system.notify.InboxMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 用户消息及渠道投递结果。 */
@Mapper
public interface SysUserMessageMapper {
    /** 按条件查询已成功投递给用户的站内信。 */
    List<InboxMessageVo> selectInboxPage(@Param("userId") String userId, @Param("query") InboxMessagePageDto query,
                                         @Param("inboxChannel") String inboxChannel);

    /** 查询用户本人收到的指定站内信。 */
    InboxMessageVo selectInboxDetail(@Param("uuid") String uuid, @Param("userId") String userId,
                                     @Param("inboxChannel") String inboxChannel);

    /** 将指定未读站内信批量设为已读。 */
    int markInboxRead(@Param("userId") String userId, @Param("inboxChannel") String inboxChannel,
                      @Param("uuids") List<String> uuids);

    /** 将当前用户的全部未读站内信设为已读。 */
    int markAllInboxRead(@Param("userId") String userId, @Param("inboxChannel") String inboxChannel);

    /** 批量写入投递记录，重复投递保留原记录。 */
    int insertBatch(@Param("messages") List<SysUserMessage> messages);

    /** 查询本批中已成功投递的用户。 */
    List<String> selectSucceededUserIds(@Param("eventId") String eventId, @Param("channel") String channel,
                                        @Param("userIds") List<String> userIds);

    /** 批量更新投递结果，保留已成功的记录。 */
    int updateDeliveryStatusBatch(@Param("eventId") String eventId, @Param("channel") String channel,
                                  @Param("userIds") List<String> userIds, @Param("status") int status);

    /** 清理本事件中无可用地址的用户仍处于待投递状态的记录。 */
    int deletePendingBatch(@Param("eventId") String eventId, @Param("channel") String channel,
                           @Param("userIds") List<String> userIds);
}

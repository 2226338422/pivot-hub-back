package com.pivothub.system.notify.strategy;

import com.pivothub.commoncore.constants.system.NotifyMqConstants;
import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.po.system.SysUserMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 站内信渠道策略：系统自带能力，无需绑定第三方账号。
 */
@Component
@RocketMQMessageListener(topic = NotifyMqConstants.TOPIC, consumerGroup = NotifyMqConstants.INBOX_GROUP)
public class InboxNotifyStrategy extends NotifyCommonStrategy {
    @Override
    public String channel() {
        return NotifyChannel.INBOX.getValue();
    }

    @Override
    public boolean isAvailable(SysUser user, String userId) {
        return true;
    }

    /** 解析收件人后批量写入站内信。 */
    @Override
    public void onMessage(NotifyEventDto event) {
        if (prepareEvent(event)) {
            writeInboxMessages(event);
        }
    }

    /** 一次写入所有站内信并记录为投递成功，重复消息保留原记录。 */
    private void writeInboxMessages(NotifyEventDto event) {
        String channelValue = channel();
        List<SysUserMessage> messages = event.getReceiverUserIds().stream()
                .map(userId -> createMessage(event, channelValue, userId, DELIVERY_SUCCEEDED))
                .toList();
        sysUserMessageMapper.insertBatch(messages);
    }
}

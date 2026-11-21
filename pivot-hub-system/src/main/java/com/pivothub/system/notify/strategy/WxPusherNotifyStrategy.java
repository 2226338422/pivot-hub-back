package com.pivothub.system.notify.strategy;

import com.pivothub.commoncore.constants.system.NotifyMqConstants;
import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.po.system.SysUserPlatform;
import com.pivothub.system.mapper.SysUserPlatformMapper;
import com.pivothub.system.wxpusher.service.WxPusherService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * WxPusher 渠道策略：需绑定第三方，绑定信息存 sys_user_platform.wxpusher_uid。
 */
@Component
@RocketMQMessageListener(topic = NotifyMqConstants.TOPIC, consumerGroup = NotifyMqConstants.WXPUSHER_GROUP)
public class WxPusherNotifyStrategy extends NotifyCommonStrategy {

    @Autowired
    private SysUserPlatformMapper platformMapper;

    @Autowired
    private WxPusherService wxPusherService;

    @Override
    public String channel() {
        return NotifyChannel.WXPUSHER.getValue();
    }

    @Override
    public boolean isAvailable(SysUser user, String userId) {
        SysUserPlatform platform = platformMapper.selectByUserId(userId);
        return platform != null
                && Integer.valueOf(1).equals(platform.getWxpusherStatus())
                && StringUtils.hasText(platform.getWxpusherUid());
    }

    /** 解析收件人后发送 WxPusher 消息。 */
    @Override
    public void onMessage(NotifyEventDto event) {
        if (prepareEvent(event)) {
            sendWxPusherMessages(event);
        }
    }

    /** 向待投递用户批量推送同一条消息。 */
    private void sendWxPusherMessages(NotifyEventDto event) {
        sendAndRecordDelivery(event,
                userIds -> wxPusherService.sendBatchWxPusher(event.getTitle(), event.getBody(), userIds));
    }
}

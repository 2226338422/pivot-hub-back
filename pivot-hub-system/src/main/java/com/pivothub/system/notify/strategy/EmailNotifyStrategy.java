package com.pivothub.system.notify.strategy;

import com.pivothub.commoncore.constants.system.NotifyMqConstants;
import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.system.email.service.EmailService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 邮箱渠道策略：邮箱是账号自带字段，无需第三方绑定，可用性取决于是否已设置邮箱。
 */
@Component
@RocketMQMessageListener(topic = NotifyMqConstants.TOPIC, consumerGroup = NotifyMqConstants.EMAIL_GROUP)
public class EmailNotifyStrategy extends NotifyCommonStrategy {
    @Autowired
    private EmailService emailService;

    @Override
    public String channel() {
        return NotifyChannel.EMAIL.getValue();
    }

    @Override
    public boolean isAvailable(SysUser user, String userId) {
        return user != null && StringUtils.hasText(user.getEmail());
    }

    /** 解析收件人后发送邮件。 */
    @Override
    public void onMessage(NotifyEventDto event) {
        if (prepareEvent(event)) {
            sendEmails(event);
        }
    }

    /** 将同一封邮件密送给待投递用户。 */
    private void sendEmails(NotifyEventDto event) {
        sendAndRecordDelivery(event,
                userIds -> emailService.sendBatchEmail(event.getTitle(), event.getBody(), userIds));
    }
}

package com.pivothub.system.notify.strategy;

import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.po.system.SysUser;
import org.springframework.stereotype.Component;

/**
 * 站内信渠道策略：系统自带能力，无需绑定第三方账号。
 */
@Component
public class InboxNotifyStrategy implements NotifyChannelStrategy {

    @Override
    public String channel() {
        return NotifyChannel.INBOX.getValue();
    }

    @Override
    public boolean isAvailable(SysUser user, String userId) {
        return true;
    }
}

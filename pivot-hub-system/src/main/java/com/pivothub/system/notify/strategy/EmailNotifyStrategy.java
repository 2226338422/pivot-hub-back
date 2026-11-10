package com.pivothub.system.notify.strategy;

import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.po.system.SysUser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 邮箱渠道策略：邮箱是账号自带字段，无需第三方绑定，可用性取决于是否已设置邮箱。
 */
@Component
public class EmailNotifyStrategy implements NotifyChannelStrategy {

    @Override
    public String channel() {
        return NotifyChannel.EMAIL.getValue();
    }

    @Override
    public boolean isAvailable(SysUser user, String userId) {
        return user != null && StringUtils.hasText(user.getEmail());
    }
}

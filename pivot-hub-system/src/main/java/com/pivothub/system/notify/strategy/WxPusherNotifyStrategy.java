package com.pivothub.system.notify.strategy;

import com.pivothub.commoncore.enums.NotifyChannel;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.pojo.po.system.SysUserPlatform;
import com.pivothub.system.mapper.SysUserPlatformMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * WxPusher 渠道策略：需绑定第三方，绑定信息存 sys_user_platform.wxpusher_uid。
 */
@Component
public class WxPusherNotifyStrategy implements NotifyChannelStrategy {

    @Autowired
    private SysUserPlatformMapper platformMapper;

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
}

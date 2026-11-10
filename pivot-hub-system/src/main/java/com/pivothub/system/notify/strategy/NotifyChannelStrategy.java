package com.pivothub.system.notify.strategy;

import com.pivothub.pojo.po.system.SysUser;

/**
 * 通知渠道策略接口：每个通知渠道一个实现，负责该渠道的绑定判断。
 * 渠道名称、说明文案、是否需绑定等元数据由 sys_notify_setting 表提供。
 */
public interface NotifyChannelStrategy {

    /** 该策略负责的渠道标识，对应 NotifyChannel 的 value。 */
    String channel();

    /** 是否已满足开启条件（已绑定，或无需绑定）。 */
    boolean isAvailable(SysUser user, String userId);
}

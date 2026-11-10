package com.pivothub.system.notify.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知渠道策略工厂：按渠道标识索引所有 {@link NotifyChannelStrategy} 实现。
 */
@Component
public class NotifyChannelStrategyFactory {

    private final Map<String, NotifyChannelStrategy> strategies;

    @Autowired
    public NotifyChannelStrategyFactory(List<NotifyChannelStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(NotifyChannelStrategy::channel, strategy -> strategy));
    }

    /** 按渠道标识获取策略，无对应实现时返回 null。 */
    public NotifyChannelStrategy get(String channel) {
        return channel == null ? null : strategies.get(channel);
    }
}

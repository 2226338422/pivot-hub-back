package com.pivothub.system.notify.strategy;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.commoncore.constants.redis.SystemRedisConstants;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import com.pivothub.pojo.po.system.SysMessageSubscription;
import com.pivothub.pojo.po.system.SysUserMessage;
import com.pivothub.system.mapper.SysMenuMapper;
import com.pivothub.system.mapper.SystemNotifySettingMapper;
import com.pivothub.system.notify.mapper.SysMessageSubscriptionMapper;
import com.pivothub.system.notify.mapper.SysUserMessageMapper;
import jakarta.validation.Validator;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** 各通知渠道共用的收件人解析和投递记录处理。 */
public abstract class NotifyCommonStrategy implements NotifyChannelStrategy, RocketMQListener<NotifyEventDto> {
    private static final Logger LOG = LoggerFactory.getLogger(NotifyCommonStrategy.class);
    private static final int DELIVERY_PENDING = 0;
    protected static final int DELIVERY_SUCCEEDED = 1;
    private static final int DELIVERY_FAILED = 2;

    @Autowired
    private SysMessageSubscriptionMapper sysMessageSubscriptionMapper;

    @Autowired
    protected SysUserMessageMapper sysUserMessageMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SystemNotifySettingMapper systemNotifySettingMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private Validator validator;

    /** 补齐来源菜单和收件人，消息有效且有收件人时返回 true。 */
    protected final boolean prepareEvent(NotifyEventDto event) {
        //校验事件
        if (event == null || !NotifyMessageType.supports(event.getMessageType()) || !validator.validate(event).isEmpty()) {
            LOG.warn("忽略格式无效或类型未知的通知事件");
            return false;
        }
        // 补上来源菜单
        SysMessageSubscription defaults = sysMessageSubscriptionMapper.selectDefault(event.getMessageType());
        if (defaults == null) {
            LOG.warn("未配置通知默认订阅 eventId={} messageType={}", event.getEventId(), event.getMessageType());
            throw new IllegalStateException("通知类型缺少默认订阅配置");
        }
        event.setMenuId(defaults.getMenuId());
        List<String> specifiedIds = event.getReceiverUserIds();
        List<String> candidates = specifiedIds == null || specifiedIds.isEmpty()
                ? null
                : new ArrayList<>(new LinkedHashSet<>(specifiedIds));
        //处理收件人
        //查谁订阅了这类消息
        List<String> subscribedUsers = sysMessageSubscriptionMapper.selectSubscribedUserIds(
                defaults.getMessageType(), defaults.getEnabled(), candidates);
        if (subscribedUsers.isEmpty()) {
            event.setReceiverUserIds(new ArrayList<>());
            return false;
        }
        //查订阅用户里谁有来源菜单权限
        List<String> permittedUsers = sysMenuMapper.selectUserIdsWithMenuAccess(defaults.getMenuId(), subscribedUsers);
        //查有权限的人里谁开启了当前渠道
        List<String> userIds = permittedUsers.isEmpty()
                ? new ArrayList<>()
                : systemNotifySettingMapper.selectEnabledChannelUserIds(channel(), permittedUsers);
        event.setReceiverUserIds(userIds);
        return !userIds.isEmpty();
    }

    /** 按事件和渠道加锁，一次投递全部待发送用户并记录结果。 */
    protected final void sendAndRecordDelivery(NotifyEventDto event, Function<List<String>, Map<String, Boolean>> send) {
        String channelValue = channel();
        String lockKey = SystemRedisConstants.NOTIFY_DELIVERY_LOCK_KEY_PREFIX + event.getEventId() + ":" + channelValue;
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new IllegalStateException("同一通知渠道正在投递");
        }
        try {
            sendToPendingRecipients(event, channelValue, event.getReceiverUserIds(), send);
        } catch (RuntimeException failure) {
            LOG.warn("通知投递失败 eventId={} channel={} errorType={}",
                    event.getEventId(), channelValue, failure.getClass().getSimpleName());
            throw failure;
        } finally {
            lock.unlock();
        }
    }

    /** 跳过已提交的用户，保存本次投递结果并清理无可用地址的待投递记录。 */
    private void sendToPendingRecipients(NotifyEventDto event, String channel, List<String> userIds,
                                         Function<List<String>, Map<String, Boolean>> send) {
        String eventId = event.getEventId();
        Set<String> succeeded = new HashSet<>(sysUserMessageMapper.selectSucceededUserIds(eventId, channel, userIds));
        List<String> pendingUserIds = userIds.stream().filter(userId -> !succeeded.contains(userId)).toList();
        if (pendingUserIds.isEmpty()) {
            return;
        }
        List<SysUserMessage> pendingMessages = pendingUserIds.stream()
                .map(userId -> createMessage(event, channel, userId, DELIVERY_PENDING)).toList();
        //数据库插入待投递的记录
        sysUserMessageMapper.insertBatch(pendingMessages);
        //执行发送消息逻辑
        Map<String, Boolean> results = send.apply(pendingUserIds);

        List<String> acceptedUserIds = new ArrayList<>();
        List<String> failedUserIds = new ArrayList<>();
        List<String> skippedUserIds = new ArrayList<>();
        for (String userId : pendingUserIds) {
            Boolean accepted = results.get(userId);
            if (accepted == null) {
                skippedUserIds.add(userId);
            } else if (accepted) {
                acceptedUserIds.add(userId);
            } else {
                failedUserIds.add(userId);
            }
        }
        if (!acceptedUserIds.isEmpty() && sysUserMessageMapper.updateDeliveryStatusBatch(
                eventId, channel, acceptedUserIds, DELIVERY_SUCCEEDED) != acceptedUserIds.size()) {
            throw new IllegalStateException("通知发送结果未保存");
        }
        if (!skippedUserIds.isEmpty()) {
            sysUserMessageMapper.deletePendingBatch(eventId, channel, skippedUserIds);
        }
        if (!failedUserIds.isEmpty()) {
            sysUserMessageMapper.updateDeliveryStatusBatch(eventId, channel, failedUserIds, DELIVERY_FAILED);
            throw new IllegalStateException("部分通知提交失败");
        }
    }

    /**
     * @Description: 构造一次事件对一个用户和渠道的投递记录
     * @param event
     * @param channel
     * @param userId
     * @param status
     * @return SysUserMessage
     * @Author lhb
     * @CreateTime 2026/11/21 16:44
     */
    protected final SysUserMessage createMessage(NotifyEventDto event, String channel, String userId, int status) {
        SysUserMessage message = new SysUserMessage();
        message.setUuid(IdWorker.getIdStr());
        message.setEventId(event.getEventId());
        message.setUserId(userId);
        message.setMessageType(event.getMessageType());
        message.setMenuId(event.getMenuId());
        message.setChannel(channel);
        message.setSourceId(event.getSourceId());
        message.setTitle(event.getTitle());
        message.setBody(event.getBody());
        message.setDeliveryStatus(status);
        return message;
    }
}

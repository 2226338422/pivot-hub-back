package com.pivothub.common.notify.event;

import com.pivothub.common.notify.mq.NotifyMqProducer;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotifyEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(NotifyEventListener.class);

    @Autowired
    private NotifyMqProducer notifyMqProducer;

    /** 业务事务提交后，将受支持的通知事件发送到 MQ。 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotifyEvent(NotifyEventDto event) {
        if (!NotifyMessageType.supports(event.getMessageType())) {
            return;
        }
        try {
            notifyMqProducer.send(event);
        } catch (RuntimeException failure) {
            LOG.warn("通知发送失败 messageType={} sourceId={} eventId={}",
                    event.getMessageType(), event.getSourceId(), event.getEventId(), failure);
        }
    }
}

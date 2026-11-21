package com.pivothub.common.notify.mq;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.pivothub.common.exception.SystemException;
import com.pivothub.commoncore.constants.system.NotifyMqConstants;
import com.pivothub.commoncore.enums.NotifyMessageType;
import com.pivothub.pojo.dto.system.notify.NotifyEventDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/** 业务服务和系统服务共享的通知生产者。 */
@Component
public class NotifyMqProducer {
    @Autowired
    private ObjectProvider<RocketMQTemplate> rocketMqTemplateProvider;

    @Autowired
    private Validator validator;

    /** 发送包含业务标题、正文和附件 UUID 的通知事件，返回投递去重使用的事件 ID。 */
    public String send(NotifyEventDto event) {
        if (event == null) {
            throw new SystemException(400, "通知事件不能为空");
        }
        if (event.getEventId() == null) {
            event.setEventId(IdWorker.getIdStr());
        }
        if (event.getOccurredAt() == null) {
            event.setOccurredAt(new Date());
        }
        //校验dto
        Set<ConstraintViolation<NotifyEventDto>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .sorted()
                    .collect(Collectors.joining("，"));
            throw new SystemException(400, message);
        }
        if (!NotifyMessageType.supports(event.getMessageType())) {
            throw new SystemException(400, "不支持的通知消息类型");
        }

        RocketMQTemplate rocketMqTemplate = rocketMqTemplateProvider.getIfAvailable();
        if (rocketMqTemplate == null || rocketMqTemplate.getProducer() == null) {
            throw new SystemException(503, "当前服务未配置 RocketMQ 生产者");
        }
        String sendFailureMessage = "通知消息未确认发送成功，eventId=" + event.getEventId();
        SendResult result;
        try {
            result = rocketMqTemplate.syncSend(NotifyMqConstants.TOPIC, MessageBuilder.withPayload(event)
                    .setHeader(RocketMQHeaders.KEYS, event.getEventId())
                    .build());
        } catch (MessagingException failure) {
            SystemException exception = new SystemException(503, sendFailureMessage);
            exception.initCause(failure);
            throw exception;
        }
        if (result == null || result.getSendStatus() != SendStatus.SEND_OK) {
            throw new SystemException(503, sendFailureMessage);
        }
        return event.getEventId();
    }
}

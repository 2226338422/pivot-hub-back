package com.pivothub.commoncore.constants.system;

/** 通用通知的 RocketMQ topic 和消费者组。 */
public final class NotifyMqConstants {
    public static final String TOPIC = "pivot-hub-notify";
    public static final String INBOX_GROUP = "pivot-hub-notify-inbox";
    public static final String EMAIL_GROUP = "pivot-hub-notify-email";
    public static final String WXPUSHER_GROUP = "pivot-hub-notify-wxpusher";

    private NotifyMqConstants() {
    }
}

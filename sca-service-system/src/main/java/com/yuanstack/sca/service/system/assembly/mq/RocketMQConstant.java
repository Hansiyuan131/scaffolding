package com.yuanstack.sca.service.system.assembly.mq;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: RocketMQConstant
 * @author: hansiyuan
 * @date: 2022/6/29 2:34 PM
 */
@Getter
public class RocketMQConstant {

    @Getter
    @Setter
    private volatile static ConcurrentHashMap<String, String> topicMap = new ConcurrentHashMap<>();

    @Getter
    public enum TOPIC {
        TOPIC_MESSAGE("MESSAGE_TOPIC", "GID_MESSAGE", "延时消息中心"),
        TOPIC_COMMON_MESSAGE("COMMON_MESSAGE_TOPIC", "GID_COMMON_MESSAGE", "普通消息中心");

        private final String topic;
        private final String groupId;
        private final String desc;

        TOPIC(String topic, String groupId, String desc) {
            this.topic = topic;
            this.groupId = groupId;
            this.desc = desc;
        }
    }

    @Getter
    public enum TAG {
        TOPIC_DEFAULT("", "默认"),
        TOPIC_EMAIL_MSG_TAG("EmailMsg", "邮件消息"),
        TOPIC_PHONE_MSG_TAG("PhoneMsg", "短信消息"),
        TOPIC_ALIPAY_MSG_TAG("AlipayMsg", "支付宝消息");

        private final String tag;
        private final String desc;

        TAG(String tag, String desc) {
            this.tag = tag;
            this.desc = desc;
        }

        /**
         * 获取tag
         */
        public static TAG getTag(String tag) {
            for (TAG t : TAG.values()) {
                if (t.getTag().equals(tag)) {
                    return t;
                }
            }
            return null;
        }
    }

}


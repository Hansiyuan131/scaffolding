package com.yuanstack.sca.service.system.assembly.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.Properties;

/**
 * @description: BaseProducer
 * @author: hansiyuan
 * @date: 2022/6/29 2:50 PM
 */
@Slf4j
public class BaseProducer {

    @Resource
    private SystemProducer systemProducer;

    @Resource
    private TransactionProducer transactionProducer;

    private String topic;

    private String tag;

    /**
     * 初始化生产者
     */
    @PostConstruct
    protected void initProducer() {
        MQProducer mqProducer = this.getClass().getAnnotation(MQProducer.class);
        if (mqProducer == null) {
            throw new RuntimeException("RocketMQ请配置注解MQProducer");
        }
        RocketMQConstant.TOPIC rocketMQEnum = mqProducer.value();
        RocketMQConstant.TAG tagEnum = mqProducer.tag();

        topic = rocketMQEnum.getTopic();
        tag = tagEnum.getTag();
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "初始化生产者 topic", topic, "tag", tag);
    }

    /**
     * 发送事务消息
     */
    public String sendTransactionMsg(String str, String key, LocalTransactionExecuter executer) {
        Message message = new Message(topic, tag, key, str.getBytes());
        SendResult send = transactionProducer.send(message, executer, null);
        return send != null ? send.getMessageId() : null;
    }

    /**
     * 发送事务消息
     */
    public String sendTransactionMsg(String str, LocalTransactionExecuter executer) {
        return sendTransactionMsg(str, null, executer);
    }

    /**
     * 发送消息
     */
    public String sendMessage(String str) {
        return sendMessage(str, null);
    }

    public String sendMessage(String str, String key) {
        return sendMessage(str, key, null);
    }

    /**
     * 发送消息
     */
    public String sendMessage(String str, String key, Properties userProperties) {
        if (str == null) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "消息不能为null");
            return null;
        }

        Message message = new Message(topic, tag, key, str.getBytes());
        if (userProperties != null) {
            message.setUserProperties(userProperties);
        }

        SendResult sendResult = systemProducer.send(message);
        String messageId = sendResult != null ? sendResult.getMessageId() : null;

        LogUtils.info(log, ModelEnum.COMMON_UTILS, "发送普通消息 topic", topic, "tag", tag, "key", key, "str", str, "messageId", messageId);

        return messageId;
    }

    /**
     * 发送异步消息
     */
    public void sendCallback(String str, SendCallback sendCallback) {
        if (str == null || sendCallback == null) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "参数不能为空");
            return;
        }
        this.sendCallback(str, sendCallback, null);
    }

    /**
     * 发送异步消息
     */
    public void sendCallback(String str, SendCallback sendCallback, String key) {
        if (str == null || sendCallback == null) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "参数不能为空");
            return;
        }

        LogUtils.info(log, ModelEnum.COMMON_UTILS, "发送异步消息", topic, tag, key, str);

        systemProducer.sendAsync(new Message(topic, tag, key, str.getBytes()), sendCallback);
    }

    /**
     * 发送延迟消息
     * @param timeLater 多少毫秒后发送
     */
    public String sendDelayMessage(String str, String key, long timeLater) {
        if (str == null) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "参数不能为空");
            return null;
        }

        Message message = new Message(topic, tag, key, str.getBytes());
        message.setStartDeliverTime(System.currentTimeMillis() + timeLater);
        SendResult sendResult = systemProducer.send(message);

        String messageId = sendResult != null ? sendResult.getMessageId() : null;

        LogUtils.info(log, ModelEnum.COMMON_UTILS, "发送延时消息", topic, tag, key, str, messageId);

        return messageId;
    }

    /**
     * 发送延迟消息
     */
    public String sendDelayMessage(String str, long timeLater) {
        return sendDelayMessage(str, null, timeLater);
    }

    /**
     * 发送延迟消息
     */
    public String sendDelayMessage(String str, String key, Date date) {
        if (date == null) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "参数不合法");
            return null;
        }
        return sendDelayMessage(str, key, date.getTime());
    }

    /**
     * 发送延迟消息
     */
    public String sendDelayMessage(String str, Date date) {
        if (date == null) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "参数不合法");
            return null;
        }
        return sendDelayMessage(str, date.getTime() - System.currentTimeMillis());
    }

}

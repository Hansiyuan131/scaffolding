package com.yuanstack.sca.service.system.assembly.mq;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_UTILS;

/**
 * @description: AbstractBaseConsumer
 * @author: hansiyuan
 * @date: 2022/6/29 2:39 PM
 */
@Slf4j
@Setter
public abstract class AbstractBaseConsumer extends ConsumerBean {

    private String topic;

    private String tag;

    private String groupId;

    @Resource
    private RocketMQConfig rocketMQConfig;

    /**
     * 初始化消费者实例
     */
    @PostConstruct
    private void initConsumerInstance() {
        LogUtils.info(log, COMMON_UTILS, "启动RocketMQ消费者");

        // 初始化
        initConsumer();

        // 校验是否存在相同topic
        String className = RocketMQConstant.getTopicMap().get(topic);
        String curClassName = this.getClass().getSimpleName();
        if (className != null) {
            throw new RuntimeException("topic:" + topic + "在" + className + "中已被注册，不能在" + curClassName + "中重新注册");
        }else {
            RocketMQConstant.getTopicMap().put(topic, curClassName);
        }

        Properties properties = rocketMQConfig.getBaseProperties();
        properties.setProperty(PropertyKeyConst.GROUP_ID, groupId);

        super.setProperties(properties);
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(topic);
        subscription.setExpression(tag);

        subscriptionTable.put(subscription, (message, consumeContext) -> {
            try {
                String msg = new String(message.getBody());
                LogUtils.info(log, COMMON_UTILS, "消费消息,topic:", message.getTopic(), "tag", message.getTag(), "接收消息", msg);
                consumerMsg(message);
                return Action.CommitMessage;
            } catch (Exception e) {
                LogUtils.error(log, COMMON_UTILS, "消费消息异常", e);
                return Action.ReconsumeLater;
            }
        });
        super.setSubscriptionTable(subscriptionTable);

        super.start();
        LogUtils.info(log, COMMON_UTILS, "启动RocketMQ消费者成功");
    }

    @PreDestroy
    private void destroy() {
        super.shutdown();
        LogUtils.info(log, COMMON_UTILS, "关闭RocketMQ消费者成功");
    }
    /**
     * 初始化参数
     */
    protected abstract void initConsumer();

    protected abstract void consumerMsg(Message message);
}


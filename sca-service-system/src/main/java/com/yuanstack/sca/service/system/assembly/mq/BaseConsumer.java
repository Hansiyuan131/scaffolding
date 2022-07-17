package com.yuanstack.sca.service.system.assembly.mq;

import org.apache.commons.lang3.StringUtils;

/**
 * @description: BaseConsumer
 * @author: hansiyuan
 * @date: 2022/6/29 2:40 PM
 */
public abstract class BaseConsumer extends AbstractBaseConsumer {

    @Override
    protected void initConsumer() {
        MQConsumer mqConsumer = this.getClass().getAnnotation(MQConsumer.class);

        if (mqConsumer == null) {
            throw new RuntimeException("RocketMQ请配置注解MQConsumer");
        }

        RocketMQConstant.TOPIC topicEnum = mqConsumer.value();
        RocketMQConstant.TAG[] tagsEnum = mqConsumer.tags();
        String groupId = topicEnum.getGroupId();
        String topic = topicEnum.getTopic();

        String tag;
        if (tagsEnum.length > 1) {
            StringBuilder tagSb = new StringBuilder();
            for (RocketMQConstant.TAG value : tagsEnum) {
                tagSb.append(value.getTag()).append("||");
            }
            tagSb.delete(tagSb.length() - 2, tagSb.length());
            tag = tagSb.toString();
        } else {
            tag = tagsEnum[0].getTag();
        }

        if (StringUtils.isAnyBlank(groupId, topic)) {
            throw new IllegalArgumentException("初始化消费者参数异常");
        }

        super.setGroupId(groupId);
        super.setTopic(topic);
        super.setTag(tag);
    }


}


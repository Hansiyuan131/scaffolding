package com.yuanstack.sca.service.system.assembly.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: MsgProducer
 * @author: hansiyuan
 * @date: 2022/6/29 2:58 PM
 */
@Slf4j
@Component
@MQProducer(value = RocketMQConstant.TOPIC.TOPIC_MESSAGE, tag = RocketMQConstant.TAG.TOPIC_PHONE_MSG_TAG)
public class MsgProducer extends BaseProducer {

}

package com.yuanstack.sca.service.system.assembly.mq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.yuanstack.sca.service.system.assembly.mq.RocketMQConstant.TAG.TOPIC_DEFAULT;

/**
 * @description: MQProducer
 * @author: hansiyuan
 * @date: 2022/6/29 2:34 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MQProducer {

    RocketMQConstant.TOPIC value();

    RocketMQConstant.TAG tag() default TOPIC_DEFAULT;

}

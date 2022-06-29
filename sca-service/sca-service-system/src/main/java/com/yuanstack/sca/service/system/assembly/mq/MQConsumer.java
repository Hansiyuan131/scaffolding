package com.yuanstack.sca.service.system.assembly.mq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: MQConsumer
 * @author: hansiyuan
 * @date: 2022/6/29 2:33 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MQConsumer {

    RocketMQConstant.TOPIC value();

    RocketMQConstant.TAG[] tags() default {};
}

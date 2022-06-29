package com.yuanstack.sca.service.system.assembly.flowlimit;

import java.lang.annotation.*;

/**
 * @description: FlowLimit
 * @author: hansiyuan
 * @date: 2022/6/29 2:32 PM
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface FlowLimit {

    String value();

    String key() default "";

    /**
     * 限流时间范围，单位：秒
     *
     * @return
     */
    int duration() default -1;

    /**
     * 最大限流
     *
     * @return
     */
    long maxLimit() default 1;
}


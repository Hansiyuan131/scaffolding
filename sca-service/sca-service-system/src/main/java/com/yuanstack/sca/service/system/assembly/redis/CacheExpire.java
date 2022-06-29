package com.yuanstack.sca.service.system.assembly.redis;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: CacheExpire
 * @author: hansiyuan
 * @date: 2022/6/29 2:32 PM
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheExpire {

    int value() default -1;

    /**
     * 过期时间 秒
     *
     * @return
     */
    @AliasFor("value")
    int expire() default -1;
}

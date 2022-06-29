package com.yuanstack.sca.service.system.assembly.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: Idempotent
 * @author: hansiyuan
 * @date: 2022/6/29 2:04 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Idempotent {

    long timeout() default -1L;

    Class<?> strategy() ;
}

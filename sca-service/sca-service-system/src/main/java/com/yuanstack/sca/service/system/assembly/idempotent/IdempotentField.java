package com.yuanstack.sca.service.system.assembly.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: IdempotentField
 * @author: hansiyuan
 * @date: 2022/6/29 2:04 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentField {

}

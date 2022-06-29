package com.yuanstack.sca.service.system.assembly.desensitize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 环境配置
 * @author: hansiyuan
 * @date: 2022/6/29 2:46 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitization {

    /**
     * 脱敏描述
     */
    DesensitizeType type() default DesensitizeType.DEFAULT;

}

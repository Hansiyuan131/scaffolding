package com.yuanstack.sca.service.system.assembly.desensitize;

import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextValueFilter;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_DESENSITIZE;

/**
 * @description: 脱敏过滤
 * @author: hansiyuan
 * @date: 2022/6/29 11:23 AM
 */
@Slf4j
public class DesensitizeFilter implements ContextValueFilter {
    @Override
    public Object process(BeanContext context, Object object, String name, Object value) {
        if (!(value instanceof String) || ((String) value).length() == 0) {
            return value;
        }
        try {
            if (context != null) {
                Field field = context.getField();
                Desensitization desensitization;
                if (field != null) {
                    if (String.class != field.getType() || (desensitization = field.getAnnotation(Desensitization.class)) == null) {
                        return value;
                    }
                    DesensitizeType type = desensitization.type();
                    DesensitizeStrategy strategy = DesensitizeType.getDescByType(type.getType());
                    if (strategy != null) {
                        return strategy.encryptToStr((String) value);
                    }
                }
            }
            return value;


        } catch (Exception e) {
            LogUtils.warn(log, COMMON_DESENSITIZE, "DesensitizeFilter the class {} has no field {}", object.getClass(), name);
            LogUtils.info(log, COMMON_DESENSITIZE, "异常信息", e);
        }
        return value;
    }
}


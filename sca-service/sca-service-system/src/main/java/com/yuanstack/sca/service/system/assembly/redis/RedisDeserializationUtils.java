package com.yuanstack.sca.service.system.assembly.redis;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @description: Redis序列号工具类
 * @author: hansiyuan
 * @date: 2022/6/29 11:37 AM
 */
public class RedisDeserializationUtils {
    public static <T> T parseObject(Serializable serializable, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(serializable), clazz);
    }
}

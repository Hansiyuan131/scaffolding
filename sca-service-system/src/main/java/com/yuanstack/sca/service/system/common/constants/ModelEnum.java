package com.yuanstack.sca.service.system.common.constants;

import lombok.Getter;

/**
 * @description: 系统模块枚举
 * @author: hansiyuan
 * @date: 2022/6/28 4:05 PM
 */
@Getter
public enum ModelEnum {

    // 业务模块
    BIZ_USER("User", "用户模块"),

    // 组件
    ASSEMBLY_ROCKETMQ("RocketMQ", "rocketMQ模块"),

    // 工具类
    COMMON_UTILS("Utils", "工具类"),
    COMMON_MONITOR("Monitor", "监控"),
    COMMON_DESENSITIZE("Desensitize", "脱敏处理"),
    COMMON_THREAD("Thread", "多线程异步处理"),
    COMMON_OSS("OSS", "存储");

    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    ModelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

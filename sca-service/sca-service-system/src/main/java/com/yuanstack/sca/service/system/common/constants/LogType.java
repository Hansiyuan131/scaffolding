package com.yuanstack.sca.service.system.common.constants;

import lombok.Getter;

/**
 * @description: 日志类型
 * @author: hansiyuan
 * @date: 2022/6/28 4:00 PM
 */
@Getter
public enum LogType {

    DEBUG("debug", 1),
    INFO("info", 2),
    WARN("warn", 3),
    ERROR("error", 4);

    private final String value;
    private final int priority;

    LogType(String value, int priority) {
        this.value = value;
        this.priority = priority;
    }
}

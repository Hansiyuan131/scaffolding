package com.yuanstack.sca.service.system.assembly.monitor;

import lombok.Getter;

/**
 * @description: 监控类型
 * @author: hansiyuan
 * @date: 2022/6/29 11:04 AM
 */
@Getter
public enum MonitorType {
    RT("RT", "响应时间"),
    QPS("QPS", "QPS");

    private final String type;
    private final String desc;

    MonitorType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}

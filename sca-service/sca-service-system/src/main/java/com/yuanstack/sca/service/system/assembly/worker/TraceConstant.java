package com.yuanstack.sca.service.system.assembly.worker;

import java.util.UUID;

/**
 * @description: 链路日志常量
 * @author: hansiyuan
 * @date: 2022/6/29 11:29 AM
 */
public class TraceConstant {
    public static final String TRACE_ID = "traceId";

    public TraceConstant() {
    }

    /**
     * 生成traceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

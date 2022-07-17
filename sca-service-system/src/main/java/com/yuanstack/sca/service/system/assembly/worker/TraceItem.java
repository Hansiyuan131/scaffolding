package com.yuanstack.sca.service.system.assembly.worker;

import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * @description: 链路信息，MDC中存放的记录，便于查询日志
 * @author: hansiyuan
 * @date: 2022/6/29 11:28 AM
 */

@Data
public class TraceItem implements Serializable {

    private static final long serialVersionUID = 7555643650112381108L;

    /**
     * 请求traceId
     **/
    private String traceId;

    public TraceItem(String traceId) {
        this.traceId = traceId;
    }

    /**
     * 基于当前MDC创建item
     */
    public static TraceItem createByCurrentMDC() {
        return new TraceItem(MDC.get(TraceConstant.TRACE_ID));
    }

    /**
     * 往当前线程的MDC中放值
     */
    public void putAll() {
        MDC.put(TraceConstant.TRACE_ID, getTraceId());
    }

    /**
     * 移除MDC中的值
     */
    public void removeAll() {
        MDC.remove(TraceConstant.TRACE_ID);
    }
}

package com.yuanstack.sca.service.system.assembly.worker;

import com.yuanstack.sca.service.system.common.log.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_THREAD;

/**
 * @description: 有返回值的线程
 * @author: hansiyuan
 * @date: 2022/6/29 11:27 AM
 */
public abstract class AbsCallableWorker<V> implements Callable<V> {
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public V call() throws Exception {
        TraceItem item = getTraceItem();
        try {
            item.putAll();
            return execute();
        } catch (Throwable e) {
            LogUtils.error(log, COMMON_THREAD, "线程运行异常", e);
            throw e;
        } finally {
            item.removeAll();
        }
    }

    /**
     * 返回追踪日志信息
     */
    protected abstract TraceItem getTraceItem();

    /**
     * 执行线程方法-有返回值
     */
    protected abstract V execute();
}
package com.yuanstack.sca.service.system.assembly.worker;

import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_THREAD;

/**
 * @description: 没有返回值的线程
 * @author: hansiyuan
 * @date: 2022/6/29 11:31 AM
 */
public abstract class AbsRunnableWorker implements Runnable {
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        TraceItem item = getTraceItem();
        try {
            item.putAll();
            execute();
        } catch (Throwable e) {
            LogUtils.error(log, COMMON_THREAD, "线程运行异常", e);
        } finally {
            item.removeAll();
        }
    }

    /**
     * 返回追踪日志信息
     */
    protected abstract TraceItem getTraceItem();

    /**
     * 执行线程方法
     */
    protected abstract void execute();
}


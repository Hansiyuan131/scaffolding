package com.yuanstack.sca.service.system.assembly.worker;

import com.yuanstack.sca.service.system.common.log.LogUtils;

import java.util.Random;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_THREAD;

/**
 * @description: DemoRunnableWorker
 * @author: hansiyuan
 * @date: 2022/6/29 11:33 AM
 */
public class DemoRunnableWorker extends AbsRunnableWorker {

    private final TraceItem traceItem;

    public DemoRunnableWorker() {
        this.traceItem = TraceItem.createByCurrentMDC();
    }

    @Override
    protected TraceItem getTraceItem() {
        return traceItem;
    }

    @Override
    protected void execute() {
        int nextInt = new Random().nextInt(5);
        LogUtils.info(log, COMMON_THREAD, "Thread execute... " + Thread.currentThread().getName() + " random:" + nextInt);
        if (nextInt == 3) {
            try {
                int i = 1 / 0;
            } catch (Exception e) {
                LogUtils.error(log, COMMON_THREAD, "线程异常测试,Thread:{}", Thread.currentThread().getName(), e);
            }
        }
    }
}


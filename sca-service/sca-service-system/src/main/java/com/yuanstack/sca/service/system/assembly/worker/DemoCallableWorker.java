package com.yuanstack.sca.service.system.assembly.worker;

import com.yuanstack.sca.service.system.common.log.LogUtils;

import java.util.Random;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_THREAD;

/**
 * @description: DemoCallableWorker
 * @author: hansiyuan
 * @date: 2022/6/29 11:32 AM
 */
public class DemoCallableWorker extends AbsCallableWorker<String> {

    private final TraceItem traceItem;

    public DemoCallableWorker() {
        this.traceItem = TraceItem.createByCurrentMDC();
    }

    @Override
    protected TraceItem getTraceItem() {
        return traceItem;
    }

    @Override
    protected String execute() {
        int nextInt = new Random().nextInt(5);
        LogUtils.info(log, COMMON_THREAD, "Thread execute... " + Thread.currentThread().getName() + " random:" + nextInt);
        if (nextInt == 3) {
            throw new RuntimeException("线程异常测试,Thread:" + Thread.currentThread().getName());
        }
        return Thread.currentThread().getName();
    }
}

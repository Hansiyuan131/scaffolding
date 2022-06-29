package com.yuanstack.sca.service.system.assembly.monitor;

/**
 * @description: 监控QPS
 * @author: hansiyuan
 * @date: 2022/6/29 11:14 AM
 */
public class MonitorQps extends AbstractMonitorKey {

    MonitorQps(String key1, String[] key2, String key3) {
        super(key1, key2, key3);
    }

    MonitorQps(String key1, String key2, String key3) {
        super(key1, key2, key3);
    }

    public void record() {
        super.setValue1AndValue2(1, 0);
    }

    @Override
    protected MonitorType getKeyType() {
        return MonitorType.QPS;
    }
}

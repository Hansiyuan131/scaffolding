package com.yuanstack.sca.service.system.assembly.monitor;

/**
 * @description: 监控响应时间
 * @author: hansiyuan
 * @date: 2022/6/29 11:06 AM
 */
public class MonitorRT extends AbstractMonitorKey {

    MonitorRT(String key1, String[] key2, String key3) {
        super(key1, key2, key3);
    }

    MonitorRT(String key1, String key2, String key3) {
        super(key1, key2, key3);
    }

    public void record(long startTimeStamp) {
        super.setValue1AndValue2(System.currentTimeMillis() - startTimeStamp, 1);
    }

    public void recordTotalUsed(long totalUsedInMs) {
        super.setValue1AndValue2(totalUsedInMs, 1);
    }

    public void recordIfSlower(long startTimeStamp, int slowLimit) {
        long used = System.currentTimeMillis() - startTimeStamp;
        if (used >= slowLimit) {
            super.setValue1AndValue2(used, 1);
        }
    }

    public void recordTimeUsed(long totalUsedInMs) {
        super.setValue1AndValue2(totalUsedInMs, 1);
    }

    @Override
    protected MonitorType getKeyType() {
        return MonitorType.RT;
    }
}

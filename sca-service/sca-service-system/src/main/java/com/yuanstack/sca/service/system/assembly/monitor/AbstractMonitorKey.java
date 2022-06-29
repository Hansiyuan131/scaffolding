package com.yuanstack.sca.service.system.assembly.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 监控Key
 * @author: hansiyuan
 * @date: 2022/6/29 11:07 AM
 */
public abstract class AbstractMonitorKey {
    protected List<MonitorKeys> monitorKey = new ArrayList<>();

    protected abstract MonitorType getKeyType();

    public String[] getMonitorKeys() {
        if (monitorKey != null) {
            MonitorKeys monitorKeys = monitorKey.get(0);
            if (monitorKeys != null) {
                return new String[] {monitorKeys.getKey1(), monitorKeys.getKey2(), monitorKeys.getKey3()};
            }
        }
        return null;
    }

    AbstractMonitorKey(String key1, String[] key2, String key3) {
        for (String k2 : key2) {
            MonitorKeys k = new MonitorKeys(getKeyType().getType(), key1, k2, key3);
            monitorKey.add(k);
        }
    }

    AbstractMonitorKey(String key1, String key2, String key3) {
        MonitorKeys k = new MonitorKeys(getKeyType().getType(), key1, key2, key3);
        monitorKey.add(k);
    }

    protected void setValue1(long v) {
        int size = monitorKey.size();
        for (int i = 0; i < size; i++) {
            MonitorLog.addStat(monitorKey.get(i), v, 0);
        }
    }

    protected void setValue1AndValue2(long v1, long v2) {
        int size = monitorKey.size();
        for (int i = 0; i < size; i++) {
            MonitorLog.addStat(monitorKey.get(i), v1, v2);
        }
    }

    protected void setValue2(long v) {
        int size = monitorKey.size();
        for (int i = 0; i < size; i++) {
            MonitorLog.addStat(monitorKey.get(i), 0, v);
        }
    }
}

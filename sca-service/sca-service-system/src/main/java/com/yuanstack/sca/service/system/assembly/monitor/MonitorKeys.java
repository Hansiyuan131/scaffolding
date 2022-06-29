package com.yuanstack.sca.service.system.assembly.monitor;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 监控Key
 * @author: hansiyuan
 * @date: 2022/6/29 11:12 AM
 */
@Getter
@Setter
public class MonitorKeys implements Serializable {
    private static final long serialVersionUID = 2695960851560264847L;

    private List<String> keys = new ArrayList<>(4);

    private String key1;
    private String key2;
    private String key3;

    private String monitorType;

    public MonitorKeys() {

    }

    public MonitorKeys(List<String> keys) {
        this.keys.addAll(keys);
        if (keys != null && keys.size() == 4) {
            this.monitorType = keys.get(1);
        }
    }

    public MonitorKeys(String monitorType, String key1, String key2, String key3) {
        this.keys.add(monitorType);
        this.keys.add(key1);
        this.keys.add(key2);
        this.keys.add(key3);

        this.monitorType = monitorType;
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;

    }

    @Override
    public boolean equals(Object comparedKeys) {
        if (this == comparedKeys) {
            return true;
        }
        if (!(comparedKeys instanceof MonitorKeys)) {
            return false;
        }

        if (!(this.monitorType == null ? ((MonitorKeys)comparedKeys).monitorType == null : this.monitorType.equals(
                ((MonitorKeys)comparedKeys).monitorType))) { return false; }
        if (!(this.key1 == null ? ((MonitorKeys)comparedKeys).key1 == null : this.key1.equals(
                ((MonitorKeys)comparedKeys).key1))) { return false; }
        if (!(this.key2 == null ? ((MonitorKeys)comparedKeys).key2 == null : this.key2.equals(
                ((MonitorKeys)comparedKeys).key2))) { return false; }
        if (!(this.key3 == null ? ((MonitorKeys)comparedKeys).key3 == null : this.key3.equals(
                ((MonitorKeys)comparedKeys).key3))) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    public String getString(String splitter) {
        StringBuilder sb = new StringBuilder();
        boolean isNotFirst = false;
        int i = 0;
        for (String key : keys) {

            if (++i == 1 && key == null && monitorType == null) {
                continue;
            } else if (key == null) {
                key = "";
            }

            if (isNotFirst) {
                sb.append(splitter);
            } else {
                isNotFirst = true;
            }

            sb.append(key);
        }

        return sb.toString();
    }
}


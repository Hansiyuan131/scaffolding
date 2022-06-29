package com.yuanstack.sca.service.system.assembly.monitor;

/**
 * @description: 监控
 * @author: hansiyuan
 * @date: 2022/6/29 11:06 AM
 */
public class KvMonitor {
    public static MonitorRT rt(String k1) {
        return rt(k1, "", "");
    }

    public static MonitorRT rt(String k1, String[] k2, String k3) {
        return new MonitorRT(k1, k2, k3);
    }

    public static MonitorRT rt(String k1, String k2, String k3) {
        return new MonitorRT(k1, k2, k3);
    }

    public static MonitorQps qps(String k1) {
        return qps(k1, "", "");
    }

    public static MonitorQps qps(String k1, String k2, String k3) {
        return new MonitorQps(k1, k2, k3);
    }

    public static MonitorQps qps(String k1, String[] k2, String k3) {
        return new MonitorQps(k1, k2, k3);
    }

    public static void main(String[] args) throws InterruptedException {
        KvMonitor.rt("测试").record(System.currentTimeMillis());

        Thread.sleep(60000);
    }
}

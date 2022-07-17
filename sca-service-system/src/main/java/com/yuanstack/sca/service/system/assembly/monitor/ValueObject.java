package com.yuanstack.sca.service.system.assembly.monitor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description: 值对象
 * @author: hansiyuan
 * @date: 2022/6/29 11:09 AM
 */
public class ValueObject implements Serializable {

    private static final long serialVersionUID = 9212489446827758222L;

    public static final int NUM_VALUES = 2;

    private final AtomicReference<long[]> values = new AtomicReference<long[]>();

    public ValueObject() {
        long[] init = new long[NUM_VALUES];
        this.values.set(init);
    }

    public ValueObject(long value1, long value2) {
        this();
        addCount(value1, value2);
    }

    public void addCount(long value1, long value2) {
        long[] current;
        long[] update = new long[NUM_VALUES];
        do {
            current = values.get();
            update[0] = current[0] + value1;
            update[1] = current[1] + value2;
        } while (!values.compareAndSet(current, update));

    }

    /**
     * Should only be used by log writer to deduct written counts. This method does not affect stat rules.
     */
    void deductCount(long value1, long value2) {
        long[] current;
        long[] update = new long[NUM_VALUES];
        do {
            current = values.get();
            update[0] = current[0] - value1;
            update[1] = current[1] - value2;
        } while (!values.compareAndSet(current, update));
    }

    public long getValue1() {
        return values.get()[0];
    }

    public long getValue2() {
        return values.get()[1];
    }

    public long[] getValues() {
        return values.get();
    }
}


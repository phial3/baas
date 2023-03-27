package org.phial.baas.service.service;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class DelayedItem<T> implements Delayed {

    private T data;
    private Long evictTime;

    /**
     *
     * @param data
     * @param timeout
     */
    public DelayedItem(T data, long timeout) {
        this.data = data;
        this.evictTime = System.currentTimeMillis() + timeout;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long remain = unit.convert(evictTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return remain;
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    /**
     * 获取 data
     *
     * @return data
     */
    public T getData() {
        return data;
    }

    /**
     * 设置 data
     *
     * @param data data 值
     */
    public void setData(T data) {
        this.data = data;
    }
}

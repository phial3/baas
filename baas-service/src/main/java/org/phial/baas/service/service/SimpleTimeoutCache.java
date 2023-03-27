package org.phial.baas.service.service;

import org.mayanjun.mybatisx.api.entity.Entity;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * 一个简单的本地超时缓存
 * @since 2021/3/3
 * @author mayanjun
 */
public class SimpleTimeoutCache implements ApplicationRunner, DisposableBean {

    private Map<String, Item<? extends Entity>> cache = new ConcurrentHashMap<>();
    private DelayQueue queue = new DelayQueue();

    private volatile boolean running = false;

    private static String key(Class<? extends Entity> cls, Serializable id) {
        return cls.getName() + ":" + id;
    }

    private void handleItem() {
        try {
            Item item = (Item) queue.take();
            if (item != null) {
                System.out.println(new Date() + ":: " + item.key);
                cache.remove(item.key);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (running) return;

        running = true;
        Thread thread = new Thread(() -> {
            while (running) {
                handleItem();
            }
        }, "SimpleTimeoutCacheWorker");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        running = false;
    }

    private static class Item<T extends Entity> extends DelayedItem<T> {

        private String key;

        public Item(T data, long timeout) {
            super(data, timeout);
            this.key = key(data.getClass(), data.getId());
        }

        /**
         * 获取 key
         *
         * @return key
         */
        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item<?> item = (Item<?>) o;
            return key.equals(item.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    /**
     *
     * @param data
     * @param timeout 毫秒
     * @return
     */
    public boolean set(Entity data, long timeout) {
        if (data != null && timeout > 0) {
            Item item = new Item(data, timeout);
            cache.put(item.key, item);
            return queue.offer(item);
        }
        return false;
    }

    public <T extends Entity> T get(Class<T> cls, long id) {
        String key = key(cls, id);
        Item item = cache.get(key);
        if (item != null) {
            return (T) item.getData();
        }
        return null;
    }

    public <T extends Entity> T remove(Class<T> cls, long id) {
        String key = key(cls, id);
        Item item = cache.remove(key);
        if (item != null) {
            queue.remove(item);
            return (T) item.getData();
        }
        return null;
    }
}

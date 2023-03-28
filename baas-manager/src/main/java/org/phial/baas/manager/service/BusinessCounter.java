package org.phial.baas.manager.service;

import com.esotericsoftware.minlog.Log;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.manager.config.cache.CacheKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务计数器
 * 用来对各种业务数据进行技术统计，以便对程序进行调优
 * @since 2021/3/26
 * @author mayanjun
 */
@Component
public class BusinessCounter {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private BasicDAO dao;

    public enum Key {
        /**
         *
         */
        RFID_SCAN,
        ACTIVE_RFID_TEARDOWN,
        ACTIVE_RFID_LOW_BATTERY,
        BOX_RFID,
        ACTIVE_RFID_HEARTBEAT_TIMEOUT,
    }

    public void increment(Key key) {
        incrementAndGet(key);
    }

    public long incrementAndGet(Key key) {
        try {
            return cacheClient.increment(CacheKey.COUNTER, key.name());
        } catch (Exception e) {
            Log.error("Increment count error", e);
        }
        return -1;
    }

    public long get(Key key) {
        try {
            return cacheClient.getAtomicLong(CacheKey.COUNTER, key.name());
        } catch (Exception e) {
            Log.error("Increment count error", e);
        }
        return 0;
    }

    public Map<Key, Long> getAll() {
        try {
            Map<Key, Long> map = new HashMap<>();
            for (Key k : Key.values()) {
                map.put(k, get(k));
            }
            return map;
        } catch (Exception e) {
            Log.error("Get all count error", e);
        }

        return null;
    }

    public void clearAll() {
        try {
            for (Key k : Key.values()) {
                cacheClient.delete(CacheKey.COUNTER, k.name());
            }
        } catch (Exception e) {
            Log.error("CLear count error", e);
        }
    }

}
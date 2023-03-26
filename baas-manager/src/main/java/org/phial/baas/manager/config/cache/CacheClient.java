package org.phial.baas.manager.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.locks.Lock;

public interface CacheClient extends DisposableBean {

    Number getNumber(CacheKey cacheKey, String key);

    void setNumber(CacheKey cacheKey, String key, Number value);

    void setNumber(CacheKey cacheKey, String key, Number value, int timeout);

    String get(CacheKey cacheKey, String key);

    String getAndDelete(CacheKey cacheKey, String key);

    void set(CacheKey cacheKey, String key, String value, int timeout);

    boolean trySet(CacheKey cacheKey, String key, String value, int timeout);

    void set(CacheKey cacheKey, String key, String value);

    boolean delete(CacheKey cacheKey, String key);

    void set(CacheKey cacheKey, String key, Object entity);

    void set(CacheKey cacheKey, String key, Object entity, int timeout);

    <T> T get(CacheKey cacheKey, String key, TypeReference<T> reference);

    <T> T getAndDelete(CacheKey cacheKey, String key, TypeReference<T> reference);

    void putString(CacheKey cacheKey, String mapKey, String key, String o);

    void put(CacheKey cacheKey, String mapKey, String key, Object o);

    <T> T getFromMap(CacheKey cacheKey, String mapKey, String key, Class<T> cls);

    String getStringFromMap(CacheKey cacheKey, String mapKey, String key);

    void removeFromMap(CacheKey cacheKey, String mapKey, String key);

    <T> Map<String, T> map(CacheKey cacheKey, String mapKey, Class<T> cls);

    void clearMap(CacheKey cacheKey, String mapKey);

    Lock getLock(CacheKey cacheKey, String lockKey);

    boolean exists(CacheKey cacheKey, String key);

    long increment(CacheKey cacheKey, String key);

    long getAtomicLong(CacheKey cacheKey, String key);
}

package org.phial.baas.manager.config.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 缓存客户端
 * @since 2020/12/25
 * @author mayanjun
 */
public class RedisCacheClient implements CacheClient {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheClient.class);

    private ObjectMapper mapper;

    private Config config;
    private RedissonClient client;

    private RedisCacheClient() {
    }

    public RedisCacheClient(String host, int port) {
        this.mapper = new ObjectMapper();
        this.config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port);
        config.setCodec(new StringCodec());
        this.client = Redisson.create(config);
        LOG.info("Redis connected: host={}, port={}", host, port);
    }

    private RedissonClient client() {
        return client;
    }

    @Override
    public Number getNumber(CacheKey cacheKey, String key) {
        return (Number) client().getBucket(cacheKey.key(key)).get();
    }

    @Override
    public long getAtomicLong(CacheKey cacheKey, String key) {
        return client().getAtomicLong(cacheKey.key(key)).get();
    }

    @Override
    public void setNumber(CacheKey cacheKey, String key, Number value) {
        client().getBucket(cacheKey.key(key)).set(value);
    }

    @Override
    public void setNumber(CacheKey cacheKey, String key, Number value, int timeout) {
        client().getBucket(cacheKey.key(key)).set(value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public String get(CacheKey cacheKey, String key) {
        return (String) client().getBucket(cacheKey.key(key)).get();
    }

    @Override
    public String getAndDelete(CacheKey cacheKey, String key) {
        return (String) client().getBucket(cacheKey.key(key)).getAndDelete();
    }

    @Override
    public void set(CacheKey cacheKey, String key, String value, int timeout) {
        client().getBucket(cacheKey.key(key)).set(value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public boolean trySet(CacheKey cacheKey, String key, String value, int timeout) {
        return client().getBucket(cacheKey.key(key)).trySet(value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void set(CacheKey cacheKey, String key, String value) {
        client().getBucket(cacheKey.key(key)).set(value);
    }

    @Override
    public boolean delete(CacheKey cacheKey, String key) {
        return client().getBucket(cacheKey.key(key)).delete();
    }

    @Override
    public void set(CacheKey cacheKey, String key, Object entity) {
        set(cacheKey, key, entity, -1);
    }

    @Override
    public void set(CacheKey cacheKey, String key, Object entity, int timeout) {
        String value = null;
        if ((value = se(entity)) != null) {
            if (timeout <= 0) {
                set(cacheKey, key, value);
            } else {
                set(cacheKey, key, value, timeout);
            }
        }
    }

    @Override
    public <T> T get(CacheKey cacheKey, String key, TypeReference<T> reference) {
        String value = get(cacheKey, key);
        return de(value, reference);
    }

    @Override
    public <T> T getAndDelete(CacheKey cacheKey, String key, TypeReference<T> reference) {
        String value = getAndDelete(cacheKey, key);
        return de(value, reference);
    }

    private <T> T de(String json, TypeReference<T> reference) {

        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return mapper.readValue(json, reference);
        } catch (Exception e) {
            LOG.error("Get cache error, json=" + json, e);
        }

        return null;
    }

    private <T> T de(String json, Class<T> cls) {

        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return mapper.readValue(json, cls);
        } catch (JsonProcessingException e) {
            LOG.error("Get cache error, json=" + json, e);
        }

        return null;
    }

    private String se(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            LOG.error("Write json error", e);
        }
        return null;
    }

    @Override
    public void putString(CacheKey cacheKey, String mapKey, String key, String o) {
        client().getMap(cacheKey.key(mapKey)).put(key, o);
    }

    @Override
    public void put(CacheKey cacheKey, String mapKey, String key, Object o) {
        String value = null;
        if ((value = se(o)) != null) {
            client().getMap(cacheKey.key(mapKey)).put(key, value);
        }
    }

    @Override
    public <T> T getFromMap(CacheKey cacheKey, String mapKey, String key, Class<T> cls) {
        String json = (String) client().getMap(cacheKey.key(mapKey)).get(key);
        return de(json, cls);
    }

    @Override
    public String getStringFromMap(CacheKey cacheKey, String mapKey, String key) {
        return  (String) client().getMap(cacheKey.key(mapKey)).get(key);
    }

    @Override
    public void removeFromMap(CacheKey cacheKey, String mapKey, String key) {
        client().getMap(cacheKey.key(mapKey)).remove(key);
    }

    @Override
    public <T> Map<String, T> map(CacheKey cacheKey, String mapKey, Class<T> cls) {
        Set<Map.Entry<Object, Object>> set = client().getMap(cacheKey.key(mapKey)).entrySet();
        Map<String, T> map = new HashedMap();

        if (set != null) {
            set.stream().forEach(e -> map.put(
                    (String) e.getKey(),
                    de((String) e.getValue(), cls)
            ));
        }
        return map;
    }

    @Override
    public void clearMap(CacheKey cacheKey, String mapKey) {
        client.getMap(cacheKey.key(mapKey)).clear();
    }

    @Override
    public Lock getLock(CacheKey cacheKey, String lockKey) {
        return client().getLock(cacheKey.key(lockKey));
    }

    @Override
    public boolean exists(CacheKey cacheKey, String key) {
        return client.getBucket(cacheKey.key(key)).isExists();
    }

    @Override
    public long increment(CacheKey cacheKey, String key) {
        return client().getAtomicLong(cacheKey.key(key)).incrementAndGet();
    }

    @Override
    public void destroy() throws Exception {
        client().shutdown();
        LOG.info("!!!!!!!!!!!!!!!! RedisClient destroyed !!!!!!!!!!!!!!!!");
    }
}

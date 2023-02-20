package org.phial.baas.manager.config.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisClient {

    @Resource
    private RedissonClient redissonClient;

    private final ConcurrentHashMap<String, Long> topicListenerMap = new ConcurrentHashMap<>();

    public RedisClient(RedissonClient client) {
        this.redissonClient = client;
    }

    public boolean exists(String key) {
        return this.redissonClient.getBucket(key).isExists();
    }

    public boolean setNX(String key, String value, long keepTime, TimeUnit timeUnit) {
        RBucket<Object> bucket = this.redissonClient.getBucket(key);
        return bucket.trySet(value, keepTime, timeUnit);
    }


    public boolean setNX(String key, String value) {
        RBucket<Object> bucket = this.redissonClient.getBucket(key);
        return bucket.trySet(value);
    }


    public void setData(String key, String value, long keepTime, TimeUnit timeUnit) {
        RBucket<String> bucket = this.redissonClient.getBucket(key);
        bucket.set(value, keepTime, timeUnit);
    }

    // keepTime, timeUnit
    public void expireData(String key, long keepTime, TimeUnit timeUnit) {
        RBucket<String> bucket = this.redissonClient.getBucket(key);
        bucket.expire(keepTime, timeUnit);
    }

    public boolean delete(String key) {
        RBucket<String> bucket = this.redissonClient.getBucket(key);
        return bucket.delete();
    }

    public String getData(String key) {
        RBucket<String> bucket = this.redissonClient.getBucket(key);
        return bucket.get();
    }

    public long getLong(String key) {
        RAtomicLong atomicLong = this.redissonClient.getAtomicLong(key);
        return atomicLong.get();
    }

    public long incrBy(String key, Number value) {
        RAtomicLong atomicLong = this.redissonClient.getAtomicLong(key);
        return atomicLong.addAndGet(value.longValue());
    }

    public long incrByExpire(String key, Number value, long time, TimeUnit timeUnit) {
        RAtomicLong atomicLong = this.redissonClient.getAtomicLong(key);
        long newValue = atomicLong.addAndGet(value.longValue());
        atomicLong.expire(time, timeUnit);
        return newValue;
    }

    public void deleteLong(String key) {
        RAtomicLong atomicLong = this.redissonClient.getAtomicLong(key);
        atomicLong.delete();
    }

    public void setLong(String key, Number value) {
        RAtomicLong atomicLong = this.redissonClient.getAtomicLong(key);
        atomicLong.set(value.longValue());
    }

    public void publish(String topic, RedisEvent value) {
        RTopic rTopic = this.redissonClient.getTopic(topic);
        long pubId = rTopic.publish(JSON.toJSONString(value));
        //topicListenerMap.put(topic, pubId);
    }

    public void listen(String topic, RedisMessageListener listener) {
        if (!topicListenerMap.containsKey(topic)) {
            log.warn("redis client have no listen topic:{}", topic);
            return;
        }
        RTopic rTopic = this.redissonClient.getTopic(topic);
        int listenerId = rTopic.addListener(String.class, listener);
        topicListenerMap.put(topic, (long) listenerId);
    }

    public void pushFixedScoreSortedSet(String key, String value, int length) {
        RScoredSortedSet<Object> scoredSortedSet = this.redissonClient.getScoredSortedSet(key);
        scoredSortedSet.add(System.currentTimeMillis(), value);
        scoredSortedSet.removeRangeByRank(0, -1 * length - 1);
    }

    public List<Object> readAllScoreSortedSet(String key) {
        RScoredSortedSet<Object> scoredSortedSet = this.redissonClient.getScoredSortedSet(key);
        return new LinkedList<>(scoredSortedSet.readAll());
    }
}

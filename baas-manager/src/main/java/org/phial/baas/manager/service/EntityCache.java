package org.phial.baas.manager.service;

import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.api.entity.Entity;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 提供实体缓存服务
 * @since 2020/11/24
 * @author mayanjun
 */
@Component
public class EntityCache {

    private static final Logger LOG = LoggerFactory.getLogger(EntityCache.class);

    private Map<Class, Map<Long, Entity>> cache = new ConcurrentHashMap<>();

    private BasicDAO service;

    private AtomicLong requestCount = new AtomicLong(1);
    private AtomicLong failCount = new AtomicLong(1);

    public EntityCache(BasicDAO service) {
        this.service = service;
    }

    protected <T extends Entity>  T newInstance(Class<T> cls , Long id) {
        try {
            T bean = cls.getConstructor(Long.class).newInstance(id);
            return bean;
        } catch (Exception e) {
            LOG.error("Can not create instance: " + cls, e);
            throw new ServiceException(e.getMessage());
        }
    }

    public <T extends Entity> T get(Class<T> cls, Long id) {
        requestCount.incrementAndGet();
        Map<Long, Entity> map = cache.computeIfAbsent(cls, key -> new ConcurrentHashMap<>());
        T entity = (T) map.computeIfAbsent(id, i ->  {
            failCount.incrementAndGet();
            return service.getInclude(newInstance(cls, id));
        });
        return entity;
    }

    public void evict(Entity entity) {
        if (entity != null) {
            Map<Long, Entity> map = cache.get(entity.getClass());
            if (map != null) {
                map.remove(entity.getId());
                LOG.info("----> Evict cache: id={}, type={},", entity.getId(), entity.getClass());
            }
        }
    }

    public float hitRate() {
        return 1 - (failCount.get() / requestCount.get());
    }
}

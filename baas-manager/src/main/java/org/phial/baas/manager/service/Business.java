package org.phial.baas.manager.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.api.entity.Entity;
import org.mayanjun.mybatisx.api.entity.LongEntity;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.phial.baas.service.domain.entity.NamedEntity;
import org.phial.baas.service.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Function;

public abstract class Business<T extends Entity> {

    private static final Logger LOG = LoggerFactory.getLogger(Business.class);

    @Autowired
    protected BasicDAO service;

    @Autowired
    protected EntityCache cache;

    protected TransactionTemplate transaction() {
        return service.databaseRouter().getDatabaseSession().transaction();
    }

    public BasicDAO dao() {
        return service;
    }

    /**
     *
     * @param entity
     * @param update
     */
    protected void named(T entity, boolean update) {
        if (entity instanceof NamedEntity) {
            String name = ((NamedEntity) entity).getName();
            if (StringUtils.isNotBlank(name)) {
                String pinyin = Strings.pinyin(name);
                ((NamedEntity) entity).setPinyin(pinyin);
            }

            Extras extras = getExtras(entity, update);
            if (extras != null) {
                ((NamedEntity) entity).setExtras(extras.toJSONString());
            }
        }
    }

    private <E extends LongEntity> Long entityId(E e) {
        if (e != null) {
            return e.getId();
        }
        return null;
    }

    /**
     * 设置额外信息
     *
     * @param entity
     * @param update
     */
    protected Extras getExtras(T entity, boolean update) {
        return null;
    }

    public <E extends LongEntity> Long[] ids(List<E> entities, Function<E, Long> function) {
        if (CollectionUtils.isNotEmpty(entities)) {
            Long[] ids = new Long[entities.size()];
            if (function == null) {
                function = this::entityId;
            }
            for (int i = 0; i < ids.length; i++) {
                ids[i] = function.apply(entities.get(i));
            }
            return ids;
        }
        return null;
    }

    public <T extends LongEntity> T idInstance(Class<T> cls, Long id) {
        if (id == null) throw new ServiceException("ID不能为空");
        T instance = null;
        Exception exception = null;
        try {
            Constructor<T> c = cls.getConstructor(Long.class);
            if (!c.isAccessible()) {
                c.setAccessible(true);
            }
            instance = c.newInstance(id);
        } catch (NoSuchMethodException e) {
            try {
                Constructor<T> c = cls.getConstructor();
                if (!c.isAccessible()) {
                    c.setAccessible(true);
                }
                instance = c.newInstance();
                instance.setId(id);
            } catch (Exception e2) {
                exception = e2;
            }
        } catch (Exception e) {
            exception = e;
        }

        if (instance == null) {
            LOG.error("Can't create Entity instance: class={}, id={}, exception={}", cls, id, exception.getMessage());
            throw new ServiceException("无法创建实例");
        }

        return instance;
    }
}

package org.phial.baas.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.phial.baas.service.constant.CommonConstant;
import org.phial.baas.service.domain.entity.Entity;
import org.phial.baas.service.listener.system.EntityEventDispatcher;
import org.phial.baas.service.listener.system.EntityEventListener;
import org.phial.baas.service.util.ClassUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.Date;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
public abstract class BaseService<T extends Entity> implements EntityEventListener {

    protected abstract BaseMapper<T> mapper();

    private Class<T> beanType = null;

    @Resource
    private EntityEventDispatcher eventDispatcher;

    public int save(T bean) {
        validate(bean, false);
        int ret = doSave(bean);
        log.info("Save bean done:id={},class={}", bean.getId(), bean.getClass().getSimpleName());
        Assert.isTrue(ret > 0, "save bean error!");
        return ret;
    }

    public int update(T bean) {
        validate(bean, true);
        int ret = doUpdate(bean);
        log.info("Update bean done:ret={},id={},class={}", ret, bean.getId(), bean.getClass().getSimpleName());
        Assert.isTrue(ret > 0, "update bean error!");
        return ret;
    }

    public T get(Long id) {
        return doGet(id);
    }

    public int delete(Long id) {
        return doDelete(id);
    }

    protected Page<T> list(T bean, int pageNo, int pageSize, boolean isAsc) {
        Assert.isTrue(pageNo > 0, "pageNo must be greater than 0");
        Assert.isTrue(pageSize > 0, "pageSize must be greater than 0");
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>(bean);
        queryWrapper.orderBy(true, isAsc, (SFunction<T, Long>) Entity::getId);
        long total = mapper().selectCount(queryWrapper);
        //PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);
        return mapper().selectPage(Page.of(pageNo, pageSize, total), queryWrapper);
    }

    protected Page<T> listAll(T bean) {
        return list(bean, CommonConstant.DEFAULT_PAGE_NO, CommonConstant.DEFAULT_PAGE_SIZE, true);
    }

    @Override
    public boolean support(Entity event) {
        return getBeanType() == event.getClass();
    }

    /**
     * 执行实体检查操作
     *
     * @param entity
     */
    protected void validate(T entity, boolean update) {
        doCheck(entity, update);
        named(entity, update);
        setOperator(entity, update);
    }

    /**
     * 执行参数逻辑校验检查工作,针对不同的业务需求，校验逻辑自己实现
     *
     * @param entity
     */
    protected void doCheck(T entity, boolean update) {
    }

    /**
     * @param bean
     * @param update
     */
    protected void named(T bean, boolean update) {
        if (bean instanceof Entity) {
            // named operator
        }
    }

    /**
     * @param bean
     * @param update
     */
    protected void setOperator(T bean, boolean update) {
        if (bean instanceof Entity) {
            // set operator

            // update
            if (update) {
                bean.setUpdateTime(new Date());
            } else {
                bean.setCreateTime(new Date());
                bean.setUpdateTime(new Date());
            }
        }
    }

    /**
     * @param bean
     * @return
     */
    protected int doSave(T bean) {
        return mapper().insert(bean);
    }

    /**
     * @param bean
     * @return
     */
    protected int doUpdate(T bean) {
        return mapper().updateById(bean);
    }

    /**
     * @param id
     * @return
     */
    protected T doGet(Long id) {
        return mapper().selectById(id);
    }

    /**
     * @param id
     * @return
     */
    protected int doDelete(Long id) {
        return mapper().deleteById(id);
    }

    /**
     * 发布当前实体变更信息事件
     *
     * @param event
     * @return
     */
    protected BaseService<T> emitEvent(Entity event) {
        eventDispatcher.emitEvent(event);
        return this;
    }


    public <T extends Entity> T newInstance(Class<T> cls, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }

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
            log.error("Can't create Entity instance: class={}, id={}, exception={}", cls, id, exception.getMessage());
            throw new RuntimeException("Can not create instance: " + instance);
        }

        return instance;
    }

    protected T newInstance(Long id) {
        Class<T> t = getBeanType();
        try {
            T bean = t.getConstructor(Long.class).newInstance(id);
            return bean;
        } catch (Exception e) {
            log.error("Can not create instance: " + t, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    protected T newInstance() {
        Class<T> t = getBeanType();
        try {
            T bean = t.getConstructor().newInstance();
            return bean;
        } catch (Exception e) {
            log.error("Can not create instance: " + t, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取实际参数类型
     *
     * @return
     */
    protected Class<T> getBeanType() {
        if (this.beanType != null) return beanType;
        beanType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return beanType;
    }
}

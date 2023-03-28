package org.phial.baas.manager.service;

import org.mayanjun.mybatisx.api.entity.Entity;

/**
 * 实体CRUD事件
 * @since 2020-05-17
 * @author mayanjun
 */
public class EntityEvent {

    private Entity[] entities;

    private EventType type;

    private Class targetClazz;

    public EntityEvent(EventType type, Entity ... entities) {
        this.type = type;
        this.entities = entities;
        if (entities != null && entities.length > 0) {
            this.targetClazz = entities[0].getClass();
        }
    }

    public enum EventType {
        NEW,        // 新增一个实体时触发
        UPDATE,     // 更新一个实体时触发
        DELETE      // 删除一个实体时触发
    }

    public EventType type() {
        return type;
    }

    public Entity [] entities() {
        if (entities == null) return new Entity[0];
        return entities;
    }

    /**
     * 获取 entities
     *
     * @return entities
     */
    public Entity[] getEntities() {
        return entities;
    }

    /**
     * 设置 entities
     *
     * @param entities entities 值
     */
    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }

    /**
     * 获取 type
     *
     * @return type
     */
    public EventType getType() {
        return type;
    }

    /**
     * 设置 type
     *
     * @param type type 值
     */
    public void setType(EventType type) {
        this.type = type;
    }

    public Class getTargetClazz() {
        return targetClazz;
    }

    public void setTargetClazz(Class targetClazz) {
        this.targetClazz = targetClazz;
    }

    public boolean isNotEmpty() {
        return !(this.entities == null || this.getEntities().length == 0);
    }
}

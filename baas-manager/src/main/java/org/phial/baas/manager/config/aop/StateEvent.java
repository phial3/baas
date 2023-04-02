package org.phial.baas.manager.config.aop;

public class StateEvent {
    public static enum StatType {
        ALARM_UPDATE,
        DEVICE_UPDATE,
        TASK_UPDATE,
        STAT_RECORD_UPDATE,

        TASK_UPDATED,
        ALARM_UPDATED,
        STAT_RECORD_UPDATED,
    }

    private StatType type;

    private Object data;

    public StateEvent(StatType type) {
        this.type = type;
    }

    public StateEvent(StatType type, Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * 获取 type
     *
     * @return type
     */
    public StatType getType() {
        return type;
    }

    /**
     * 获取 data
     *
     * @return data
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置 data
     *
     * @param data data 值
     */
    public void setData(Object data) {
        this.data = data;
    }
}

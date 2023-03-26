package org.phial.baas.manager.config.cache;


/**
 * 为了各个业务中间不产生KEY冲突，这里必须使用一个 CacheKey 对象来区分业务
 *
 * @author mayanjun
 * @since 2020/12/25
 */
public enum CacheKey {

    /**
     * 后台用户会话
     */
    CONSOLE_USER,

    /**
     * 移动用户会会话
     */
    MOBILE_USER,

    /**
     * 设备心跳
     */
    HEARTBEAT,

    /**
     * 存储设备信息
     */
    DEVICE_MANAGER,

    /**
     * 设备存储逻辑
     */
    DEVICE_BUSINESS,

    /**
     * 报警压制
     */
    ALARM_SUPPRESS,

    /**
     * 属性管理
     */
    ATTRIBUTE,

    /**
     * 报警管理器业务
     */
    ALARM_MANAGER,

    /**
     * RFID 事件处理器业务逻辑
     */
    RFID_EVENT_HANDLER,

    /**
     * 统计相关业务逻辑
     */
    STAT_BUSINESS,


    UNSAFE_RFID_POS,

    /**
     * 3D统计相关业务逻辑
     */
    THREE_STAT_BUSINESS,

    /**
     * 业务计数器
     */
    COUNTER,

    /**
     * 报警人员设置
     */
    ALARM_SETTING;

    public String key(String key) {
        return "id:" + this.name() + ":" + key;
    }
}

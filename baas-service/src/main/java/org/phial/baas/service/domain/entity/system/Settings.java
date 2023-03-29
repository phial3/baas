package org.phial.baas.service.domain.entity.system;

/**
 * 系统设置
 * @since 2021/4/8
 * @author mayanjun
 */
public class Settings {

    public static final String ALARM_SUPPRESS_INTERVAL = "alarmSuppressInterval";
    public static final String ALARM_EVIDENCE_DURATION = "alarmEvidenceDuration";
    public static final String CAMERA_SHOT_INTERVAL = "cameraShotInterval";

    /**
     * 是都打印详细日志
     */
    private Boolean verboseLogEnabled = false;

    /**
     * 报警压制周期
     */
    private int alarmSuppressInterval = 1800;

    /**
     * 默认菜单是否展开
     */
    private Boolean defaultExpand = false;

    /**
     * 业务报警产生时，截取存证的前后时间长度,单位：秒
     */
    private int alarmEvidenceDuration = 30;

    /**
     * 摄像头拍照周期，单位：秒，默认 4 小时
     */
    private int cameraShotInterval = 3600 * 4;

    /**
     * 默认菜单展开是与其他展开的菜单是否互斥
     */
    private Boolean defaultExpandExclusively = true;

    @AttributeItem
    public Boolean getVerboseLogEnabled() {
        return verboseLogEnabled;
    }

    public void setVerboseLogEnabled(Boolean verboseLogEnabled) {
        this.verboseLogEnabled = verboseLogEnabled;
    }

    @AttributeItem(user = "*")
    public Boolean getDefaultExpand() {
        return defaultExpand;
    }

    public void setDefaultExpand(Boolean defaultExpand) {
        this.defaultExpand = defaultExpand;
    }

    /**
     * 获取 alarmSuppressInterval
     *
     * @return alarmSuppressInterval
     */
    @AttributeItem
    public int getAlarmSuppressInterval() {
        return alarmSuppressInterval;
    }

    /**
     * 设置 alarmSuppressInterval
     *
     * @param alarmSuppressInterval alarmSuppressInterval 值
     */
    public void setAlarmSuppressInterval(int alarmSuppressInterval) {
        this.alarmSuppressInterval = alarmSuppressInterval;
    }

    /**
     * 获取 alarmEvidenceDuration
     *
     * @return alarmEvidenceDuration
     */
    @AttributeItem
    public int getAlarmEvidenceDuration() {
        return alarmEvidenceDuration;
    }

    /**
     * 设置 alarmEvidenceDuration
     *
     * @param alarmEvidenceDuration alarmEvidenceDuration 值
     */
    public void setAlarmEvidenceDuration(int alarmEvidenceDuration) {
        this.alarmEvidenceDuration = alarmEvidenceDuration;
    }

    /**
     * 获取 cameraShotInterval
     *
     * @return cameraShotInterval
     */
    @AttributeItem
    public int getCameraShotInterval() {
        return cameraShotInterval;
    }

    /**
     * 设置 cameraShotInterval
     *
     * @param cameraShotInterval cameraShotInterval 值
     */
    public void setCameraShotInterval(int cameraShotInterval) {
        this.cameraShotInterval = cameraShotInterval;
    }

    /**
     * 获取 defaultExpandExclusively
     *
     * @return defaultExpandExclusively
     */
    @AttributeItem(user = "*")
    public Boolean getDefaultExpandExclusively() {
        return defaultExpandExclusively;
    }

    /**
     * 设置 defaultExpandExclusively
     *
     * @param defaultExpandExclusively defaultExpandExclusively 值
     */
    public void setDefaultExpandExclusively(Boolean defaultExpandExclusively) {
        this.defaultExpandExclusively = defaultExpandExclusively;
    }
}
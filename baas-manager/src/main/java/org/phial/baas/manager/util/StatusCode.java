package org.phial.baas.manager.util;

import org.mayanjun.core.Status;

/**
 * @author mayanjun
 * @vendor mayanjun.org
 */
public class StatusCode {

    public static final Status DAO_SAVE_FAIL = new Status(3001, "保存失败");
    public static final Status DAO_UPDATE_FAIL = new Status(3002, "更新失败");
    public static final Status API_NOT_SUPPORTED = new Status(3003, "不支持的API");
    public static final Status PERMISSION_DENIED = new Status(3004, "无权限");
    public static final Status OPEN_API_PERMISSION_DENIED = new Status(3500, "非法访问");


    public static final Status RFID_BUSY = new Status(4000, "RFID正在使用中，请稍后再试");
    public static final Status TASK_NOT_FOUND = new Status(4001, "作业ID错误或者任务不存在");
    public static final Status WORK_TASK_COUNT_LIMIT = new Status(4002, "当前任务入库数量超限");
    public static final Status TASK_STATUS_INCORRECT = new Status(4003, "作业状态错误");
    public static final Status TASK_STATUS_ALREADY_DONE = new Status(4004, "当前任务以完成，无法继续作业");
    public static final Status TASK_PLATE_INCORRECT = new Status(4005, "入库作业车牌号缺失");
    public static final Status TASK_ITEM_STATUS_INCORRECT = new Status(4006, "任务项状态错误");
    public static final Status ALARM_NOT_FOUND = new Status(4007, "报警信息不存在");

    public static final Status APP_NOT_FOUND = new Status(4008, "应用不存在");
    public static final Status APP_DISABLED = new Status(4009, "应用已被禁用");
    public static final Status TASK_ITEM_IN_PROGRESS = new Status(4010, "当前作业有尚未完成的货物");
    public static final Status FILE_NOT_FOUND = new Status(4011, "文件不存在");

    public static final Status GOODS_NOT_FOUND = new Status(4012, "货物不存在");
    public static final Status GOODS_CODE_CONFLICTING = new Status(4013, "钢卷号冲突");
    public static final Status TASK_TYPE_ERROR = new Status(4014, "任务类型不匹配");
    public static final Status RFID_EXISTS = new Status(4015, "RFID已经存在");
    public static final Status OUTBOUND_NO_POSITION_EVENT = new Status(4016, "系统未检测到出库落位事件，请手动确认出库");
}

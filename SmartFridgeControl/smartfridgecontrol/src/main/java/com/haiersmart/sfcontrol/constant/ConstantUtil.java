package com.haiersmart.sfcontrol.constant;

/**
 * 公共常量Util
 */
public class ConstantUtil {

    /**
     * sp 默认文件名
     */
    public static final String SFCONTROL = "nation_sp";
    /**
     * 常量
     */
    public static final boolean DEBUG = false;  //true debug版本 false 正式版本

    public static final int SERVICE_RESTART_TIME = 5 * 1000;                //Service 重启间隔时间
    public static final int LUNBO_TIME = 15 * 1000;                //Service 重启间隔时间

    public static final boolean MODBUS_SERVICE_STARTUP = true;                //Service 重启间隔时间

    public static final int CODE_STATUS_SIZE = 110;                //电控板返回状态码数组大小

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "controlboard.db";
    public static final String DB_TABLE_NAME_CONTROL = "controltable";
    /**
     * INTENT
     */
    public static final String INTENT_FROM = "i00";


    /**
     * Bundle
     */
    public static final String Bundle_FROM = "i00";

    /**
     * fridge full model
     */
    public static final String BCD325_MODEL= "BCD-325";
    public static final String BCD251_MODEL = "BCD-251WDICU1";
    public static final String BCD401_MODEL = "BCD-401WDIAU1";
    public static final String BCD256_MODEL = "BCD-256WDICU1";
    public static final String BCD658_MODEL = "BCD-658WDIBU1";
    public static final String BCD630_MODEL = "BCD-630";
    public static final String BCD476_MODEL = "BCD-476WDICU1";
    public static final String BCD480_MODEL = "BCD-480WLDCPU1";
    public static final String BCD475_MODEL = "BCD-475WDIDU1";

    /**
     * fridge typeid
     */
    public static final String BCD325_SN = "111c12002400081001030061800446410000000000";
    public static final String BCD251_SN = "111c12002400081001030061800118420000000000";
    public static final String BCD401_SN = "111c12002400081001010061800447000000000000";
    public static final String BCD256_SN = "111c12002400081001030061800458410000000000";
    public static final String BCD630_SN = "111c12002400081001010061800259430000000000";
    public static final String BCD658_SN = "111c12002400081001010061800259430000000000";
    public static final String BCD476_SN = "111c12002400081001010061800347460000000000";
    public static final String BCD480_SN = "111c12002400081001020061800502000000000000";

    /**
     * broadcast
     */
    //broadcast from mainboard to service, internal
    public static final String BROADCAST_ACTION_QUERY_BACK = "com.com.haiersmart.sfcontrol.queryback";//冰箱档设备查询码从电控板返回广播
    public static final String BROADCAST_ACTION_STATUS_BACK = "com.haiersmart.sfcontrol.statusback";//冰箱控制和温度等状态从电控板返回广播
    //broadcast to user , external
//    public static final String BROADCAST_ACTION_FRIDGE_INFO = "com.haiersmart.sfcontrol.info";//设备fridge info(id, type)发生变化广播
//    public static final String BROADCAST_ACTION_CONTROL = "com.haiersmart.sfcontrol.control";//通知模式和档位信息广播
//    public static final String BROADCAST_ACTION_TEMPER = "com.haiersmart.sfcontrol.temper";//通知温度广播
//    public static final String BROADCAST_ACTION_ERROR = "com.haiersmart.sfcontrol.error";//通知错误或故障信息广播
//    public static final String BROADCAST_ACTION_ALARM = "com.haiersmart.sfcontrol.alarm";//通知报警信息广播
    public static final String COMMAND_TO_SERVICE = "com.haiersmart.sfcontrol.command";//用户命令给service广播
    public static final String SERVICE_NOTICE = "com.haiersmart.sfcontrol.notice";//service通知状态
//    public static final String BROADCAST_ACTION_FRIDGE_RANGE = "com.haiersmart.sfcontrol.fridge.range";//通知冷藏档位大小范围
//    public static final String BROADCAST_ACTION_CHANGE_RANGE = "com.haiersmart.sfcontrol.change.range";//通知变温档位大小范围
//    public static final String BROADCAST_ACTION_FREEZE_RANGE = "com.haiersmart.sfcontrol.freeze.range";//通知冷冻档位大小范围
//    public static final String BROADCAST_ACTION_READY = "com.haiersmart.sfcontrol.ready";//通知service是否能正常获取主控板信息

    /**
     * user command request actions
     */
    public static final String MODE_SMART_ON = "openSmartModel";//智能模式
    public static final String MODE_SMART_OFF = "closeSmartModel";//

    public static final String MODE_HOLIDAY_ON = "openHolidayModel";//假日模式
    public static final String MODE_HOLIDAY_OFF = "closeHolidayModel";//

    public static final String MODE_FREEZE_ON = "openFreezeModel";//速冻模式
    public static final String MODE_FREEZE_OFF = "closeFreezeModel";//

    public static final String MODE_COLD_ON = "openColdModel";//速冷模式
    public static final String MODE_COLD_OFF = "closeColdModel";//

    public static final String REFRIGERATOR_OPEN = "refrigerator_open";//冷藏开
    public static final String REFRIGERATOR_CLOSE = "refrigerator_close";//冷藏关

    public static final String MODE_ZHENPIN_ON = "openZhenPinModel";//珍品模式
    public static final String MODE_ZHENPIN_OFF = "closeZhenPinModel";//

    public static final String MODE_CLEAN_ON = "openCleanModel";//净化模式
    public static final String MODE_CLEAN_OFF = "closeCleanModel";//

    public static final String TEMPER_SETCOLD = "setCold";//冷藏区温控
    public static final String TEMPER_SETFREEZE = "setFreeze";//冷冻区温控
    public static final String TEMPER_SETCUSTOMAREA = "setCustomArea";//变温区温控

    public static final String BOOT_COMPLETED = "bootCompleted";//系统重启

    public static final String MODE_UV = "setUVModel";//设置杀菌模式
    public static final String KEY_QUERY = "query";//远程模块主动查询冰箱状态

    public static final String MODE_TIDBIT_ON = "openTidbitModel";//珍品开
    public static final String MODE_TIDBIT_OFF = "closeTidbitModel";//珍品关
    public static final String MODE_PURIFY_ON = "openPurifyModel";//净化开
    public static final String MODE_PURIFY_OFF = "closePurifyModel";//净化关
    public static final String MODE_STERILIZE_ON = "openSterilizeModel";//杀菌模式

    public static final String FRIDGE_LIGHT_ON = "openFridgeLight";//冷藏灯开
    public static final String FRIDGE_LIGHT_OFF = "closeFridgeLight";//冷藏灯关

    public static final String HANDLE_LIGHT_ON = "openHandleLight";//把手灯开
    public static final String HANDLE_LIGHT_OFF = "closeHandleLight";//把手灯关

    public static final String PIR_ON = "openPir";//人感开
    public static final String PIR_OFF = "closePir";//人感关
    public static final String FRIDGE_TOP_LIGHT_ON = "openFridgeTopLight";//冷藏顶灯开
    public static final String FRIDGE_TOP_LIGHT_OFF = "closeFridgeTopLight";//冷藏顶灯关
    public static final String FRIDGE_BACK_LIGHT_ON = "openFridgeBackLight";//冷藏背灯开
    public static final String FRIDGE_BACK_LIGHT_OFF = "closeFridgeBackLight";//冷藏背关
    public static final String INSIDE_DOOR_ON = "openInsideDoor";//门中门开
    public static final String INSIDE_DOOR_OFF = "closeInsideDoor";//门中门关



    //返回的key
    public static final String KEY_MODE= "commandMode";
    public static final String KEY_READY= "serviceReady";
    public static final String KEY_FRIDGE_ID = "fridgeId";//冰箱系统型号
    public static final String KEY_TYPE_ID = "typeId";//冰箱互联型号
    public static final String KEY_FRIDGE_TYPE = "fridgeType";//冰箱硬件型号 海尔注册的冰箱型号
    public static final String KEY_CONTROL_INFO = "ControlInfo";//识别key为“ControlInfo” 发送的数据为Serializable
    public static final String KEY_TEMPER = "temper";
    public static final String KEY_ERROR = "error";//错误命令key
    public static final String KEY_ALARM = "alarm";//报警key
    public static final String KEY_RANGE = "range";//温度控制范围
    public static final String KEY_INFO = "info";//温度控制范围
    public static final String KEY_STERILIZE_STATUS = "sterilizeStatus";//杀菌剩余时间
    public static final String KEY_STATUS_CODE = "statusCode";//状态码



    public static final String QUERY_CONTROL_READY= "queryControlReady";//
    public static final String QUERY_FRIDGE_INFO= "queryFridgeInfo";//
    public static final String QUERY_CONTROL_INFO = "queryControlInfo";//
    public static final String QUERY_TEMPER_INFO = "queryTemperInfo";//
    public static final String QUERY_ERROR_INFO = "queryErrorInfo";//
    public static final String QUERY_TEMP_RANGE = "queryTempRange";//查询温度范围
    public static final String QUERY_FRIDGE_TEMP_RANGE = "queryFridgeTempRange";//
    public static final String QUERY_CHANGE_TEMP_RANGE = "queryChangeTempRange";//
    public static final String QUERY_FREEZE_TEMP_RANGE = "queryFreezeTempRange";//查询冷冻温度范围
    public static final String QUERY_STERILIZE_STATUS = "querySterilizeStatus";//杀菌剩余时间
    public static final String QUERY_STATUS_CODE = "queryStatusCode";//查询状态码


    public static final String KEY_SET_FRIDGE_LEVEL= "fridgelevel";
    public static final String KEY_SET_COLD_LEVEL= "coldlevel";
    public static final String KEY_SET_FREEZE_LEVEL= "freezelevel";
    public static final String FRIDGE_TEMP_MAX= "fridgeMaxValue";
    public static final String FRIDGE_TEMP_MIN= "fridgeMinValue";
    public static final String CHANGE_TEMP_MAX= "changeMaxValue";
    public static final String CHANGE_TEMP_MIN= "changeMinValue";
    public static final String FREEZE_TEMP_MAX= "freezeMaxValue";
    public static final String FREEZE_TEMP_MIN= "freezeMinValue";


    public static final String QUERY_MODE = "model";
    public static final String QUERY_COLD_TEMPER_SET = "getSetCold";
    public static final String QUERY_COLD_TEMPER_TURE = "getCold";
    public static final String QUERY_FREEZE_TEMPER_SET = "getSetFreeze";
    public static final String QUERY_FREEZE_TEMPER_TURE = "getFreeze";
    public static final String QUERY_MID_TEMPER_SET = "getsetCustomArea";
    public static final String QUERY_MID_TEMPER_TURE = "getCustomArea";

    //门状态
    public static final String DOOR_STATUS= "doorStatus";//门状态
    public static final String DOOR_FRIDGE_OPEN = "fridgeDoorOpen";//冷藏门开门
    public static final String DOOR_FRIDGE_CLOSE= "fridgeDoorClose";//冷藏门关门
    public static final String DOOR_FREEZE_OPEN = "freezeDoorOpen";//冷冻门开门
    public static final String DOOR_FREEZE_CLOSE= "freezeDoorClose";//冷冻门关门
    public static final String DOOR_CHANGE_OPEN = "changeDoorOpen";//变温门开门
    public static final String DOOR_CHANGE_CLOSE= "changeDoorClose";//变温门关门
    //门报警
    public static final String DOOR_ALARM_STATUS = "doorAlarmStatus";//门常开报警状态
    public static final String DOOR_FRIDGE_ALARM_TURE = "fridgeDoorAlarmTrue";//冷藏门常开报警开始
    public static final String DOOR_FRIDGE_ALARM_FALSE= "fridgeDoorAlarmFalse";//冷藏门常开报警停止
    public static final String DOOR_FREEZE_ALARM_TURE= "freezeDoorAlarmTrue";//冷冻门常开报警开始
    public static final String DOOR_FREEZE_ALARM_FALSE= "freezeDoorAlarmFalse";//冷冻门常开报警停止
    public static final String DOOR_CHANGE_ALARM_TURE= "changeDoorAlarmTrue";//变温门常开报警开始
    public static final String DOOR_CHANGE_ALARM_FALSE= "changeDoorAlarmFalse";//变温门常开报警停止



    //toast info
//    public static final String NO_WARNING = "none";
//    public static final String HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING = "假日模式已开启，如要关闭冷藏室请先退出假日模式";
//    public static final String SMART_ON_REFRIGERATOR_CLOSE_WARNING = "智能模式已开启，如要关闭冷藏室请先退出智能模式";
//    public static final String SMART_ON_SET_TEMPER_WARNING = "智能模式已开启，如要调节温度请先退出智能模式";
//    public static final String HOLIDAY_ON_SET_TEMPER_WARNING = "假日模式已开启，如要调节温度请先退出假日模式";
//    public static final String FREEZE_ON_SET_TEMPER_WARNING = "速冻模式已开启，如要调节温度请先退出速冻模式";
//    public static final String CLOD_ON_SET_TEMPER_WARNING = "速冷模式已开启，如要调节温度请先退出速冷模式";
//    public static final String REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING = "冷藏室已关闭，如要调节温度请先开启冷藏室";
    public static final String NO_WARNING = "none";
    public static final String HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING = "假日";
    public static final String SMART_ON_REFRIGERATOR_CLOSE_WARNING = "智能";
    public static final String SMART_ON_SET_TEMPER_WARNING = "智能";
    public static final String HOLIDAY_ON_SET_TEMPER_WARNING = "假日";
    public static final String FREEZE_ON_SET_TEMPER_WARNING = "速冻";
    public static final String CLOD_ON_SET_TEMPER_WARNING = "速冷";
    public static final String REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING = "关闭";
    public static final String TIDBIT_ON_SET_TEMPER_WARNING = "珍品";

    //年月日
    public static final String pop_year = "年";//y
    public static final String pop_month = "月";//m
    public static final String pop_day = "日";//d
    public static boolean isAlarm = true;

    //SP
    public static final String COLDTIME = "cold_time";//冷藏室时间
    public static final String COLDCOUNT = "cold_count";//冷藏室计数
    public static final String FREEZETIME = "freeze_time";//冷冻室时间
    public static final String FREEZECOUNT = "freeze_count";//冷冻室计数
    public static final String COLDEND = "cold_end";//冷藏室计时结束
    public static final String FREEZEEND= "freeze_end";//冷冻室计时结束
    public static final String STERILIZETIME= "sterilize_time";//杀菌开始时间
    public static final String STERILIZETIMECOUNT= "sterilize_count";//杀菌计时
    public static final String STERILIZETIMEINTERVALCOUNT= "sterilize_interval_count";//杀菌计时




}

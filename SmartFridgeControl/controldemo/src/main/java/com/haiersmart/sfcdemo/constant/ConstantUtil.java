package com.haiersmart.sfcdemo.constant;

/**
 * 公共常量Util
 */
public class ConstantUtil {


    /**
     * fridge type
     */
    public static final String BCD325_MODEL= "BCD-325";
    public static final String BCD251_MODEL = "BCD-251WDICU1";
    public static final String BCD401_MODEL = "BCD-401WDIAU1";
    public static final String BCD256_MODEL = "BCD-256WDICU1";
    public static final String BCD630_MODEL = "BCD-630";
    public static final String BCD476_MODEL = "BCD-476WDICU1";

    /**
     * broadcast
     */

    //broadcast to user , external
    public static final String COMMAND_TO_SERVICE = "com.haiersmart.sfcontrol.command";//用户命令给service广播
    public static final String SERVICE_NOTICE = "com.haiersmart.sfcontrol.notice";//service通知状态

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

    //返回的key
    public static final String KEY_CONTROL_INFO = "ControlInfo";//识别key为“ControlInfo” 发送的数据为Serializable
    public static final String KEY_FRIDGE_TYPE = "fridgeType";
    public static final String KEY_TYPE_ID = "typeId";//冰箱互联型号
    public static final String KEY_ERROR = "error";//错误命令key
    public static final String KEY_TEMPER = "temper";
    public static final String KEY_MODE= "commandMode";
    public static final String KEY_READY= "serviceReady";
    public static final String KEY_RANGE = "range";//温度控制范围
    public static final String KEY_INFO = "info";//温度控制范围

    public static final String KEY_SET_COLD_LEVEL= "coldlevel";
    public static final String KEY_SET_FRIDGE_LEVEL= "fridgelevel";
    public static final String KEY_SET_FREEZE_LEVEL= "freezelevel";

    public static final String QUERY_CONTROL_READY= "queryControlReady";
    public static final String QUERY_FRIDGE_INFO= "queryFridgeInfo";
    public static final String QUERY_CONTROL_INFO = "queryControlInfo";
    public static final String QUERY_TEMPER_INFO = "queryTemperInfo";
    public static final String QUERY_ERROR_INFO = "queryErrorInfo";
    public static final String QUERY_TEMP_RANGE = "queryTempRange";


    public static final String QUERY_MODE = "model";
    public static final String QUERY_COLD_TEMPER_SET = "getSetCold";
    public static final String QUERY_COLD_TEMPER_TURE = "getCold";
    public static final String QUERY_FREEZE_TEMPER_SET = "getSetFreeze";
    public static final String QUERY_FREEZE_TEMPER_TURE = "getFreeze";
    public static final String QUERY_MID_TEMPER_SET = "getsetCustomArea";
    public static final String QUERY_MID_TEMPER_TURE = "getCustomArea";

    //门状态
    public static final String DOOR_STATUS= "doorStatus";//门状态

    //门报警
    public static final String DOOR_ALARM_STATUS = "doorAlarmStatus";//门常开报警状态
    //杀菌
    public static final String QUERY_STERILIZE_STATUS = "querySterilizeStatus";//杀菌剩余时间
    public static final String KEY_STERILIZE_STATUS = "sterilizeStatus";//杀菌剩余时间

}

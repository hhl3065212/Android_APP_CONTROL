/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.constant;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/14
 * Author: Holy.Han
 * modification:
 */
public class ConstantWifiUtil {
    protected final String TAG = "ConstantWifiUtil";

    //    互联互通控制
    public static final String ACTION_MODECONTROL = "com.haier.tft.control.pcb";//模式开关action
    public static final String ACTION_CONTROL = "com.haier.tft.control.broadcast";//温度控制等action
    public static final String KEY_SENDCONTROL = "sendControl";//extra key
    public static final String KEY_MODE = "model";//

    public static final String MODE_FREEZE_ON = "openFreezeModel";//速冻模式
    public static final String MODE_FREEZE_OFF = "closeFreezeModel";//

    public static final String MODE_COLD_ON = "openColdModel";//速冷模式
    public static final String MODE_COLD_OFF = "closeColdModel";//

    public static final String MODE_SMART_ON = "openSmartModel";//智能模式
    public static final String MODE_SMART_OFF = "closeSmartModel";//

    public static final String MODE_HOLIDAY_ON = "openHolidayModel";//假日模式
    public static final String MODE_HOLIDAY_OFF = "closeHolidayModel";//

    public static final String MODE_ZHENPIN_ON = "openZhenPinModel";//珍品模式
    public static final String MODE_ZHENPIN_OFF = "closeZhenPinModel";//

    public static final String MODE_CLEAN_ON = "openCleanModel";//净化模式
    public static final String MODE_CLEAN_OFF = "closeCleanModel";//

    public static final String TEMPER_SETCOLD = "setCold";//冷藏区温控
    public static final String TEMPER_SETFREEZE = "setFreeze";//冷冻区温控
    public static final String TEMPER_SETCUSTOMAREA = "setCustomArea";//变温区温控
    public static final String MODE_UV = "setUVModel";//设置杀菌模式


    public static final String KEY_QUERY = "query";//远程模块主动查询冰箱状态

    //返回的key
    public static final String KEY_GETSTATE = "getState";//识别key为“getState” 发送的数据为hashmap
    public static final String KEY_GETBYTES = "getBytes";//识别key为“getState” 发送的数据为hashmap
    public static final String KEY_ERROR = "error";//错误命令key
    public static final String KEY_ALARM = "alarm";//报警key

    public static final String KEY_TEMPER = "temper";

    public static final String KEY_TYPE_ID = "typeid";

    public static final String COLD_OPEN = "cold_open";


    public static final String QUERY_MODE = "model";
    public static final String QUERY_COLD_TEMPER_SET = "getSetCold";
    public static final String QUERY_COLD_TEMPER_TURE = "getCold";
    public static final String QUERY_FREEZE_TEMPER_SET = "getSetFreeze";
    public static final String QUERY_FREEZE_TEMPER_TURE = "getFreeze";
    public static final String QUERY_MID_TEMPER_SET = "getsetCustomArea";
    public static final String QUERY_MID_TEMPER_TURE = "getCustomArea";


    public static final String QUERY_GOOD_FOOD = "getGoodFoodModel";
    public static final String QUERY_ZHENPIN_MODEL = "getZhenPinModel";
    public static final String QUERY_UV = "getUVModel";
}

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 401配置文件
 * Author:  Holy.Han
 * Date:  2016/12/29
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service.configtable;

import com.haiersmart.sfcontrol.constant.EnumBaseName;

import java.util.ArrayList;

/**
 * <p>function: </p>
 * <p>description:  401配置文件</p>
 * history:  1. 2016/12/29
 * Author: Holy.Han
 * modification:
 */
public class ConfigFourZeroOne {
    protected final String TAG = "ConfigFourZeroOne";
    private ArrayList<ProtocolConfigBase> protocolConfig;
    private ArrayList<ProtocolConfigBase> protocolDebugConfig;

    public ConfigFourZeroOne() {
        init();
    }

    private void init() {
        initConfig();
        initDebug();
    }

    private void initConfig(){
        protocolConfig = new ArrayList<>();
        //        protocolConfig.add(new ProtocolConfigBase("111c12002400081001030061800118420000000000", 0, 0, 0));//type id
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeShowTemp.name(), 1, 7, 8,38));//冷藏显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeShowTemp.name(), 1, 8, 8,38));//冷冻显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeTargetTemp.name(), 0, 9, 8, 1, 7, 0));//冷藏目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeTargetTemp.name(), 0, 10, 8, -23, -15, 26));//冷冻目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeRealTemp.name(),1,11,16,380));//冷藏实际温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeRealTemp.name(),1,13,16,380));//冷冻实际温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envRealTemp.name(),1,15,16,380));//环境实际温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envShowHum.name(),1,17,8));//环境湿度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostRealTemp.name(),1,18,16,380));//冷冻化霜实际温度

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.smartMode.name(), 0, 21, 2));//智能模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.purifyMode.name(), 0, 21, 3));//净化模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickFreezeMode.name(), 0, 21, 0));//速冻模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickColdMode.name(), 0, 21, 1));//速冷模式

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorStatus.name(), 1, 23, 2));//冷藏门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.marketDemo.name(), 1, 21, 7));//商场演示

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envTempSensorErr.name(), 1, 29, 2));//环境温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeSensorErr.name(), 1, 29, 3));//冷藏温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeSensorErr.name(), 1, 29, 0));//冷冻温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envHumSensorErr.name(), 1, 27, 1));//湿度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostSensorErr.name(), 1, 29, 1));//化霜传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostErr.name(), 1, 29, 5));//冷冻化霜故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeFanErr.name(),1,29,6));//冷冻风机故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.testMode.name(),1,20,8));//T模式

        //以下不是从主控板获取
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationErr.name(),1,1,10));//通信错误
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationOverTime.name(),1,1,10));//通信超时
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorErr.name(), 1, 1, 10));//冷藏门报警
    }

    // TODO: 2016/11/15 如果有调试信息，请增加
    private void initDebug() {
        protocolDebugConfig = new ArrayList<>();
    }

    public ArrayList<ProtocolConfigBase> getProtocolConfig() {
        return protocolConfig;
    }

    public ArrayList<ProtocolConfigBase> getProtocolDebugConfig() {
        return protocolDebugConfig;
    }
}

package com.haiersmart.sfcontrol.service.configtable;

import com.haiersmart.sfcontrol.constant.EnumBaseName;

import java.util.ArrayList;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/21
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class ConfigFourSevenSix {
    private ArrayList<ProtocolConfigBase> protocolConfig;
    private ArrayList<ProtocolConfigBase> protocolDebugConfig;

    public ConfigFourSevenSix() {
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
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envShowTemp.name(), 1, 11, 8,38));//环境显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envShowHum.name(), 1, 12, 8));//环境显示湿度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeTargetTemp.name(), 0, 13, 8, 1, 8, 0));//冷藏目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeTargetTemp.name(), 0, 14, 8, -24, -14, 26));//冷冻目标温度

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.smartMode.name(), 0, 20, 1));//智能模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.holidayMode.name(), 0, 20, 2));//假日模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickFreezeMode.name(), 0, 20, 3));//速冻模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickColdMode.name(), 0, 20, 4));//速冷模式

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.coldLightMode.name(), 1, 20, 5));//冷藏灯
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.handleLightMode.name(), 1, 20, 7));//把手灯

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorStatus.name(), 1, 22, 0));//冷藏门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDoorStatus.name(), 1, 22, 1));//冷冻门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.SterilizeSwitch.name(), 0, 22, 4));//杀菌
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.marketDemo.name(), 1, 21, 0));//商场演示

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envTempSensorErr.name(), 1, 32, 0));//环境温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeSensorErr.name(), 1, 32, 1));//冷藏温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeSensorErr.name(), 1, 32, 3));//冷冻温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.defrostSensorErr.name(), 1, 31, 0));//化霜传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostErr.name(), 1, 31, 6));//冷冻化霜故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envHumSensorErr.name(), 1, 31, 7));//环境湿度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeFanErr.name(), 1, 31, 4));//冷冻风机故障

        //以下不是从主控板获取
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationErr.name(),1,1,10));//通信错误
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationOverTime.name(),1,1,10));//通信超时
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorErr.name(), 1, 1, 10));//冷藏门报警
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDoorErr.name(), 1, 1, 10));//冷冻门报警

    }

    // TODO: 2016/11/15 如果有调试信息，请增加
    private void initDebug() {
        protocolDebugConfig = new ArrayList<>();
        //        protocolDebugConfig.add(new ProtocolConfigBase("no",1,0,0));
    }

    public ArrayList<ProtocolConfigBase> getProtocolConfig() {
        return protocolConfig;
    }

    public ArrayList<ProtocolConfigBase> getProtocolDebugConfig() {
        return protocolDebugConfig;
    }
}

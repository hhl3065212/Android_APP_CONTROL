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

public class ConfigSixFiveEight {
    private ArrayList<ProtocolConfigBase> protocolConfig;
    private ArrayList<ProtocolConfigBase> protocolDebugConfig;

    public ConfigSixFiveEight() {
        init();
    }

    private void init() {
        initConfig();
        initDebug();
    }

    private void initConfig(){
        protocolConfig = new ArrayList<>();
        //        protocolConfig.add(new ProtocolConfigBase("111c12002400081001020061800259430000000000", 0, 0, 0));//type id
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeShowTemp.name(), 1, 7, 8,38));//冷藏显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeShowTemp.name(), 1, 8, 8,38));//冷冻显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeShowTemp.name(), 1, 9, 8,38));//变温显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envShowTemp.name(), 1, 11, 8,38));//环境显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envShowHum.name(), 1, 12, 8));//环境显示湿度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeTargetTemp.name(), 0, 13, 8, 1, 9, 0));//冷藏目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeTargetTemp.name(), 0, 14, 8, -23, -15, 24));//冷冻目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeTargetTemp.name(), 0, 15, 8, -20, 5, 24));//冷冻目标温度
//
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeSwitch.name(),0,13,9));//冷藏关闭 冷藏档位设置为0 如有此项一定放在所有模式之前
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.smartMode.name(), 0, 20, 1));//智能模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.holidayMode.name(), 0, 20, 2));//假日模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickFreezeMode.name(), 0, 20, 3));//速冻模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickColdMode.name(), 0, 20, 4));//速冷模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.pirSwitch.name(), 0, 22, 6));//人感开关
//
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.forceDelete.name(), 1, 19, 7));//强制删除
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeTopLight.name(), 1, 23, 4));//冷藏顶灯
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeBackLight.name(), 1, 23, 5));//冷藏背灯
//
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorStatus.name(), 1, 22, 0));//冷藏左门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDoorStatus.name(), 1, 22, 1));//冷冻门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeDoorStatus.name(), 1, 22, 2));//变温门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorStatus.name(), 1, 24, 1));//冷藏右门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.insideDoorStatus.name(), 1, 23, 6));//门中门

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.pirStatus.name(), 1, 23, 3));//人感状态

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.marketDemo.name(), 1, 21, 0));//商场演示
//
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envTempSensorErr.name(), 1, 32, 0));//环境温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeSensorErr.name(), 1, 32, 1));//冷藏温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDefrostSensorErr.name(), 1, 32, 2));//冷藏化霜传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeSensorErr.name(), 1, 32, 3));//冷冻温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeSensorErr.name(), 1, 32, 4));//变温温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeDefrostSensorErr.name(), 1, 32, 5));//变温化霜传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostSensorErr.name(), 1, 31, 0));//冷冻化霜传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeFanErr.name(), 1, 31, 4));//冷冻风机故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.coldFanErr.name(), 1, 31, 5));//冷却风机故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostErr.name(), 1, 31, 6));//冷冻化霜故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envHumSensorErr.name(), 1, 31, 7));//环境湿度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDefrostErr.name(), 1, 30, 2));//冷藏化霜故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.pirErr.name(), 1, 30, 4));//人感故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.alsErr.name(), 1, 30, 5));//光感故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeFanErr.name(), 1, 30, 7));//冷藏风机故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeDefrostErr.name(), 1, 29, 5));//变温化霜故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeFanErr.name(), 1, 28, 7));//变温风机故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.dryAreaSensorErr.name(), 1, 27, 0));//干区传感器故障

        //以下不是从主控板获取
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationErr.name(),1,1,10));//通信错误
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationOverTime.name(),1,1,10));//通信超时
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorErr.name(), 1, 1, 10));//冷藏左门报警
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeRightDoorErr.name(), 1, 1, 10));//冷藏右门报警
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDoorErr.name(), 1, 1, 10));//冷冻门报警
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeDoorErr.name(), 1, 1, 10));//变温门报警
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.insideDoorErr.name(), 1, 1, 10));//门中门报警

    }

    // TODO: 2016/11/15 如果有调试信息，请增加
    private void initDebug() {
        protocolDebugConfig = new ArrayList<>();
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeRealTemp.name(),1,7,16,380));//冷藏实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDefrostRealTemp.name(),1,11,16,380));//冷藏化霜实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeRealTemp.name(),1,13,16,380));//冷冻实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostRealTemp.name(),1,15,16,380));//冷冻化霜实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.envRealTemp.name(),1,17,16,380));//环境实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeRealTemp.name(),1,19,16,380));//变温实际湿度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeDefrostRealTemp.name(),1,23,16,380));//变温化霜实际湿度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.dryAreaRealTemp.name(),1,25,16,380));//干区实际湿度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.envRealHum.name(),1,35,8));//环境实际湿度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeFanVoltage.name(),1,36,8));//冷冻风机电压
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.coldFanVoltage.name(),1,37,8));//冷却风机电压
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeFanVoltage.name(),1,38,8));//冷藏风机电压
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeFanVoltage.name(),1,39,8));//变温风机电压
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.pressorOneFreq.name(),1,40,8));//压机1频率
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.airDoorValve.name(),1,42,8));//风门阀状态
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.airDoor.name(),1,42,3));//冷冻风机电压
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeHeater.name(),1,44,0));//冷藏化霜加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeHeater.name(),1,44,1));//冷冻化霜加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeHeater.name(),1,44,2));//变温化霜加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.airDoorHeater.name(),1,44,3));//风门加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.verticalBridgeHeater.name(),1,44,4));//竖梁加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.doorBorderHeater.name(),1,44,5));//门边框加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeLight.name(),1,45,0));//冷藏室背灯
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.handleLight.name(),1,45,1));//冷藏室顶灯
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeLight.name(),1,45,2));//冷冻室灯
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.handleLight.name(),1,45,3));//变温室灯
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.testMode.name(),1,48,8));//T模式
    }

    public ArrayList<ProtocolConfigBase> getProtocolConfig() {
        return protocolConfig;
    }

    public ArrayList<ProtocolConfigBase> getProtocolDebugConfig() {
        return protocolDebugConfig;
    }
}

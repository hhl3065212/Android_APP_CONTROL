/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 256冰箱配置文件
 * Author:  Holy.Han
 * Date:  2016/12/26
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service.configtable;

import com.haiersmart.sfcontrol.constant.EnumBaseName;

import java.util.ArrayList;

/**
 * <p>function: </p>
 * <p>description:  256冰箱配置文件</p>
 * history:  1. 2016/12/26
 * Author: Holy.Han
 * modification:
 */
public class ConfigTwoFiveSix {
    protected final String TAG = "ConfigTwoFiveSix";
    private ArrayList<ProtocolConfigBase> protocolConfig;
    private ArrayList<ProtocolConfigBase> protocolDebugConfig;

    public ConfigTwoFiveSix() {
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
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeShowTemp.name(), 1, 9, 8,38));//变温显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envShowTemp.name(), 1, 10, 8,38));//环境显示温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeTargetTemp.name(), 0, 11, 8, 2, 10, 0));//冷藏目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeTargetTemp.name(), 0, 12, 8, -24, -16, 26));//冷冻目标温度
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeTargetTemp.name(), 0, 13, 8, -18, 5, 21));//变温目标温度

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeSwitch.name(),0,11,9));//冷藏关闭 冷藏档位设置为0 如有此项一定放在所有模式之前
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.smartMode.name(), 0, 20, 1));//智能模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.holidayMode.name(), 0, 20, 2));//假日模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickFreezeMode.name(), 0, 20, 3));//速冻模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.quickColdMode.name(), 0, 20, 4));//速冷模式
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.tidbitMode.name(),0,23,0));//珍品模式

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorStatus.name(), 1, 22, 0));//冷藏门
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.marketDemo.name(), 1, 21, 0));//商场演示

        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.envTempSensorErr.name(), 1, 32, 0));//环境温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeSensorErr.name(), 1, 32, 1));//冷藏温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeSensorErr.name(), 1, 32, 3));//冷冻温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.changeSensorErr.name(), 1, 32, 4));//变温温度传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.defrostSensorErr.name(), 1, 31, 0));//化霜传感器故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostErr.name(), 1, 31, 6));//冷冻化霜故障
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.freezeFanErr.name(),1,31,4));//冷冻风机故障

        //以下不是从主控板获取
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationErr.name(),1,1,10));//通信错误
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.communicationOverTime.name(),1,1,10));//通信超时
        protocolConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeDoorErr.name(), 1, 1, 10));//冷藏门报警
    }

    // TODO: 2016/11/15 如果有调试信息，请增加
    private void initDebug() {
        protocolDebugConfig = new ArrayList<>();
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeRealTemp.name(),1,7,16,380));//冷藏实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeRealTemp.name(),1,9,16,380));//冷冻实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeDefrostRealTemp.name(),1,11,16,380));//冷冻化霜实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.envRealTemp.name(),1,13,16,380));//环境实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeRealTemp.name(),1,15,16,380));//变温实际温度
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.freezeFanVoltage.name(),1,17,8));//冷冻风机电压
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.pressorOneFreq.name(),1,18,8));//压机1频率
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeAirDoor.name(),1,19,0));//冷藏风门
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeAirDoor.name(),1,19,2));//变温风门
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.defrostHeater.name(),1,20,0));//化霜加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.changeHeater.name(),1,20,1));//变温室加热丝
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.fridgeLight.name(),1,21,0));//冷藏灯
        protocolDebugConfig.add(new ProtocolConfigBase(EnumBaseName.testMode.name(),1,22,8));//T模式
    }

    public ArrayList<ProtocolConfigBase> getProtocolConfig() {
        return protocolConfig;
    }

    public ArrayList<ProtocolConfigBase> getProtocolDebugConfig() {
        return protocolDebugConfig;
    }
}

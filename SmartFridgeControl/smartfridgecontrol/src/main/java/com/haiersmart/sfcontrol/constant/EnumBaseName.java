package com.haiersmart.sfcontrol.constant;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/25
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public enum EnumBaseName {
    smartMode("smartMode"),//智能模式
    holidayMode("holidayMode"),//假日模式
    purifyMode("purifyMode"),//净化模式
    quickColdMode("quickColdMode"),//速冷模式
    quickFreezeMode("quickFreezeMode"),//速冻模式
    tidbitMode("tidbitMode"),//珍品模式
    fridgeSwitch("fridgeSwitch"),//冷藏开关
    strongPurifyMode("strongPurifyMode"),//强效净化模式
    fridgeTargetTemp("fridgeTargetTemp"),//冷藏室档位
    freezeTargetTemp("freezeTargetTemp"),//冷冻室档位
    changeTargetTemp("changeTargetTemp"),//变温室档位
    SterilizeMode("SterilizeMode"),//杀菌模式
    SterilizeSwitch("SterilizeSwitch"),//杀菌开关

    marketDemo("marketDemo"),//商场演示
    getDeviceId("getDeviceId"),//获取id
    getAllProperty("getAllProperty"),//获取所有参数
    fridgeShowTemp("fridgeShowTemp"),//冷藏显示温度
    freezeShowTemp("freezeShowTemp"),//冷冻显示温度
    changeShowTemp("changeShowTemp"),//变温显示温度
    envShowTemp("envShowTemp"),//环境显示温度
    envShowHum("envShowHum"),//环境显示湿度
    coldLightMode("coldLightMode"),//冷藏灯开关
    handleLightMode("handleLightMode"),//把手灯开关
    communicationErr("communicationErr"),//通信错误
    communicationOverTime("communicationOverTime"),//通信超时
    fridgeDoorStatus("fridgeDoorStatus"),//冷藏室（左、上）门
    freezeDoorStatus("freezeDoorStatus"),//冷冻室门
    changeDoorStatus("changeDoorStatus"),//变温室门
    fridgeRightDoorStatus("fridgeRightDoorStatus"),//冷藏室右门
    insideDoorStatus("insideDoorStatus"),//门中门状态
    fridgeDoorErr("fridgeDoorErr"),//冷藏室（左、上）门报警
    freezeDoorErr("freezeDoorErr"),//冷冻室门报警
    changeDoorErr("changeDoorErr"),//变温室门报警
    fridgeRightDoorErr("fridgeRightDoorErr"),//冷藏室右门报警
    insideDoorErr("insideDoorErr"),//门中门报警
    envTempSensorErr("envTempSensorErr"),//环境温度传感器故障
    envHumSensorErr("envHumSensorErr"),//环境湿度传感器故障
    fridgeSensorErr("fridgeSensorErr"),//冷藏室传感器故障
    freezeSensorErr("freezeSensorErr"),//冷冻室传感器故障
    changeSensorErr("changeSensorErr"),//变温室传感器故障
    defrostSensorErr("defrostSensorErr"),//化霜传感器故障
    fridgeDefrostSensorErr("fridgeDefrostSensorErr"),//冷藏化霜传感器故障
    freezeDefrostSensorErr("freezeDefrostSensorErr"),//冷冻化霜传感器故障
    changeDefrostSensorErr("changeDefrostSensorErr"),//变温化霜传感器故障
    fridgeDefrostErr("fridgeDefrostErr"),//冷藏化霜故障
    freezeDefrostErr("freezeDefrostErr"),//冷冻化霜故障
    changeDefrostErr("changeDefrostErr"),//变温化霜故障
    fridgeFanErr("fridgeFanErr"),//冷藏风机故障
    freezeFanErr("freezeFanErr"),//冷冻风机故障
    changeFanErr("changeFanErr"),//变温风机故障
    coldFanErr("coldFanErr"),//冷却风机故障
    dryAreaSensorErr("dryAreaSensorErr"),//干区传感器故障
    pirSwitch("pirSwitch"),//人感开关
    pirStatus("pirStatus"),//人感状态
    fridgeTopLight("fridgeTopLight"),//冷藏顶灯开关及状态
    fridgeBackLight("fridgeBackLight"),//冷藏背灯开关及状态
    insideDoor("insideDoor"),//门中门开关
    forceDelete("forceDelete"),//强行删除
    pirErr("pirErr"),//人感故障
    alsErr("alsErr"),//光感故障
//    debug
    getDebug("getDebug"),//获取debug参数
    fridgeRealTemp("fridgeRealTemp"),//冷藏室实际温度
    freezeRealTemp("freezeRealTemp"),//冷冻室实际温度
    changeRealTemp("changeRealTemp"),//变温室实际温度
    fridgeDefrostRealTemp("fridgeDefrostRealTemp"),//冷藏化霜实际温度
    freezeDefrostRealTemp("freezeDefrostRealTemp"),//冷冻化霜实际温度
    changeDefrostRealTemp("changeDefrostRealTemp"),//变温化霜实际温度
    dryAreaRealTemp("dryAreaRealTemp"),//干区实际温度
    envRealTemp("envRealTemp"),//环境实际温度
    envRealHum("envRealHum"),//环境实际温度
    fridgeFanVoltage("fridgeFanVoltage"),//冷藏风机电压
    freezeFanVoltage("freezeFanVoltage"),//冷冻风机电压
    changeFanVoltage("changeFanVoltage"),//变温风机电压
    coldFanVoltage("coldFanVoltage"),//冷却风机电压
    pressorOneFreq("pressorOneFreq"),//压机1频率
    fridgeAirDoor("fridgeAirDoor"),//冷藏室风门状态
    changeAirDoor("changeAirDoor"),//变温室风门状态
    airDoor("airDoor"),//风门状态
    airDoorValve("airDoorValve"),//风门阀状态
    defrostHeater("defrostHeater"),//化霜加热丝
    fridgeHeater("fridgeHeater"),//冷藏加热丝
    freezeHeater("freezeHeater"),//冷冻加热丝
    changeHeater("changeHeater"),//变温加热丝
    verticalBridgeHeater("verticalBridgeHeater"),//竖梁加热丝
    airDoorHeater("airDoorHeater"),//风门加热丝
    doorBorderHeater("doorBorderHeater"),//门边框加热丝
    fridgeLight("fridgeLight"),//冷藏室（顶）灯
    freezeLight("freezeLight"),//冷冻室灯
    changeLight("changeLight"),//变温室灯
    fridgeBackLightDebug("fridgeBackLightDebug"),//冷藏室背灯
    handleLight("handleLight"),//把手灯
    testMode("testMode");//测试模式





    private String nCode;

    EnumBaseName(String _nCode) {
        this.nCode = _nCode;
    }

    @Override
    public String toString() {
        return String.valueOf(this.nCode);
    }
}

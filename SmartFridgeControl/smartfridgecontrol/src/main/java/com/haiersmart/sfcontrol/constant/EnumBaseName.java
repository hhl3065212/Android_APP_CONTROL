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
    coldLightMode("coldLightMode"),//冷藏灯
    handleLightMode("handleLightMode"),//把手灯
    fridgeDoorStatus("fridgeDoorStatus"),//冷藏室门
    freezeDoorStatus("freezeDoorStatus"),//冷冻室门
    communicationErr("communicationErr"),//通信错误
    communicationOverTime("communicationOverTime"),//通信超时
    fridgeDoorErr("fridgeDoorErr"),//冷藏室门报警
    freezeDoorErr("freezeDoorErr"),//冷冻室门报警
    envTempSensorErr("envTempSensorErr"),//环境温度传感器故障
    fridgeSensorErr("fridgeSensorErr"),//冷藏室传感器故障
    freezeSensorErr("freezeSensorErr"),//冷冻室传感器故障
    changeSensorErr("changeSensorErr"),//变温室传感器故障
    defrostSensorErr("defrostSensorErr"),//化霜传感器故障
    freezeDefrostErr("freezeDefrostErr"),//冷冻化霜故障
    envHumSensorErr("envHumSensorErr"),//环境湿度传感器故障
    freezeFanErr("freezeFanErr"),//冷冻风机故障
    freezeDefrostSensorErr("freezeDefrostSensorErr"),//冷冻化霜传感器故障
    changeDoorErr("changeDoorErr"),//变温室门
//    debug
    getDebug("getDebug"),//获取debug参数
    fridgeRealTemp("fridgeRealTemp"),//冷藏室实际温度
    freezeRealTemp("freezeRealTemp"),//冷冻室实际温度
    freezeDefrostRealTemp("freezeDefrostRealTemp"),//冷冻化霜实际温度
    envRealTemp("envRealTemp"),//环境实际温度
    envRealHum("envRealHum"),//环境实际温度
    changeRealTemp("changeRealTemp"),//变温室实际温度
    freezeFanVoltage("freezeFanVoltage"),//冷冻风机电压
    pressorOneFreq("pressorOneFreq"),//压机1频率
    fridgeAirDoor("fridgeAirDoor"),//冷藏室风门状态
    changeAirDoor("changeAirDoor"),//变温室风门状态
    defrostHeater("defrostHeater"),//化霜加热丝
    changeHeater("changeHeater"),//变温加热丝
    verticalBridgeHeater("verticalBridgeHeater"),//竖梁加热丝
    fridgeLight("fridgeLight"),//冷藏室灯
    freezeLight("freezeLight"),//冷冻室灯
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

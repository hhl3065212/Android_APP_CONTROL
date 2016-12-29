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
    smartMode("smartMode"),//
    holidayMode("holidayMode"),//
    purifyMode("purifyMode"),//
    quickColdMode("quickColdMode"),//
    quickFreezeMode("quickFreezeMode"),//
    tidbitMode("tidbitMode"),//
    marketDemo("marketDemo"),//
    fridgeSwitch("fridgeSwitch"),
    strongPurifyMode("strongPurifyMode"),//
    fridgeTargetTemp("fridgeTargetTemp"),//
    freezeTargetTemp("freezeTargetTemp"),//
    changeTargetTemp("changeTargetTemp"),//
    SterilizeMode("SterilizeMode"),//

    getDeviceId("getDeviceId"),//
    getAllProperty("getAllProperty"),
    fridgeShowTemp("fridgeShowTemp"),
    freezeShowTemp("freezeShowTemp"),
    changeShowTemp("changeShowTemp"),
    envShowTemp("envShowTemp"),
    envShowHum("envShowHum"),
    coldLightMode("coldLightMode"),
    handleLightMode("handleLightMode"),
    fridgeDoorStatus("fridgeDoorStatus"),
    freezeDoorStatus("freezeDoorStatus"),
    communicationErr("communicationErr"),
    communicationOverTime("communicationOverTime"),
    fridgeDoorErr("fridgeDoorErr"),
    freezeDoorErr("freezeDoorErr"),
    envTempSensorErr("envTempSensorErr"),
    fridgeSensorErr("fridgeSensorErr"),
    freezeSensorErr("freezeSensorErr"),
    changeSensorErr("changeSensorErr"),
    defrostSensorErr("defrostSensorErr"),
    freezeDefrostErr("freezeDefrostErr"),
    envHumSensorErr("envHumSensorErr"),
    freezeFanErr("freezeFanErr"),
    freezeDefrostSensorErr("freezeDefrostSensorErr"),
//    debug
    getDebug("getDebug"),
    fridgeRealTemp("fridgeRealTemp"),
    freezeRealTemp("freezeRealTemp"),
    freezeDefrostRealTemp("freezeDefrostRealTemp"),
    envRealTemp("envRealTemp"),
    changeRealTemp("changeRealTemp"),
    freezeFanVoltage("freezeFanVoltage"),
    pressorOneFreq("pressorOneFreq"),
    fridgeAirDoor("fridgeAirDoor"),
    changeAirDoor("changeAirDoor"),
    defrostHeater("defrostHeater"),
    changeHeater("changeHeater"),
    fridgeLight("fridgeLight"),
    testMode("testMode");





    private String nCode;

    EnumBaseName(String _nCode) {
        this.nCode = _nCode;
    }

    @Override
    public String toString() {
        return String.valueOf(this.nCode);
    }
}

package com.haiersmart.sfcontrol.service;

import com.haiersmart.sfcontrol.service.mbmodel.MainBoardEntry;

import java.util.ArrayList;

/**
 * Created by tingting on 2016/11/1.
 */

public class ControlMainBoardInfo {
    private MainBoardParameters mMBParams;
    public  ControlMainBoardInfo(MainBoardParameters params) {
        mMBParams = params;
    }

    String getFridgeId() {
       return  mMBParams.getFridgeId();
    }

    String getFridgeVersion() {
       return mMBParams.getFridgeVersion();
    }

    String getFridgeFactory() {
        return mMBParams.getFridgeFactory();
    }

    String getFridgeSn() {
        return mMBParams.getFridgeSn();
    }


    public int getSmartMode() {
        int res = searchControlValue("smartMode");
        return res;
    }

    public int getHolidayMode() {
        int res = searchControlValue("holidayMode");
        return res;
    }
    //珍品模式
    public int getPurityMode() {
        int res = searchControlValue("purifyMode");
        return res;
    }

    //冷藏显示温度
    public int getFridgeShowTemp() {
        int res = searchStatusValue("fridgeShowTemp");
        return res;
    }
    //冷冻显示温度
    public int getFreezeShowTemp() {
        int res = searchStatusValue("freezeShowTemp");
        return res;
    }

    //变温显示温度
    public int getVariableShowTemp() {
        int res = searchStatusValue("changeShowTemp");
        return res;
    }

    //环境示温度
    public int getEnvShowTemp() {
        int res = searchStatusValue("envShowTemp");
        return res;
    }

    //环境显示湿度
    public int getEnvShowHumidity() {
        int res = searchStatusValue("envShowHumidity");
        return res;
    }
    //冷藏门
    public int getFridgeDoorStatus() {
        int res = searchStatusValue("fridgeDoorStatus");
        return res;
    }
    //通信错误
    public int getCommunicationErr() {
        int res = searchStatusValue("communicationErr");
        return res;
    }
    //冷藏门报警
    public int getFridgeDoorErr() {
        int res = searchStatusValue("fridgeDoorErr");
        return res;
    }
    //环境传感器故障
    public int getEnvSensorErr() {
        int res = searchStatusValue("envSensorErr");
        return res;
    }
    //冷藏传感器故障
    public int getFridgeSensorErr() {
        int res = searchStatusValue("fridgeSensorErr");
        return res;
    }
    //冷冻传感器故障
    public int getFreezeSensorErr() {
        int res = searchStatusValue("freezeSensorErr");
        return res;
    }
    //变温传感器故障
    public int getChangeSensorErr() {
        int res = searchStatusValue("changeSensorErr");
        return res;
    }

    //化霜传感器故障
    public int getDefrostSensorErr() {
        int res = searchStatusValue("defrostSensorErr");
        return res;
    }

    //冷冻化霜传感器故障
     public int getFreezeDefrostErr() {
        int res = searchStatusValue("freezeDefrostErr");
        return res;
    }

    //化霜传感器实际温度
   public int getdeFrostingSensorRealTemperature() {
        int res = searchStatusValue("defrostingSensorRealTemperature");
        return res;
    }


    private int searchControlValue(String funcName){
        int res = -1;
        ArrayList<MainBoardEntry> entryList = mMBParams.getMainBoardControl();
        int tableLength = entryList.size();
        String tempName = "";
        for(int i=0; i<tableLength; i++) {
            tempName = entryList.get(i).getName();
            if( tempName == funcName ) {
                res = entryList.get(i).getValue();
                break;
            }
        }
        return res;
    }

    private int searchStatusValue(String funcName){
        int res = -1;
        ArrayList<MainBoardEntry> entryList = mMBParams.getMainBoardStatus();
        int tableLength = entryList.size();
        String tempName = "";
        for(int i=0; i<tableLength; i++) {
            tempName = entryList.get(i).getName();
            if( tempName.equals(funcName) ) {
                res = entryList.get(i).getValue();
                break;
            }
        }
        return res;
    }


}

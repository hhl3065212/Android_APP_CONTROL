/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 256冰箱模型，模型的配置，档位同步，开关门及报警时间。此模型用于对冰箱模型的创建，使用MainBoardBase.java进行操作
 * Author:  Holy.Han
 * Date:  2016/12/29
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service.mbmodel;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.service.alarm.HandleDoorAlarm;
import com.haiersmart.sfcontrol.service.configtable.ConfigTwoFiveSix;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>function: </p>
 * <p>description:  256冰箱模型，模型的配置，档位同步，开关门及报警时间。此模型用于对冰箱模型的创建，使用MainBoardBase.java进行操作</p>
 * history:  1. 2016/12/29
 * Author: Holy.Han
 * modification:
 */
public class MainBoardTwoFiveSix extends MainBoardBase{

    /** 冷藏门中历史状态，用于来确定是否状态变化 */
    private static boolean mFridgeDoorHistoryStatus = false;
    /** 冷藏门从开门到报警时间 */
    private static final int mFridgeDoorAlarmStartTime = 3*60*1000;
    /** 冷藏门从报警到自动停止时间 */
    private static final int mFridgeDoorAlarmStopTime = 10*60*1000;
    /** 冷藏门开门报警操作 */
    private HandleDoorAlarm mFridgeDoorAlarm;

    @Override
    public void initConfig() {
        ConfigTwoFiveSix mConfigTwoFiveSix = new ConfigTwoFiveSix();
        mProtocolConfigStatus = mConfigTwoFiveSix.getProtocolConfig();
        mProtocolConfigDebug = mConfigTwoFiveSix.getProtocolDebugConfig();
        mFridgeDoorAlarm = new HandleDoorAlarm(mFridgeDoorAlarmStartTime, mFridgeDoorAlarmStopTime, "fridge") {
            @Override
            public void setDoorErr(boolean b) {
                setFridgeDoorErr(b);
            }
        };
    }

    @Override
    public ArrayList<byte[]> packSyncLevel() {
        ArrayList<byte[]> tmpSendBytes = new ArrayList<>();
        //256 有冷藏、冷冻、变温
        boolean fridgeCmdEn = true;//冷藏档位下发使能 初始为下发
        boolean freezeCmdEn = true;//冷冻档位下发使能 初始为下发
        boolean changeCmdEn = true;//变温档位下发使能 初始为下发
        for (FridgeControlEntry fridgeControlEntry:dbFridgeControlSet){
            if(fridgeControlEntry.name.equals(EnumBaseName.smartMode.name())){
                fridgeCmdEn = false;//智能 不比较冷藏档位
                freezeCmdEn = false;//智能 不比较冷冻档位
            }else if(fridgeControlEntry.name.equals(EnumBaseName.holidayMode.name())){
                fridgeCmdEn = false;//假日 不比较冷藏档位
            }else if(fridgeControlEntry.name.equals(EnumBaseName.quickFreezeMode.name())){
                freezeCmdEn = false;//速冻 不比较冷冻档位
            }else if(fridgeControlEntry.name.equals(EnumBaseName.quickColdMode.name())){
                changeCmdEn = false;//速冷 不比较变温档位
            }else if(fridgeControlEntry.name.equals(EnumBaseName.tidbitMode.name())){
                changeCmdEn = false;//珍品 不比较变温档位
            }
        }
        for (FridgeControlEntry fridgeControlEntry:dbFridgeControlCancel){
            if(fridgeControlEntry.name.equals(EnumBaseName.fridgeSwitch.toString())){
                fridgeCmdEn = false;//冷藏关闭 不比较冷藏档位
            }
        }
        if(fridgeCmdEn){
            FridgeControlEntry fridgeControlEntry = new FridgeControlEntry(EnumBaseName.fridgeTargetTemp.toString());
            mFridgeControlDbMgr.queryByName(fridgeControlEntry);
            int valueDb = fridgeControlEntry.value;
            int valueBoard = getMainBoardControlByName(EnumBaseName.fridgeTargetTemp.toString());
            //温度值不同 下发档位
            if(valueDb != valueBoard) {
                tmpSendBytes.add(packFridgeTargetTemp(valueDb));
            }
        }
        if(freezeCmdEn){
            FridgeControlEntry fridgeControlEntry = new FridgeControlEntry(EnumBaseName.freezeTargetTemp.toString());
            mFridgeControlDbMgr.queryByName(fridgeControlEntry);
            int valueDb = fridgeControlEntry.value;
            int valueBoard = getMainBoardControlByName(EnumBaseName.freezeTargetTemp.toString());
            //温度值不同 下发档位
            if(valueDb != valueBoard) {
                tmpSendBytes.add(packFreezeTargetTemp(valueDb));
            }
        }
        if(changeCmdEn){
            FridgeControlEntry fridgeControlEntry = new FridgeControlEntry(EnumBaseName.changeTargetTemp.toString());
            mFridgeControlDbMgr.queryByName(fridgeControlEntry);
            int valueDb = fridgeControlEntry.value;
            int valueBoard = getMainBoardControlByName(EnumBaseName.changeTargetTemp.toString());
            //温度值不同 下发档位
            if(valueDb != valueBoard) {
                tmpSendBytes.add(packChangeTargetTemp(valueDb));
            }
        }
        return tmpSendBytes;
    }

    @Override
    public void processInVainMessage(byte[] frame) {
        switch (frame[5]){
            case (byte)0x00://无效命令
                break;
            case (byte)0x01://智能状态下不可设
                break;
            case (byte)0x02://假日状态下不可设
                break;
            case (byte)0x03://速冻状态下不可设
                break;
            case (byte)0x04://速冷状态下不可设
                break;
            case (byte)0x06://传感器故障状态下不可设
                break;
            case (byte)0x07://档位超限
                break;
            case (byte)0x08://变温速冷状态下不可设
                break;
            default:
                break;
        }
    }

    @Override
    public void handleDoorEvents() {
        boolean isDoorChange = false;
        boolean bFridgeDoorNowStatus = getMainBoardStatusByName("fridgeDoorStatus")==1;
        //        boolean bFridgeDoorNowStatus = testDoor;
        HashMap<String,Integer> doorHashMap = new HashMap<>();

        if(bFridgeDoorNowStatus != mFridgeDoorHistoryStatus){
            mFridgeDoorHistoryStatus = bFridgeDoorNowStatus;
            if(bFridgeDoorNowStatus){
                MyLogUtil.i(TAG,"fridgeDoorStatus is open");
                mFridgeDoorAlarm.startAlarmTimer();
            }else {
                MyLogUtil.i(TAG,"fridgeDoorStatus is close");
                mFridgeDoorAlarm.stopAll();
            }
            isDoorChange = true;
        }
        if(isDoorChange){
            doorHashMap.put("fridge",bFridgeDoorNowStatus?1:0);
            doorHashMap.put("freeze",0);
            doorHashMap.put("change",0);
            String doorJson = JSON.toJSONString(doorHashMap);
            Intent intent = new Intent();
            intent.putExtra(ConstantUtil.DOOR_STATUS,doorJson);
            intent.setAction(ConstantUtil.SERVICE_NOTICE);
            ControlApplication.getInstance().sendBroadcast(intent);
        }
    }
}

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 476上层模型service
 * Author:  Holy.Han
 * Date:  2017/1/16
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service.model;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.database.FridgeStatusEntry;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>function: </p>
 * <p>description:  476上层模型service</p>
 * history:  1. 2017/1/16
 * Author: Holy.Han
 * modification:
 */
public class FourSevenSixModel extends ModelBase{
    protected final String TAG = "FourSevenSixModel";

    FourSevenSixModel(ControlMainBoardService service) {
        super(service);
    }

    @Override
    public void init() {
        initControlEntries();
        initTempStatusEntries();
        initErrorStatusEntries();
    }

    private void initControlEntries() {
        MyLogUtil.i(TAG, "initControlEntries in");
        mControlEntries = new ArrayList<FridgeControlEntry>();
        List<FridgeControlEntry> controlEntryList = getControlDbMgr().query();
        mControlEntries.add(controlEntryList.get(0));//0 智能模式
        mControlEntries.add(controlEntryList.get(1));//1 假日模式
        mControlEntries.add(controlEntryList.get(3));//2 速冷模式
        mControlEntries.add(controlEntryList.get(4));//3 速冻模式
        mControlEntries.add(controlEntryList.get(8));//4 冷藏档位模式
        mControlEntries.add(controlEntryList.get(9));//5 冷冻档位模式
        mControlEntries.add(controlEntryList.get(11));//6 杀菌模式
        mControlEntries.add(controlEntryList.get(12));//7 杀菌开关
        MyLogUtil.i(TAG, "initControlEntries out");
    }

    private void initTempStatusEntries() {
        mShowTempEntryList = new ArrayList<FridgeStatusEntry>();
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envTempSensorErr.name())));
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envTempSensorErr.name())));
    }
    private void initErrorStatusEntries() {
        mErrorEntryList = new ArrayList<FridgeStatusEntry>();
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.envTempSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envTempSensorErr.name())));//环境温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeSensorErr.name())));//冷藏温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeSensorErr.name())));//冷冻温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.defrostSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.defrostSensorErr.name())));//化霜传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeDefrostErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeDefrostErr.name())));//冷冻化霜故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.envHumSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envHumSensorErr.name())));//环境湿度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeFanErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeFanErr.name())));//冷冻风机故障

        //以下不是从主控板获取
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.communicationErr.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.communicationErr.name())));//通信错误
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.communicationOverTime.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.communicationOverTime.name())));//通信超时
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeDoorErr.name())));//冷藏门报警
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeDoorErr.name())));//冷冻门报警
    }

    @Override
    public void smartOn() {
        FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
        if (smartEntry.value == 0) {
            //如果速冷模式on,设置速冷模式off，数据库同步更新此状态
            FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
            if (coldEntry.value == 1) {
                mService.stopColdOnTime();
                //设置off
                setControlValueByName(EnumBaseName.quickColdMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.quickColdMode, 0);
            }
            //如果速冻模式on,发送设置速冻模式off cmd，数据库同步更新此状态
            FridgeControlEntry freezeEntry = getControlEntryByName(EnumBaseName.quickFreezeMode);
            if (freezeEntry.value == 1) {
                mService.stopFreezeOnTime();
                setControlValueByName(EnumBaseName.quickFreezeMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.quickFreezeMode, 0);
            }

            //如果假日模式on,发送设置假日模式off cmd，数据库同步更新此状态
            FridgeControlEntry holidayEntry = getControlEntryByName(EnumBaseName.holidayMode);
            if (holidayEntry.value == 1) {
                setControlValueByName(EnumBaseName.holidayMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.holidayMode, 0);
            }

            //进智能cmd
            smartEntry.value = 1;
            smartEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(smartEntry);
            getControlDbMgr().updateEntry(smartEntry);
            //设置冷藏档位不可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.SMART_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.SMART_ON_SET_TEMPER_WARNING);

            //设置冷冻档位不可调节
            setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.SMART_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.SMART_ON_SET_TEMPER_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void smartOff() {
        FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
        if (smartEntry.value == 1) {
            //退智能cmd
            smartEntry.value = 0;
            smartEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(smartEntry);
            getControlDbMgr().updateEntry(smartEntry);
            //恢复冷藏档位并设置可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            //恢复冷冻档位并设置可调节
            setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void freezeOn() {
        FridgeControlEntry freezeEntry = getControlEntryByName(EnumBaseName.quickFreezeMode);
        if (freezeEntry.value == 0) {
            //智能检查
            if (getControlValueByName(EnumBaseName.smartMode) == 1) {
                setControlValueByName(EnumBaseName.smartMode, 0);
                getControlDbMgr().updateValueByName(EnumBaseName.smartMode, 0);
                //恢复冷藏档位并设置可调节
                setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            }
            //设置速冻档位灰色
            setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.FREEZE_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.FREEZE_ON_SET_TEMPER_WARNING);

            //设置速冻on
            freezeEntry.value = 1;
            freezeEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(freezeEntry);
            getControlDbMgr().updateEntry(freezeEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void freezeOff() {
        FridgeControlEntry freezeEntry = getControlEntryByName(EnumBaseName.quickFreezeMode);
        if (freezeEntry.value == 1) {
            //设置速冻off
            freezeEntry.value = 0;
            freezeEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(freezeEntry);
            getControlDbMgr().updateEntry(freezeEntry);
            //速冻档位enable
            setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void coldOn() {
        FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
        if (coldEntry.value == 0) {
            if (getControlValueByName(EnumBaseName.smartMode) == 1) {
                setControlValueByName(EnumBaseName.smartMode, 0);
                getControlDbMgr().updateValueByName(EnumBaseName.smartMode, 0);
                //恢复冷冻档位并设置可调节
                setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
            }
            //如果假日模式on,发送设置假日模式off cmd，数据库同步更新此状态
            FridgeControlEntry holidayEntry = getControlEntryByName(EnumBaseName.holidayMode);
            if (holidayEntry.value == 1) {
                setControlValueByName(EnumBaseName.holidayMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.holidayMode, 0);
            }
            //进速冷
            coldEntry.value = 1;
            coldEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(coldEntry);
            getControlDbMgr().updateEntry(coldEntry);
            //设置冷藏档位disable
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.CLOD_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.CLOD_ON_SET_TEMPER_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void coldOff() {
        FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
        if (coldEntry.value == 1) {
            //退速冷
            coldEntry.value = 0;
            coldEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(coldEntry);
            getControlDbMgr().updateEntry(coldEntry);
            //设置变温档位enable,档位值显示
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void setCold(int coldTemper) {
        FridgeControlEntry coldLevelEntry = getControlEntryByName(EnumBaseName.fridgeTargetTemp);
        coldLevelEntry.value = coldTemper;
        setControlValueByName(EnumBaseName.fridgeTargetTemp, coldTemper);
        getControlDbMgr().updateValue(coldLevelEntry);
        mService.sendControlCmdResponse();
    }

    @Override
    public void setFreeze(int freezeTemper) {
        FridgeControlEntry freezeLevelEntry = getControlEntryByName(EnumBaseName.freezeTargetTemp);
        freezeLevelEntry.value = freezeTemper;
        updateControlByEntry(freezeLevelEntry);
        getControlDbMgr().updateValue(freezeLevelEntry);
        mService.sendControlCmdResponse();
    }

    @Override
    public void holidayOn() {
        FridgeControlEntry holidayEntry = getControlEntryByName(EnumBaseName.holidayMode);
        if(holidayEntry.value == 0) {
            //检查智能模式
            FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
            if(smartEntry.value == 1) {
                //退智能
                smartEntry.value = 0;
                smartEntry.disable = ConstantUtil.NO_WARNING;
                updateControlByEntry(smartEntry);
                getControlDbMgr().updateEntry(smartEntry);
                //设置冷冻档位可调节
                setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
            }

            FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
            if (coldEntry.value == 1) {
                mService.stopColdOnTime();
                //设置off
                setControlValueByName(EnumBaseName.quickColdMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.quickColdMode, 0);
            }

            //设置冷藏档位不可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            MyLogUtil.d(TAG, "holidayOn modedebug  fridgeTargetTemp disable=" + getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);

            //进假日
            holidayEntry.value = 1;
            holidayEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(holidayEntry);
            getControlDbMgr().updateEntry(holidayEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void holidayOff() {
        FridgeControlEntry holidayEntry = getControlEntryByName(EnumBaseName.holidayMode);
        if(holidayEntry.value == 1) {
            //退假日
            holidayEntry.value = 0;
            holidayEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(holidayEntry);
            getControlDbMgr().updateEntry(holidayEntry);

            //恢复冷藏档位设置
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            MyLogUtil.d(TAG,"holidayOff modedebug fridgeTargetTemp disable="+getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);

        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void setSterilizeMode(int step){
        FridgeControlEntry sterilizeEntry = getControlEntryByName(EnumBaseName.SterilizeMode);
        FridgeControlEntry sterilizeSwitchEntry = getControlEntryByName(EnumBaseName.SterilizeSwitch);
        if(sterilizeEntry.value != step) {
            if(step == 0){
                mService.stopSterilize();
                if(sterilizeSwitchEntry.value == 1) {
                    sterilizeSwitchEntry.value = 0;
                    sterilizeSwitchEntry.disable = ConstantUtil.NO_WARNING;
                    updateControlByEntry(sterilizeSwitchEntry);
                    getControlDbMgr().updateEntry(sterilizeSwitchEntry);
                }
            }
            sterilizeEntry.value = step;
            sterilizeEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(sterilizeEntry);
            getControlDbMgr().updateEntry(sterilizeEntry);
            if(step != 0) {
                mService.startSterilize(step,true);
                if(sterilizeSwitchEntry.value == 0) {
                    sterilizeSwitchEntry.value = 1;
                    sterilizeSwitchEntry.disable = ConstantUtil.NO_WARNING;
                    updateControlByEntry(sterilizeSwitchEntry);
                    getControlDbMgr().updateEntry(sterilizeSwitchEntry);
                }
            }
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }


    @Override
    public void sterilizeSwitchOn(){
        FridgeControlEntry sterilizeSwitchEntry = getControlEntryByName(EnumBaseName.SterilizeSwitch);
        if(sterilizeSwitchEntry.value == 0) {
            sterilizeSwitchEntry.value = 1;
            sterilizeSwitchEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(sterilizeSwitchEntry);
            getControlDbMgr().updateEntry(sterilizeSwitchEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG,"ster::sterilizeSwitchOn");
    }
    @Override
    public void sterilizeSwitchOff(){
        FridgeControlEntry sterilizeSwitchEntry = getControlEntryByName(EnumBaseName.SterilizeSwitch);
        if(sterilizeSwitchEntry.value == 1) {
            sterilizeSwitchEntry.value = 0;
            sterilizeSwitchEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(sterilizeSwitchEntry);
            getControlDbMgr().updateEntry(sterilizeSwitchEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG,"ster::sterilizeSwitchOff");
    }

    @Override
    public void handleStatusDataResponse() {
        if (mShowTempEntryList == null) {
            initTempStatusEntries();
        }
        //显示温度信息处理
        handleTemperInfoResponse();
        if (mErrorEntryList == null) {
            initErrorStatusEntries();
        }
        // 错误故障信息处理
        handleErrorInfoResponse();
        mService.handleDoorEvents();

    }

//    private void handleTemperInfoResponse() {
//        MyLogUtil.v(TAG, "handleTemperInfoResponse in");
//        Boolean isTempChanged = false;
//
//        for(FridgeStatusEntry fridgeStatusEntry:mShowTempEntryList){
//            int showTemp = getMainBoardInfo().searchStatusValueBoard(fridgeStatusEntry.name);
//            if(fridgeStatusEntry.value != showTemp){
//                fridgeStatusEntry.value = showTemp;
//                isTempChanged = true;
//            }
//        }
//
//        if (isTempChanged) {
//            mService.notifyTemperChanged(mShowTempEntryList);
//            MyLogUtil.d("printSerialString", "temper");
//            RemoteUtil.sendQuery();
//        }
//        MyLogUtil.v(TAG, "handleTemperInfoResponse out");
//    }
//
//    private void handleErrorInfoResponse() {
//        Boolean isErrOccurred = false;
//
//        for(FridgeStatusEntry fridgeStatusEntry:mErrorEntryList) {
//            int errorMessage = getMainBoardInfo().searchStatusValueBoard(fridgeStatusEntry.name);
//            if (fridgeStatusEntry.value != errorMessage) {
//                fridgeStatusEntry.value = errorMessage;
//                isErrOccurred = true;
//            }
//        }
//
//        if (isErrOccurred) {
//            mService.notifyErrorOccurred(mErrorEntryList);
//            MyLogUtil.d("printSerialString", "error");
//            RemoteUtil.sendQuery();
//        }
//    }
}

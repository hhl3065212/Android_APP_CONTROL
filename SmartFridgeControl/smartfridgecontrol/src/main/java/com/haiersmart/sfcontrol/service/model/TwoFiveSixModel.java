/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 256冰箱service模型，模式互斥逻辑
 * Author:  Holy.Han
 * Date:  2017/1/9
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
 * <p>description:  256冰箱service模型，模式互斥逻辑</p>
 * history:  1. 2017/1/9
 * Author: Holy.Han
 * modification:
 */
public class TwoFiveSixModel extends ModelBase {
    protected final String TAG = "TwoFiveSixModel";

    TwoFiveSixModel(ControlMainBoardService service) {
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
        mControlEntries.add(controlEntryList.get(5));//4 珍品模式
        mControlEntries.add(controlEntryList.get(6));//8 冷藏开关
        mControlEntries.add(controlEntryList.get(8));//5 冷藏档位模式
        mControlEntries.add(controlEntryList.get(9));//6 冷冻档位模式
        mControlEntries.add(controlEntryList.get(10));//7 变温档位模式
        MyLogUtil.i(TAG, "initControlEntries out");
    }

    private void initTempStatusEntries() {
        mShowTempEntryList = new ArrayList<FridgeStatusEntry>();
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeShowTemp.name())));
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.changeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeShowTemp.name())));
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeShowTemp.name())));
    }

    private void initErrorStatusEntries() {
        mErrorEntryList = new ArrayList<FridgeStatusEntry>();
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.envTempSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envTempSensorErr.name())));//环境温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeSensorErr.name())));//冷藏温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeSensorErr.name())));//冷冻温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.changeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeSensorErr.name())));//变温温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.defrostSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.defrostSensorErr.name())));//化霜传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeDefrostErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeDefrostErr.name())));//冷冻化霜故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeFanErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeFanErr.name())));//冷冻风机故障

        //以下不是从主控板获取
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.communicationErr.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.communicationErr.name())));//通信错误
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.communicationOverTime.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.communicationOverTime.name())));//通信超时
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeDoorErr.name())));//冷藏门报警
    }

    @Override
    public void smartOn() {
        MyLogUtil.i(TAG, "smartOn in");
        FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
        if (smartEntry.value == 0) {
            //如果假日模式on,设置假日模式off，数据库同步更新此状态
            if (getControlValueByName(EnumBaseName.holidayMode) == 1) {
                //设置off
                setControlValueByName(EnumBaseName.holidayMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.holidayMode, 0);
            }
            //如果速冻模式on,发送设置速冻模式off cmd，数据库同步更新此状态
            FridgeControlEntry freezeEntry = getControlEntryByName(EnumBaseName.quickFreezeMode);
            MyLogUtil.i(TAG, "smartOn  freezeEntry.value=" + freezeEntry.value);
            if (freezeEntry.value == 1) {
                mService.stopFreezeOnTime();
                setControlValueByName(EnumBaseName.quickFreezeMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.quickFreezeMode, 0);
            }

            //如果冷藏关闭，设置open
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            if (fridgeCloseEntry.value == 0) {
                fridgeCloseEntry.value = 1;
            }
            //设置冷藏开关不可调节
            fridgeCloseEntry.disable = ConstantUtil.SMART_ON_REFRIGERATOR_CLOSE_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);

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
        MyLogUtil.i(TAG, "smartOn out");
    }

    @Override
    public void smartOff() {
        MyLogUtil.i(TAG, "smartOff in");
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
            //恢复冷藏关闭可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG, "smartOff out");
    }

    @Override
    public void freezeOn() {
        MyLogUtil.i(TAG, "freezeOn in");
        FridgeControlEntry freezeEntry = getControlEntryByName(EnumBaseName.quickFreezeMode);
        if (freezeEntry.value == 0) {
            //智能检查
            if (getControlValueByName(EnumBaseName.smartMode) == 1) {
                setControlValueByName(EnumBaseName.smartMode, 0);
                getControlDbMgr().updateValueByName(EnumBaseName.smartMode, 0);
                //恢复冷藏档位并设置可调节
                setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);

                //设置冷藏开关可调节
                setControlDisableByName(EnumBaseName.fridgeSwitch, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.fridgeSwitch, ConstantUtil.NO_WARNING);

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
        MyLogUtil.i(TAG, "freezeOn out");
    }

    @Override
    public void freezeOff() {
        MyLogUtil.i(TAG, "freezeOff in");
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
        MyLogUtil.i(TAG, "freezeOff out");
    }

    @Override
    public void coldOn() {
        MyLogUtil.i(TAG, "coldOn in");
        FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
        if (coldEntry.value == 0) {
            //进速冷
            coldEntry.value = 1;
            coldEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(coldEntry);
            getControlDbMgr().updateEntry(coldEntry);
            //设置变温档位disable
            setControlDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.CLOD_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.CLOD_ON_SET_TEMPER_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG, "coldOn out");
    }

    @Override
    public void coldOff() {
        MyLogUtil.i(TAG, "coldOff in");
        FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
        if (coldEntry.value == 1) {
            //退速冷
            coldEntry.value = 0;
            coldEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(coldEntry);
            getControlDbMgr().updateEntry(coldEntry);
            //设置变温档位enable,档位值显示
            setControlDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.NO_WARNING);
            ;
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG, "coldOff out");
    }

    @Override
    public void setCold(int coldTemper) {
        MyLogUtil.i(TAG, "setCold coldTemper=" + coldTemper);
        FridgeControlEntry coldLevelEntry = getControlEntryByName(EnumBaseName.fridgeTargetTemp);
        coldLevelEntry.value = coldTemper;
        setControlValueByName(EnumBaseName.fridgeTargetTemp, coldTemper);
        getControlDbMgr().updateValue(coldLevelEntry);
        mService.sendControlCmdResponse();
    }

    @Override
    public void setFreeze(int freezeTemper) {
        MyLogUtil.i(TAG, "setFreeze freezeTemper=" + freezeTemper);
        FridgeControlEntry freezeLevelEntry = getControlEntryByName(EnumBaseName.freezeTargetTemp);
        freezeLevelEntry.value = freezeTemper;
        updateControlByEntry(freezeLevelEntry);
        getControlDbMgr().updateValue(freezeLevelEntry);
        mService.sendControlCmdResponse();
    }

    @Override
    public void holidayOn() {
        FridgeControlEntry holidayEntry = getControlEntryByName(EnumBaseName.holidayMode);
        if (holidayEntry.value == 0) {
            //检查智能模式
            FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
            if (smartEntry.value == 1) {
                //退智能
                smartEntry.value = 0;
                smartEntry.disable = ConstantUtil.NO_WARNING;
                updateControlByEntry(smartEntry);
                getControlDbMgr().updateEntry(smartEntry);
                //设置冷冻档位可调节
                setControlDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.freezeTargetTemp, ConstantUtil.NO_WARNING);
            }

            //设置冷藏档位不可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            MyLogUtil.d(TAG, "holidayOn modedebug  fridgeTargetTemp disable=" + getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);

            //设置冷藏开关不可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            //如果冷藏关闭，设置open
            if (fridgeCloseEntry.value == 0) {
                fridgeCloseEntry.value = 1;
            }
            fridgeCloseEntry.disable = ConstantUtil.HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
            MyLogUtil.d(TAG, "holidayOn modedebug fridgeSwitch disable=" + getControlEntryByName(EnumBaseName.fridgeSwitch).disable);

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
        if (holidayEntry.value == 1) {
            //退假日
            holidayEntry.value = 0;
            holidayEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(holidayEntry);
            getControlDbMgr().updateEntry(holidayEntry);

            //恢复冷藏档位设置
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            MyLogUtil.d(TAG, "holidayOff modedebug fridgeTargetTemp disable=" + getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);

            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            MyLogUtil.d(TAG, "holidayOff modedebug fridgeCloseMode disable=" + getControlEntryByName(EnumBaseName.fridgeSwitch).disable);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void refrigeratorOpen() {
        FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
        if (fridgeCloseEntry.value == 0) {
            //冷藏开
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateValue(fridgeCloseEntry);
            MyLogUtil.d(TAG, "refrigeratorOpen modedebug fridgeSwitch disable=" + getControlEntryByName(EnumBaseName.fridgeSwitch).disable);
            //冷藏档位可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            MyLogUtil.d(TAG, "refrigeratorOpen modedebug fridgeTargetTemp disable=" + getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();

    }

    @Override
    public void refrigeratorClose() {
        FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
        if (fridgeCloseEntry.value == 1) {
            fridgeCloseEntry.value = 0;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateValue(fridgeCloseEntry);
            MyLogUtil.d(TAG, "refrigeratorClose modedebug fridgeSwitch disable=" + getControlEntryByName(EnumBaseName.fridgeSwitch).disable);
            //冷藏档位不可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING);
            MyLogUtil.d(TAG, "refrigeratorClose modedebug fridgeTargetTemp disable=" + getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void setCustomArea(int customTemper) {
        MyLogUtil.i(TAG, "setCustomArea customTemper=" + customTemper);
        setControlValueByName(EnumBaseName.changeTargetTemp, customTemper);
        getControlDbMgr().updateValueByName(EnumBaseName.changeTargetTemp, customTemper);
        mService.sendControlCmdResponse();
    }

    @Override
    public void tidbitOn() {
        FridgeControlEntry tidbitEntry = getControlEntryByName(EnumBaseName.tidbitMode);
        if (tidbitEntry.value == 0) {
            //是否有速冷
            FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
            //退速冷
            if(coldEntry.value == 1) {
                coldEntry.value = 0;
                mService.stopColdOnTime();
            }
            coldEntry.disable = ConstantUtil.TIDBIT_ON_SET_TEMPER_WARNING;
            updateControlByEntry(coldEntry);
            getControlDbMgr().updateEntry(coldEntry);

            //进珍品
            tidbitEntry.value = 1;
            tidbitEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(tidbitEntry);
            getControlDbMgr().updateEntry(tidbitEntry);
            //设置变温档位disable
            setControlDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.TIDBIT_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.TIDBIT_ON_SET_TEMPER_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void tidbitOff() {
        FridgeControlEntry tidbitEntry = getControlEntryByName(EnumBaseName.tidbitMode);
        if (tidbitEntry.value == 1) {
            FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
            //速冷去掉disable
//            coldEntry.value = 0;
            coldEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(coldEntry);
            getControlDbMgr().updateEntry(coldEntry);
            //退珍品
            tidbitEntry.value = 0;
            tidbitEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(tidbitEntry);
            getControlDbMgr().updateEntry(tidbitEntry);
            //设置变温档位disable
            setControlDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.NO_WARNING);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
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
//        int fridgeShowTemp = getMainBoardInfo().getFridgeShowTemp();
//        if (mShowTempEntryList.get(0).value != fridgeShowTemp) {
//            mShowTempEntryList.get(0).value = fridgeShowTemp;
//            isTempChanged = true;
//        }
//
//        int variableTemp = getMainBoardInfo().getVariableShowTemp();
//        if (mShowTempEntryList.get(1).value != variableTemp) {
//            mShowTempEntryList.get(1).value = variableTemp;
//            isTempChanged = true;
//        }
//
//        int freezeTemp = getMainBoardInfo().getFreezeShowTemp();
//        if (mShowTempEntryList.get(2).value != freezeTemp) {
//            mShowTempEntryList.get(2).value = freezeTemp;
//            isTempChanged = true;
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
//        int envShowTemp = getMainBoardInfo().getEnvShowTemp();
//        if (mErrorEntryList.get(0).value != envShowTemp) {
//            mErrorEntryList.get(0).value = envShowTemp;
//            isErrOccurred = true;
//        }
//
//        //        int envShowHumidity = getMainBoardInfo().getEnvShowHumidity();
//        //        if(mErrorEntryList.get(1).value != envShowHumidity) {
//        //            mErrorEntryList.get(1).value = envShowHumidity;
//        //            isErrOccurred = true;
//        //        }
//
//        int communicationOverTime = getMainBoardInfo().getCommunicationOverTime();
//        if (mErrorEntryList.get(1).value != communicationOverTime) {
//            mErrorEntryList.get(1).value = communicationOverTime;
//            isErrOccurred = true;
//        }
//
//        int envTempSensorErr = getMainBoardInfo().getEnvSensorErr();
//        if (mErrorEntryList.get(2).value != envTempSensorErr) {
//            mErrorEntryList.get(2).value = envTempSensorErr;
//            isErrOccurred = true;
//        }
//
//        int fridgeShowTempSensorErr = getMainBoardInfo().getFridgeSensorErr();
//        if (mErrorEntryList.get(3).value != fridgeShowTempSensorErr) {
//            mErrorEntryList.get(3).value = fridgeShowTempSensorErr;
//            isErrOccurred = true;
//        }
//
//        int freezeTempSensorErr = getMainBoardInfo().getFreezeSensorErr();
//        if (mErrorEntryList.get(4).value != freezeTempSensorErr) {
//            mErrorEntryList.get(4).value = freezeTempSensorErr;
//            isErrOccurred = true;
//        }
//
//        int variableTempSensorErr = getMainBoardInfo().getChangeSensorErr();
//        if (mErrorEntryList.get(5).value != variableTempSensorErr) {
//            mErrorEntryList.get(5).value = variableTempSensorErr;
//            isErrOccurred = true;
//        }
//
//        int defrostingSensorErr = getMainBoardInfo().getDefrostSensorErr();
//        if (mErrorEntryList.get(6).value != defrostingSensorErr) {
//            mErrorEntryList.get(6).value = defrostingSensorErr;
//            isErrOccurred = true;
//        }
//
//        int freezerDefrostingSensorErr = getMainBoardInfo().getFreezeDefrostErr();
//        if (mErrorEntryList.get(7).value != freezerDefrostingSensorErr) {
//            mErrorEntryList.get(7).value = freezerDefrostingSensorErr;
//            isErrOccurred = true;
//        }
//        int freezeFanErr = getMainBoardInfo().getFreezeFanErr();
//        if (mErrorEntryList.get(8).value != freezeFanErr) {
//            mErrorEntryList.get(8).value = freezeFanErr;
//            isErrOccurred = true;
//        }
//
//        if (isErrOccurred) {
//            mService.notifyErrorOccurred(mErrorEntryList);
//            MyLogUtil.d("printSerialString", "error");
//            RemoteUtil.sendQuery();
//        }
//    }
}

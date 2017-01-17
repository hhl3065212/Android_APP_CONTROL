/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 401模型service与上层
 * Author:  Holy.Han
 * Date:  2017/1/11
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
 * <p>description:  401模型service与上层</p>
 * history:  1. 2017/1/11
 * Author: Holy.Han
 * modification:
 */
public class FourZeroOneModel extends ModelBase {
    protected final String TAG = "FourZeroOneModel";

    FourZeroOneModel(ControlMainBoardService service) {
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
        mControlEntries.add(controlEntryList.get(2));//1 净化模式
        mControlEntries.add(controlEntryList.get(3));//2 速冷模式
        mControlEntries.add(controlEntryList.get(4));//3 速冻模式
        mControlEntries.add(controlEntryList.get(8));//5 冷藏档位模式
        mControlEntries.add(controlEntryList.get(9));//6 冷冻档位模式
        MyLogUtil.i(TAG, "initControlEntries out");
    }

    private void initTempStatusEntries() {
        mShowTempEntryList = new ArrayList<FridgeStatusEntry>();
        mShowTempEntryList.add(new FridgeStatusEntry("fridgeShowTemp", getMainBoardInfo().getFridgeShowTemp()));
        mShowTempEntryList.add(new FridgeStatusEntry("freezeShowTemp", getMainBoardInfo().getFreezeShowTemp()));
    }

    private void initErrorStatusEntries() {
        mErrorEntryList = new ArrayList<FridgeStatusEntry>();
        mErrorEntryList.add(new FridgeStatusEntry("envRealTemp", getMainBoardInfo().getEnvRealTemp()));
        mErrorEntryList.add(new FridgeStatusEntry("envShowHum", getMainBoardInfo().getEnvShowHum()));
        mErrorEntryList.add(new FridgeStatusEntry("communicationOverTime", getMainBoardInfo().getCommunicationErr()));
        mErrorEntryList.add(new FridgeStatusEntry("envTempSensorErr", getMainBoardInfo().getEnvTempSensorErr()));
        mErrorEntryList.add(new FridgeStatusEntry("fridgeSensorErr", getMainBoardInfo().getFridgeSensorErr()));
        mErrorEntryList.add(new FridgeStatusEntry("freezeSensorErr", getMainBoardInfo().getFreezeSensorErr()));
        mErrorEntryList.add(new FridgeStatusEntry("envHumSensorErr", getMainBoardInfo().getEnvHumSensorErr()));
        mErrorEntryList.add(new FridgeStatusEntry("freezeDefrostSensorErr", getMainBoardInfo().getFreezeDefrostSensorErr()));
        mErrorEntryList.add(new FridgeStatusEntry("freezeDefrostErr", getMainBoardInfo().getFreezeDefrostErr()));
        mErrorEntryList.add(new FridgeStatusEntry("freezeFanErr", getMainBoardInfo().getFreezeFanErr()));
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
    public void purifyOn() {
        FridgeControlEntry purifyEntry = getControlEntryByName(EnumBaseName.purifyMode);
        if (purifyEntry.value == 0) {
            //进净化
            purifyEntry.value = 1;
            purifyEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(purifyEntry);
            getControlDbMgr().updateEntry(purifyEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void purifyOff() {
        FridgeControlEntry purifyEntry = getControlEntryByName(EnumBaseName.purifyMode);
        if (purifyEntry.value == 1) {
            //进净化
            purifyEntry.value = 0;
            purifyEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(purifyEntry);
            getControlDbMgr().updateEntry(purifyEntry);
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
    }

    private void handleTemperInfoResponse() {
        MyLogUtil.v(TAG, "handleTemperInfoResponse in");
        Boolean isTempChanged = false;

        int fridgeShowTemp = getMainBoardInfo().getFridgeShowTemp();
        if (mShowTempEntryList.get(0).value != fridgeShowTemp) {
            mShowTempEntryList.get(0).value = fridgeShowTemp;
            isTempChanged = true;
        }

        int freezeTemp = getMainBoardInfo().getFreezeShowTemp();
        if (mShowTempEntryList.get(1).value != freezeTemp) {
            mShowTempEntryList.get(1).value = freezeTemp;
            isTempChanged = true;
        }

        if (isTempChanged) {
            mService.notifyTemperChanged(mShowTempEntryList);
            MyLogUtil.d("printSerialString", "temper");
            mService.sendQuery();
        }
        MyLogUtil.v(TAG, "handleTemperInfoResponse out");
    }


    private void handleErrorInfoResponse() {
        Boolean isErrOccurred = false;

        int envRealTemp = getMainBoardInfo().getEnvRealTemp();
        if (mErrorEntryList.get(0).value != envRealTemp) {
            mErrorEntryList.get(0).value = envRealTemp;
            isErrOccurred = true;
        }

        int envShowHum = getMainBoardInfo().getEnvShowHum();
        if (mErrorEntryList.get(1).value != envShowHum) {
            mErrorEntryList.get(1).value = envShowHum;
            isErrOccurred = true;
        }

        int communicationOverTime = getMainBoardInfo().getCommunicationOverTime();
        if (mErrorEntryList.get(2).value != communicationOverTime) {
            mErrorEntryList.get(2).value = communicationOverTime;
            isErrOccurred = true;
        }

        int envTempSensorErr = getMainBoardInfo().getEnvTempSensorErr();
        if (mErrorEntryList.get(3).value != envTempSensorErr) {
            mErrorEntryList.get(3).value = envTempSensorErr;
            isErrOccurred = true;
        }

        int fridgeShowTempSensorErr = getMainBoardInfo().getFridgeSensorErr();
        if (mErrorEntryList.get(4).value != fridgeShowTempSensorErr) {
            mErrorEntryList.get(4).value = fridgeShowTempSensorErr;
            isErrOccurred = true;
        }

        int freezeTempSensorErr = getMainBoardInfo().getFreezeSensorErr();
        if (mErrorEntryList.get(5).value != freezeTempSensorErr) {
            mErrorEntryList.get(5).value = freezeTempSensorErr;
            isErrOccurred = true;
        }


        int freezerDefrostingSensorErr = getMainBoardInfo().getFreezeDefrostSensorErr();
        if (mErrorEntryList.get(6).value != freezerDefrostingSensorErr) {
            mErrorEntryList.get(6).value = freezerDefrostingSensorErr;
            isErrOccurred = true;
        }

        int freezerDefrostingErr = getMainBoardInfo().getFreezeDefrostErr();
        if (mErrorEntryList.get(7).value != freezerDefrostingErr) {
            mErrorEntryList.get(7).value = freezerDefrostingErr;
            isErrOccurred = true;
        }
        int freezeFanErr = getMainBoardInfo().getFreezeFanErr();
        if (mErrorEntryList.get(8).value != freezeFanErr) {
            mErrorEntryList.get(8).value = freezeFanErr;
            isErrOccurred = true;
        }

        if (isErrOccurred) {
            mService.notifyErrorOccurred(mErrorEntryList);
            MyLogUtil.d("printSerialString", "error");
            mService.sendQuery();
        }
    }
}

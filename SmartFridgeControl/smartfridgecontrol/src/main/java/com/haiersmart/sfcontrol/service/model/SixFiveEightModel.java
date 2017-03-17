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

import static com.haiersmart.sfcontrol.constant.EnumBaseName.fridgeShowTemp;

/**
 * <p>function: </p>
 * <p>description:  658上层模型service</p>
 * history:  1. 2017/1/16
 * Author: Holy.Han
 * modification:
 */
public class SixFiveEightModel extends ModelBase{
    protected final String TAG = "FourSevenSixModel";

    SixFiveEightModel(ControlMainBoardService service) {
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
        mControlEntries.add(controlEntryList.get(6));//7 冷藏开关
        mControlEntries.add(controlEntryList.get(8));//4 冷藏档位模式
        mControlEntries.add(controlEntryList.get(9));//5 冷冻档位模式
        mControlEntries.add(controlEntryList.get(10));//6 变温档位模式
        mControlEntries.add(controlEntryList.get(13));//8 人感
        MyLogUtil.i(TAG, "initControlEntries out");
    }

    private void initTempStatusEntries() {
        mShowTempEntryList = new ArrayList<FridgeStatusEntry>();
        mShowTempEntryList.add(new FridgeStatusEntry(fridgeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(fridgeShowTemp.name())));
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeShowTemp.name())));
        mShowTempEntryList.add(new FridgeStatusEntry(EnumBaseName.changeShowTemp.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeShowTemp.name())));
    }
    private void initErrorStatusEntries() {
        mErrorEntryList = new ArrayList<FridgeStatusEntry>();

        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.envTempSensorErr.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envTempSensorErr.name())));//环境温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeSensorErr.name())));//冷藏温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeDefrostSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeDefrostSensorErr.name())));//冷藏化霜传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeSensorErr.name())));//冷冻温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.changeSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeSensorErr.name())));//变温温度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.changeDefrostSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeDefrostSensorErr.name())));//变温化霜传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeDefrostSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeDefrostSensorErr.name())));//冷冻化霜传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeFanErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeFanErr.name())));//冷冻风机故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.coldFanErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.coldFanErr.name())));//冷却风机故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeDefrostErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeDefrostErr.name())));//冷冻化霜故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.envHumSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.envHumSensorErr.name())));//环境湿度传感器故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeDefrostErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeDefrostErr.name())));//冷藏化霜故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.pirErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.pirErr.name())));//人感故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.alsErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.alsErr.name())));//光感故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeFanErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeFanErr.name())));//冷藏风机故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.changeDefrostErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeDefrostErr.name())));//变温化霜故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.changeFanErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeFanErr.name())));//变温风机故障
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.dryAreaSensorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.dryAreaSensorErr.name())));//干区传感器故障

        //以下不是从主控板获取
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.communicationErr.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.communicationErr.name())));//通信错误
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.communicationOverTime.name(),getMainBoardInfo().searchStatusValueBoard(EnumBaseName.communicationOverTime.name())));//通信超时
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeDoorErr.name())));//冷藏左门报警
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.fridgeRightDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.fridgeRightDoorErr.name())));//冷藏右门报警
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.freezeDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.freezeDoorErr.name())));//冷冻门报警
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.changeDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.changeDoorErr.name())));//变温门报警
        mErrorEntryList.add(new FridgeStatusEntry(EnumBaseName.insideDoorErr.name(), getMainBoardInfo().searchStatusValueBoard(EnumBaseName.insideDoorErr.name())));//门中门报警

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
            //如果冷藏关闭，设置open
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            if(fridgeCloseEntry.value == 0) {
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
            //恢复冷藏关闭可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
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
            //如果冷藏关闭，设置open
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            if(fridgeCloseEntry.value == 0) {
                fridgeCloseEntry.value = 1;
            }
            //设置冷藏开关不可调节
            fridgeCloseEntry.disable = ConstantUtil.CLOD_ON_SET_TEMPER_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);

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
            //设置冷藏档位enable,档位值显示
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);

            //恢复冷藏关闭可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
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
    public void setCustomArea(int customTemper) {
        FridgeControlEntry changeLevelEntry = getControlEntryByName(EnumBaseName.changeTargetTemp);
        changeLevelEntry.value = customTemper;
        updateControlByEntry(changeLevelEntry);
        getControlDbMgr().updateValue(changeLevelEntry);
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
            //检查速冷模式
            FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
            if (coldEntry.value == 1) {
                mService.stopColdOnTime();
                //设置off
                setControlValueByName(EnumBaseName.quickColdMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.quickColdMode, 0);
            }

            //设置冷藏开关不可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            //如果冷藏关闭，设置open
            if(fridgeCloseEntry.value == 0) {
                fridgeCloseEntry.value = 1;
            }
            fridgeCloseEntry.disable = ConstantUtil.HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);

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

            //恢复冷藏关闭可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);

            //恢复冷藏档位设置
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            MyLogUtil.d(TAG,"holidayOff modedebug fridgeTargetTemp disable="+getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);

        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }
    @Override
    public void refrigeratorOpen(){
        FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
        if(fridgeCloseEntry.value == 0) {
            //冷藏开
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateValue(fridgeCloseEntry);
            MyLogUtil.d(TAG,"refrigeratorOpen modedebug fridgeSwitch disable="+getControlEntryByName(EnumBaseName.fridgeSwitch).disable);
            //冷藏档位可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            MyLogUtil.d(TAG,"refrigeratorOpen modedebug fridgeTargetTemp disable="+getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();

    }

    @Override
    public void refrigeratorClose(){
        FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
        if(fridgeCloseEntry.value == 1) {
            fridgeCloseEntry.value = 0;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateValue(fridgeCloseEntry);
            MyLogUtil.d(TAG,"refrigeratorClose modedebug fridgeSwitch disable="+getControlEntryByName(EnumBaseName.fridgeSwitch).disable);
            //冷藏档位不可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING);
            MyLogUtil.d(TAG,"refrigeratorClose modedebug fridgeTargetTemp disable="+getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void pirSwitchOn() {
        FridgeControlEntry pirEntry = getControlEntryByName(EnumBaseName.pirSwitch);
        if(pirEntry.value == 0) {
            pirEntry.value = 1;
            pirEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(pirEntry);
            getControlDbMgr().updateEntry(pirEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }
    @Override
    public void pirSwitchOff() {
        FridgeControlEntry pirEntry = getControlEntryByName(EnumBaseName.pirSwitch);
        if(pirEntry.value == 1) {
            pirEntry.value = 0;
            pirEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(pirEntry);
            getControlDbMgr().updateEntry(pirEntry);
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

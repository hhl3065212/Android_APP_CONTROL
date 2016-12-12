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
 * Created by tingting on 2016/11/2.
 */

public class TwoFiveOneModel extends ModelBase {
    private static final String TAG = "TwoFiveOneModel";

    TwoFiveOneModel(ControlMainBoardService service) {
        super(service);
    }

    public void init() {
        initControlEntries();
    }

    private void initControlEntries() {
        MyLogUtil.i(TAG, "initControlEntries in");
        mControlEntries = new ArrayList<FridgeControlEntry>();
        List<FridgeControlEntry> controlEntryList = getControlDbMgr().query();
        mControlEntries.add(controlEntryList.get(0));//0 智能模式
        mControlEntries.add(controlEntryList.get(1));//1 假日模式
        mControlEntries.add(controlEntryList.get(3));//2 速冷模式
        mControlEntries.add(controlEntryList.get(4));//3 速冻模式
        mControlEntries.add(controlEntryList.get(9));//4 冷藏档位模式
        mControlEntries.add(controlEntryList.get(10));//5 冷冻档位模式
        mControlEntries.add(controlEntryList.get(11));//6 变温档位模式
        mControlEntries.add(controlEntryList.get(13));//7 冷藏开关

//                mControlEntries.add(controlEntryList.get(2));//净化模式
//                mControlEntries.add(controlEntryList.get(3));//速冷模式
//                mControlEntries.add(controlEntryList.get(4));//速冻模式
//                mControlEntries.add(controlEntryList.get(5));//珍品模式
//                mControlEntries.add(controlEntryList.get(9));//冷藏档位模式
//                mControlEntries.add(controlEntryList.get(10));//冷冻档位模式
//                mControlEntries.add(controlEntryList.get(13));//冷藏关闭
        MyLogUtil.i(TAG, "initControlEntries out");
    }

    private void initTempStatusEntries(){
        mShowTempEntryList = new ArrayList<FridgeStatusEntry>();
        mShowTempEntryList.add(new FridgeStatusEntry("fridgeShowTemp", getMainBoardInfo().getFridgeShowTemp()));
        mShowTempEntryList.add(new FridgeStatusEntry("changeShowTemp", getMainBoardInfo().getVariableShowTemp()));
        mShowTempEntryList.add(new FridgeStatusEntry("freezeShowTemp", getMainBoardInfo().getFreezeShowTemp()));
    }

   private void initErrorStatusEntries() {
       mErrorEntryList = new ArrayList<FridgeStatusEntry>();
       mErrorEntryList.add(new FridgeStatusEntry("envShowTemp", getMainBoardInfo().getEnvShowTemp()));
       mErrorEntryList.add(new FridgeStatusEntry("envShowHumidity", getMainBoardInfo().getEnvShowHumidity()));
       mErrorEntryList.add(new FridgeStatusEntry("communicationErr", getMainBoardInfo().getCommunicationErr()));
       mErrorEntryList.add(new FridgeStatusEntry("envSensorErr", getMainBoardInfo().getEnvSensorErr()));
       mErrorEntryList.add(new FridgeStatusEntry("fridgeSensorErr", getMainBoardInfo().getFridgeSensorErr()));
       mErrorEntryList.add(new FridgeStatusEntry("freezeSensorErr", getMainBoardInfo().getFreezeSensorErr()));
       mErrorEntryList.add(new FridgeStatusEntry("changeSensorErr", getMainBoardInfo().getChangeSensorErr()));
       mErrorEntryList.add(new FridgeStatusEntry("defrostSensorErr", getMainBoardInfo().getDefrostSensorErr()));
       mErrorEntryList.add(new FridgeStatusEntry("freezeDefrostErr", getMainBoardInfo().getFreezeDefrostErr()));
    }
    
    
    public void smartOn() {
        MyLogUtil.i(TAG, "smartOn in");
        FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
        if( smartEntry.value == 0) {
            //如果假日模式on,设置假日模式off，数据库同步更新此状态
            if (getControlValueByName(EnumBaseName.holidayMode) == 1) {
                //设置off
                setControlValueByName(EnumBaseName.holidayMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.holidayMode, 0);
            }
            //如果速冻模式on,发送设置速冻模式off cmd，数据库同步更新此状态
            if(getControlValueByName(EnumBaseName.quickFreezeMode) == 1) {
                setControlValueByName(EnumBaseName.quickFreezeMode, 0);
                //update database
                getControlDbMgr().updateValueByName(EnumBaseName.quickFreezeMode, 0);
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
        MyLogUtil.i(TAG, "smartOn out");
    }

    public void smartOff() {
        MyLogUtil.i(TAG, "smartOff in");
        FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
        if( smartEntry.value == 1) {
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

    public void freezeOn() {
        MyLogUtil.i(TAG, "freezeOn in");
        FridgeControlEntry freezeEntry = getControlEntryByName(EnumBaseName.quickFreezeMode);
        if (freezeEntry.value == 0) {
            //智能检查
            if(getControlValueByName(EnumBaseName.smartMode) == 1) {
                setControlValueByName(EnumBaseName.smartMode, 0);
                getControlDbMgr().updateValueByName(EnumBaseName.smartMode, 0);
                //恢复冷藏档位并设置可调节
                setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
                getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            }
            //设置速冻档位灰色
            setControlDisableByName(EnumBaseName.freezeTargetTemp,ConstantUtil.FREEZE_ON_SET_TEMPER_WARNING);
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

    public void setCold(int coldTemper){
        MyLogUtil.i(TAG,"setCold coldTemper=" + coldTemper);
        FridgeControlEntry coldLevelEntry = getControlEntryByName(EnumBaseName.fridgeTargetTemp);
        coldLevelEntry.value = coldTemper;
        setControlValueByName(EnumBaseName.fridgeTargetTemp, coldTemper);
        getControlDbMgr().updateValue(coldLevelEntry);
        mService.sendControlCmdResponse();
    }

    public void setFreeze(int freezeTemper){
        MyLogUtil.i(TAG,"setFreeze freezeTemper=" + freezeTemper);
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

            //设置冷藏档位不可调节
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            MyLogUtil.d(TAG, "holidayOn modedebug  fridgeTargetTemp disable=" + getControlEntryByName(EnumBaseName.fridgeTargetTemp).disable);

            //设置冷藏开关不可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            //如果冷藏关闭，设置open
            if(fridgeCloseEntry.value == 0) {
                fridgeCloseEntry.value = 1;
            }
            fridgeCloseEntry.disable = ConstantUtil.HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
            MyLogUtil.d(TAG,"holidayOn modedebug fridgeSwitch disable="+getControlEntryByName(EnumBaseName.fridgeSwitch).disable);

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

            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeSwitch);
            fridgeCloseEntry.value = 1;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            MyLogUtil.d(TAG,"holidayOff modedebug fridgeCloseMode disable="+getControlEntryByName(EnumBaseName.fridgeSwitch).disable);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void coldOn() {
        MyLogUtil.i(TAG, "coldOn in");
        FridgeControlEntry coldEntry = getControlEntryByName(EnumBaseName.quickColdMode);
        if(coldEntry.value == 0) {
            //进速冷
            coldEntry.value = 1;
            coldEntry.disable = ConstantUtil.NO_WARNING;
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
        if(coldEntry.value == 1) {
            //进速冷
            coldEntry.value = 0;
            coldEntry.disable = ConstantUtil.NO_WARNING;
            getControlDbMgr().updateEntry(coldEntry);
            //设置变温档位enable,档位值显示
            setControlDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.changeTargetTemp, ConstantUtil.NO_WARNING);;
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG, "coldOff out");
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
    public void setCustomArea(int customTemper) {
        MyLogUtil.i(TAG,"setCustomArea customTemper=" + customTemper);
        setControlValueByName(EnumBaseName.changeTargetTemp, customTemper);
        getControlDbMgr().updateValueByName(EnumBaseName.changeTargetTemp, customTemper);
        mService.sendControlCmdResponse();
    }

    public void handleStatusDataResponse() {
        if(mShowTempEntryList == null) {
            initTempStatusEntries();
        }
        //显示温度信息处理
        handleTemperInfoResponse();
        if(mErrorEntryList == null ) {
            initErrorStatusEntries();
        }
        // 错误故障信息处理
        handleErrorInfoResponse();
    }

    private void handleTemperInfoResponse() {
        MyLogUtil.v(TAG,"handleTemperInfoResponse in");
        Boolean isTempChanged = false;

        int fridgeShowTemp = getMainBoardInfo().getFridgeShowTemp();
        if(mShowTempEntryList.get(0).value != fridgeShowTemp) {
            mShowTempEntryList.get(0).value = fridgeShowTemp;
            isTempChanged = true;
        }

        int variableTemp = getMainBoardInfo().getVariableShowTemp();
        if(mShowTempEntryList.get(1).value != variableTemp) {
            mShowTempEntryList.get(1).value = variableTemp;
            isTempChanged = true;
        }

        int freezeTemp = getMainBoardInfo().getFreezeShowTemp();
        if(mShowTempEntryList.get(2).value != freezeTemp) {
            mShowTempEntryList.get(2).value = freezeTemp;
            isTempChanged = true;
        }

        if(isTempChanged) {
            mService.notifyTemperChanged(mShowTempEntryList);
        }
        MyLogUtil.v(TAG,"handleTemperInfoResponse out");
    }

    private void handleErrorInfoResponse() {
        Boolean isErrOccurred = false;

        int envShowTemp = getMainBoardInfo().getEnvShowTemp();
        if(mErrorEntryList.get(0).value != envShowTemp) {
            mErrorEntryList.get(0).value = envShowTemp;
            isErrOccurred = true;
        }

        int envShowHumidity = getMainBoardInfo().getEnvShowHumidity();
        if(mErrorEntryList.get(1).value != envShowHumidity) {
            mErrorEntryList.get(1).value = envShowHumidity;
            isErrOccurred = true;
        }

        int communicationErr = getMainBoardInfo().getCommunicationErr();
        if(mErrorEntryList.get(2).value != communicationErr) {
            mErrorEntryList.get(2).value = communicationErr;
            isErrOccurred = true;
        }

        int envTempSensorErr = getMainBoardInfo().getEnvSensorErr();
        if(mErrorEntryList.get(3).value != envTempSensorErr) {
            mErrorEntryList.get(3).value = envTempSensorErr;
            isErrOccurred = true;
        }

        int fridgeShowTempSensorErr = getMainBoardInfo().getFridgeSensorErr();
        if(mErrorEntryList.get(4).value != fridgeShowTempSensorErr) {
            mErrorEntryList.get(4).value = fridgeShowTempSensorErr;
            isErrOccurred = true;
        }

        int freezeTempSensorErr = getMainBoardInfo().getFreezeSensorErr();
        if(mErrorEntryList.get(5).value != freezeTempSensorErr) {
            mErrorEntryList.get(5).value = freezeTempSensorErr;
            isErrOccurred = true;
        }

        int variableTempSensorErr = getMainBoardInfo().getChangeSensorErr();
        if(mErrorEntryList.get(6).value != variableTempSensorErr) {
            mErrorEntryList.get(6).value = variableTempSensorErr;
            isErrOccurred = true;
        }

        int defrostingSensorErr = getMainBoardInfo().getDefrostSensorErr();
        if(mErrorEntryList.get(7).value != defrostingSensorErr) {
            mErrorEntryList.get(7).value = defrostingSensorErr;
            isErrOccurred = true;
        }

        int freezerDefrostingSensorErr = getMainBoardInfo().getFreezeDefrostErr();
        if(mErrorEntryList.get(8).value != freezerDefrostingSensorErr) {
            mErrorEntryList.get(8).value = freezerDefrostingSensorErr;
            isErrOccurred = true;
        }

        if(isErrOccurred) {
            mService.notifyErrorOccurred(mErrorEntryList);
        }
    }


} //End of class

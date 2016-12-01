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
    
    
    public void smartOn() {
        MyLogUtil.i(TAG, "smartOn in");
        FridgeControlEntry smartEntry = getControlEntryByName(EnumBaseName.smartMode);
        if( smartEntry.value == 1) {
            //nothing to do
        } else {
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

            //如果冷藏关闭close，设置open
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeCloseMode);
            if(fridgeCloseEntry.value == 1) {
                fridgeCloseEntry.value = 0;
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
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
            MyLogUtil.i(TAG, "smartOn sendControlCmdResponse");
        }
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
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeCloseMode);
            fridgeCloseEntry.value = 0;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
            MyLogUtil.i(TAG, "smartOff sendControlCmdResponse");
        }
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
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
        }
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
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
        }
        MyLogUtil.i(TAG, "freezeOff out");
    }

    public void setCold(int coldTemper){
        MyLogUtil.i(TAG,"setCold coldTemper=" + coldTemper);
        FridgeControlEntry coldLevelEntry = getControlEntryByName(EnumBaseName.fridgeTargetTemp);
        coldLevelEntry.value = coldTemper;
        setControlValueByName(EnumBaseName.freezeTargetTemp, coldTemper);
        getControlDbMgr().updateValue(coldLevelEntry);
        mService.sendControlCmdResponse();
    }

    public void setFreeze(int freezeTemper){
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

            //设置冷藏开关不可调节
            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeCloseMode);
            //如果冷藏关闭close，设置open
            if(fridgeCloseEntry.value == 1) {
                fridgeCloseEntry.value = 0;
            }
            fridgeCloseEntry.disable = ConstantUtil.HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING;
            updateControlByEntry(fridgeCloseEntry);
            getControlDbMgr().updateEntry(fridgeCloseEntry);

            //进假日
            holidayEntry.value = 1;
            holidayEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(holidayEntry);
            getControlDbMgr().updateEntry(holidayEntry);
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
        }
    }

    @Override
    public void holidayOff() {
        FridgeControlEntry holidayEntry = getControlEntryByName(EnumBaseName.holidayMode);
        if(holidayEntry.value == 1) {
            //退假日
            holidayEntry.value = 0;
            holidayEntry.disable = ConstantUtil.NO_WARNING;
            getControlDbMgr().updateEntry(holidayEntry);

            //恢复冷藏档位设置
            setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
            getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);

            FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeCloseMode);
            fridgeCloseEntry.value = 0;
            fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
            updateControlByEntry(fridgeCloseEntry);
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
        FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeCloseMode);
        //冷藏关
        fridgeCloseEntry.value = 1;
        fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
        getControlDbMgr().updateValue(fridgeCloseEntry);
        //冷藏档位不可调节
        setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING);
        getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.REFRIGERATOR_CLOSE_ON_SET_TEMPER_WARNING);

        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();

    }
    @Override
    public void refrigeratorClose(){
        FridgeControlEntry fridgeCloseEntry = getControlEntryByName(EnumBaseName.fridgeCloseMode);
        fridgeCloseEntry.value = 0;
        fridgeCloseEntry.disable = ConstantUtil.NO_WARNING;
        getControlDbMgr().updateValue(fridgeCloseEntry);
        //冷藏档位可调节
        setControlDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
        getControlDbMgr().updateDisableByName(EnumBaseName.fridgeTargetTemp, ConstantUtil.NO_WARNING);
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void setCustomArea(int customTemper) {
        setControlValueByName(EnumBaseName.changeTargetTemp, customTemper);
        getControlDbMgr().updateValueByName(EnumBaseName.changeTargetTemp, customTemper);
        mService.sendControlCmdResponse();
    }

    public void handleStatusDataResponse() {
        ArrayList<FridgeStatusEntry> statusEntryList = getStatusDbMgr().query();
        //显示温度信息处理
        handleTemperInfoResponse(statusEntryList);
        // 错误故障信息处理
        handleErrorInfoResponse(statusEntryList);
    }

    private void handleTemperInfoResponse(List<FridgeStatusEntry> statusEntryList) {
        MyLogUtil.v(TAG,"handleTemperInfoResponse statusEntryList.size="+statusEntryList.size());
        Boolean isTempChanged = false;
        int fridgeShowTemp = getMainBoardInfo().getFridgeShowTemp();
        if(statusEntryList.get(0).value != fridgeShowTemp) {
            statusEntryList.get(0).value = fridgeShowTemp;
            isTempChanged = true;
        }

        int variableTemp = getMainBoardInfo().getVariableShowTemp();
        if(statusEntryList.get(2).value != variableTemp) {
            statusEntryList.get(2).value = variableTemp;
            isTempChanged = true;
        }

        int freezeTemp = getMainBoardInfo().getFreezeShowTemp();
        if(statusEntryList.get(1).value != freezeTemp) {
            statusEntryList.get(1).value = freezeTemp;
            isTempChanged = true;
        }

        if(isTempChanged || !mIsUIInitDone) {
            ArrayList<FridgeStatusEntry> uploadEntryList = new ArrayList<FridgeStatusEntry>();
            uploadEntryList.add(statusEntryList.get(0));
            uploadEntryList.add(statusEntryList.get(2));
            uploadEntryList.add(statusEntryList.get(1));
            MyLogUtil.v(TAG,"handleTemperInfoResponse uploadEntryList.size="+uploadEntryList.size());
            mService.notifyTemperChanged(uploadEntryList);
        }
    }

    private void handleErrorInfoResponse(List<FridgeStatusEntry> statusEntryList) {
        Boolean isErrOccurred = false;

        int envShowTemp = getMainBoardInfo().getEnvShowTemp();
        if(statusEntryList.get(6).value != envShowTemp) {
            statusEntryList.get(6).value = envShowTemp;
            isErrOccurred = true;
        }

        int envShowHumidity = getMainBoardInfo().getEnvShowHumidity();
        if(statusEntryList.get(8).value != envShowHumidity) {
            statusEntryList.get(8).value = envShowHumidity;
            isErrOccurred = true;
        }

        int communicationErr = getMainBoardInfo().getCommunicationErr();
        if(statusEntryList.get(10).value != communicationErr) {
            statusEntryList.get(10).value = communicationErr;
            isErrOccurred = true;
        }

//        int refrigeratorDoorErr = getMainBoardInfo().getCommunicationErr();
//        if(statusEntryList.get(11).value != refrigeratorDoorErr) {
//            statusEntryList.get(11).value = refrigeratorDoorErr;
//            isErrOccurred = true;
//        }

        int envTempSensorErr = getMainBoardInfo().getEnvSensorErr();
        if(statusEntryList.get(12).value != envTempSensorErr) {
            statusEntryList.get(12).value = envTempSensorErr;
            isErrOccurred = true;
        }

        int fridgeShowTempSensorErr = getMainBoardInfo().getFridgeSensorErr();
        if(statusEntryList.get(13).value != fridgeShowTempSensorErr) {
            statusEntryList.get(13).value = fridgeShowTempSensorErr;
            isErrOccurred = true;
        }

        int freezeTempSensorErr = getMainBoardInfo().getFreezeSensorErr();
        if(statusEntryList.get(14).value != freezeTempSensorErr) {
            statusEntryList.get(14).value = freezeTempSensorErr;
            isErrOccurred = true;
        }

        int variableTempSensorErr = getMainBoardInfo().getChangeSensorErr();
        if(statusEntryList.get(15).value != variableTempSensorErr) {
            statusEntryList.get(15).value = variableTempSensorErr;
            isErrOccurred = true;
        }

        int defrostingSensorErr = getMainBoardInfo().getDefrostSensorErr();
        if(statusEntryList.get(16).value != defrostingSensorErr) {
            statusEntryList.get(16).value = defrostingSensorErr;
            isErrOccurred = true;
        }

        int freezerDefrostingSensorErr = getMainBoardInfo().getFreezeDefrostErr();
        if(statusEntryList.get(17).value != freezerDefrostingSensorErr) {
            statusEntryList.get(17).value = freezerDefrostingSensorErr;
            isErrOccurred = true;
        }

        if(isErrOccurred || !mIsUIInitDone) {
            List<FridgeStatusEntry> uploadEntryList = new ArrayList<FridgeStatusEntry>();
            uploadEntryList.add(statusEntryList.get(6));
            uploadEntryList.add(statusEntryList.get(8));
            uploadEntryList.add(statusEntryList.get(10));
            //uploadEntryList.add(statusEntryList.get(11));
            uploadEntryList.add(statusEntryList.get(12));
            uploadEntryList.add(statusEntryList.get(13));
            uploadEntryList.add(statusEntryList.get(14));
            uploadEntryList.add(statusEntryList.get(15));
            uploadEntryList.add(statusEntryList.get(16));
            uploadEntryList.add(statusEntryList.get(17));
            mService.notifyErrorOccurred(uploadEntryList);
        }
    }


} //End of class

package com.haiersmart.sfcontrol.service.model;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
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
        if( mControlEntries.get(0).value == 1) {
            //nothing to do
        } else {
            //如果假日模式on,发送设置假日模式off cmd，数据库同步更新此状态
            if (mControlEntries.get(1).value == 1) {
                //设置off
                mControlEntries.get(1).value = 0;
                //update database
                getControlDbMgr().updateValue(mControlEntries.get(1));
            }
            //如果速冻模式on,发送设置速冻模式off cmd，数据库同步更新此状态
            if (mControlEntries.get(3).value == 1) {
                mControlEntries.get(3).value = 0;
                //update database
                getControlDbMgr().updateValue(mControlEntries.get(3));
            }
            //如果冷藏关闭close，设置open
            if(mControlEntries.get(7).value == 1) {
                mControlEntries.get(7).value = 0;
            }
            //设置冷藏开关不可调节
            mControlEntries.get(7).disable = 1;
            getControlDbMgr().updateEntry(mControlEntries.get(7));

            //进智能cmd
            mControlEntries.get(0).value = 1;
            mControlEntries.get(0).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(0));
            //设置冷藏档位不可调节
            mControlEntries.get(4).disable = 1;
            getControlDbMgr().updateEntry(mControlEntries.get(4));
            //设置冷冻档位不可调节
            mControlEntries.get(5).disable = 1;
            getControlDbMgr().updateEntry(mControlEntries.get(5));
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
            MyLogUtil.i(TAG, "smartOn sendControlCmdResponse");
        }
        MyLogUtil.i(TAG, "smartOn out");
    }

    public void smartOff() {
        MyLogUtil.i(TAG, "smartOff in");
        if( mControlEntries.get(0).value == 1) {
            //退智能cmd
            mControlEntries.get(0).value = 0;
            mControlEntries.get(0).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(0));
            //恢复冷藏档位并设置可调节
            getControlDbMgr().queryByName(mControlEntries.get(4));
            mControlEntries.get(4).disable = 0;
            getControlDbMgr().updateDisable(mControlEntries.get(4));
            //恢复冷冻档位并设置可调节
            getControlDbMgr().queryByName(mControlEntries.get(5));
            mControlEntries.get(5).disable = 0;
            getControlDbMgr().updateDisable(mControlEntries.get(5));
            //恢复冷藏关闭可调节
            mControlEntries.get(7).value = 0;
            mControlEntries.get(7).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(7));
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
            MyLogUtil.i(TAG, "smartOff sendControlCmdResponse");
        }
        MyLogUtil.i(TAG, "smartOff out");
    }

    public void freezeOn() {
        MyLogUtil.i(TAG, "freezeOn in");
        if (mControlEntries.get(3).value == 0) {
            //智能检查
            if(mControlEntries.get(0).value == 1) {
                mControlEntries.get(0).value = 0;
                getControlDbMgr().updateValue(mControlEntries.get(0));
                //恢复冷藏档位并设置可调节
                getControlDbMgr().queryByName(mControlEntries.get(4));
                mControlEntries.get(4).disable = 0;
                getControlDbMgr().updateDisable(mControlEntries.get(4));
            }
            //设置速冻on
            mControlEntries.get(3).value = 1;
            mControlEntries.get(3).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(3));
            //设置速冻档位灰色
            if(mControlEntries.get(5).disable == 0) {
                mControlEntries.get(5).disable = 1;
                getControlDbMgr().updateDisable(mControlEntries.get(5));
            }
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
        }
        MyLogUtil.i(TAG, "freezeOn out");
    }

    public void freezeOff() {
        MyLogUtil.i(TAG, "freezeOff in");
        if (mControlEntries.get(3).value == 1) {
            //设置速冻off
            mControlEntries.get(3).value = 0;
            mControlEntries.get(3).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(3));
            //速冻档位enable
            if(mControlEntries.get(5).disable == 1) {
                mControlEntries.get(5).disable = 0;
                getControlDbMgr().updateDisable(mControlEntries.get(5));
            }
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
        }
        MyLogUtil.i(TAG, "freezeOff out");
    }

    public void setCold(int coldTemper){
        MyLogUtil.i(TAG,"setCold coldTemper=" + coldTemper);
        if(mControlEntries.get(0).value == 1) {
            //提示关智能再调节冷藏档位
            mService.notifyWarningToast(ConstantUtil.SMART_ON_SET_TEMPER_WARNING);
            return;
        }
        if(mControlEntries.get(1).value == 1) {
            //提示关假日再调节冷藏档位
            mService.notifyWarningToast(ConstantUtil.HOLIDAY_ON_SET_TEMPER_WARNING);
            return;
        }
        mControlEntries.get(4).value = coldTemper;
        getControlDbMgr().updateValue(mControlEntries.get(4));
        mService.sendControlCmdResponse();
    }

    public void setFreeze(int freezeTemper){
        if(mControlEntries.get(0).value == 1) {
            //提示关智能再调节冷藏档位
            mService.notifyWarningToast(ConstantUtil.SMART_ON_SET_TEMPER_WARNING);
            return;
        }
        if(mControlEntries.get(3).value == 1) {
            //提示关速冻再调节冷藏档位
            mService.notifyWarningToast(ConstantUtil.FREEZE_ON_SET_TEMPER_WARNING);
            return;
        }
        mControlEntries.get(5).value = freezeTemper;
        getControlDbMgr().updateValue(mControlEntries.get(5));
        mService.sendControlCmdResponse();
    }

    @Override
    public void holidayOn() {
        if(mControlEntries.get(1).value == 0) {
            //检查智能模式
            if(mControlEntries.get(0).value == 1) {
                //退智能
                mControlEntries.get(0).value = 0;
                mControlEntries.get(0).disable = 0;
                getControlDbMgr().updateEntry(mControlEntries.get(0));
                //冷冻档位enable并恢复上传冷冻档位值
                getControlDbMgr().queryByName(mControlEntries.get(5));
                mControlEntries.get(5).disable = 0;
                getControlDbMgr().updateDisable(mControlEntries.get(5));
            }

            //设置冷藏档位不可调节
            getControlDbMgr().queryByName(mControlEntries.get(4));
            mControlEntries.get(4).disable = 1;
            getControlDbMgr().updateDisable(mControlEntries.get(4));

            //如果冷藏关闭close，设置open
            if(mControlEntries.get(7).value == 1) {
                mControlEntries.get(7).value = 0;
            }
            //设置冷藏开关不可调节
            mControlEntries.get(7).disable = 1;
            getControlDbMgr().updateEntry(mControlEntries.get(7));

            //进假日
            mControlEntries.get(1).value = 1;
            mControlEntries.get(1).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(1));
            //广播档位和模式信息给上层
            mService.sendControlCmdResponse();
        }
    }

    @Override
    public void holidayOff() {
        if(mControlEntries.get(1).value == 1) {
            //退假日
            mControlEntries.get(1).value = 0;
            mControlEntries.get(1).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(1));
            //恢复冷藏档位设置
            getControlDbMgr().queryByName(mControlEntries.get(4));
            mControlEntries.get(4).disable = 0;
            getControlDbMgr().updateDisable(mControlEntries.get(4));

            mControlEntries.get(7).value = 0;
            mControlEntries.get(7).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(7));
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void coldOn() {
        MyLogUtil.i(TAG, "coldOn in");
        if(mControlEntries.get(2).value == 0) {
            //进速冷
            mControlEntries.get(2).value = 1;
            mControlEntries.get(2).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(2));
            //设置变温档位disable
            mControlEntries.get(6).disable = 1;
            getControlDbMgr().updateDisable(mControlEntries.get(6));
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG, "coldOn out");
    }

    @Override
    public void coldOff() {
        MyLogUtil.i(TAG, "coldOff in");
        if(mControlEntries.get(2).value == 1) {
            //进速冷
            mControlEntries.get(2).value = 0;
            mControlEntries.get(2).disable = 0;
            getControlDbMgr().updateEntry(mControlEntries.get(2));
            //设置变温档位enable,档位值显示
            mControlEntries.get(6).disable = 0;
            getControlDbMgr().updateDisable(mControlEntries.get(6));
            getControlDbMgr().queryByName(mControlEntries.get(6));
        }
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
        MyLogUtil.i(TAG, "coldOff out");
    }
    @Override
    public void refrigeratorOpen(){
        if(mControlEntries.get(0).value == 1){
            //智能打开，请先关智能再关闭冷藏 toast
            mService.notifyWarningToast(ConstantUtil.SMART_ON_REFRIGERATOR_CLOSE_WARNING);
            return;
        }
        if(mControlEntries.get(1).value == 1) {
            //假日开启，请先关闭假日再关闭冷藏toast
            mService.notifyWarningToast(ConstantUtil.HOLIDAY_ON_REFRIGERATOR_CLOSE_WARNING);
            return;
        }
        //冷藏关
        mControlEntries.get(7).value = 1;
        mControlEntries.get(7).disable = 0;
        getControlDbMgr().updateValue(mControlEntries.get(7));
        //冷藏档位恢复
        getControlDbMgr().queryByName(mControlEntries.get(4));
        mControlEntries.get(4).disable = 1;
        getControlDbMgr().updateDisable(mControlEntries.get(4));
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();

    }
    @Override
    public void refrigeratorClose(){

        mControlEntries.get(7).value = 0;
        mControlEntries.get(7).disable = 0;
        getControlDbMgr().updateValue(mControlEntries.get(7));
        //冷藏档位不可调节
        mControlEntries.get(4).disable = 0;
        getControlDbMgr().updateDisable(mControlEntries.get(4));
        //广播档位和模式信息给上层
        mService.sendControlCmdResponse();
    }

    @Override
    public void setCustomArea(int customTemper) {
        if(mControlEntries.get(2).value == 1) {
            //提示关速冷再调节冷藏档位
            mService.notifyWarningToast(ConstantUtil.CLOD_ON_SET_TEMPER_WARNING);
            return;
        }
        mControlEntries.get(6).value = customTemper;
        getControlDbMgr().updateValue(mControlEntries.get(6));
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
        //MyLogUtil.i(TAG,"handleTemperInfoResponse statusEntryList.size="+statusEntryList.size());
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
            //MyLogUtil.i(TAG,"handleTemperInfoResponse uploadEntryList.size="+uploadEntryList.size());
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

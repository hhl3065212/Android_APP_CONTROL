package com.haiersmart.sfcontrol.service.powerctl;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.service.configtable.TargetTempRange;
import com.haiersmart.sfcontrol.service.mbmodel.MainBoardFourSevenSix;
import com.haiersmart.sfcontrol.service.mbmodel.MainBoardBase;
import com.haiersmart.sfcontrol.service.mbmodel.MainBoardEntry;
import com.haiersmart.sfcontrol.service.mbmodel.MainBoardInfo;
import com.haiersmart.sfcontrol.service.mbmodel.MainBoardTwoFiveOne;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.PrintUtil;

import java.util.ArrayList;

/**
 * Created by Holy.Han on 2016/9/27 10:21
 * email hanholy1210@163.com
 */
public class SerialData {
    private final String TAG = "SerialData";
    private static final int MAX_BUFF_LEN = 110;
    private byte[] ReceiveBuff;
    private int DataLen;
    private MainBoardInfo mMainBoardInfo;
    private MainBoardBase mMainBoard;
    private String mCurrentModel;
    private boolean isSerialDataReady = false;

    private static SerialData instance;

    public static synchronized SerialData getInstance() {
        if (instance == null) {
            synchronized (SerialData.class) {
                if (instance == null)
                    instance = new SerialData();
            }
        }
        return instance;
    }

    private SerialData() {
        mMainBoardInfo = new MainBoardInfo();
        ReceiveBuff = new byte[MAX_BUFF_LEN];
        // MyLogUtil.i(TAG,"SerialData constructor ReceiveBuff.length="+ReceiveBuff.length );
        DataLen = 0;
    }

    public String getCurrentModel() {
        return mCurrentModel;
    }

    public void copyByte(byte[] data, int len) {
        int i;
        DataLen = len;
        if (DataLen > MAX_BUFF_LEN) {
            MyLogUtil.i(TAG, "ReceiveBuff.length over " + MAX_BUFF_LEN + ",is " + DataLen);
            DataLen = MAX_BUFF_LEN;
        }
        for (i = 0; i < len; i++) {
            ReceiveBuff[i] = data[i];
        }
        ReceiveBuff[i] = 0;
        MyLogUtil.i(TAG, "ReceiveBuff:" + PrintUtil.BytesToString(ReceiveBuff, 16,DataLen));
    }

    public byte[] getFrameData(){
        byte[] res = new byte[DataLen];
        for (int i=0;i<DataLen;i++){
            res[i] = ReceiveBuff[i];
        }
        return res;
    }

    private Boolean isFrameHeader() {
        Boolean res = false;
        if ((ReceiveBuff[0] & 0xff) == 0xAA) {
            if ((ReceiveBuff[1] & 0xff) == 0x55) {
                res = true;
            }
        }
        return res;
    }

    private boolean isFrameLenth() {
        boolean res = false;
        if (ReceiveBuff[2] == (DataLen - 3)) {
            res = true;
        }
        return res;

    }

    private boolean isSumCheck() {
        boolean res = false;
        int len = DataLen - 1;
        byte mSum = 0;
        for (int i = 2; i < len; i++) {
            mSum += ReceiveBuff[i];
        }
        if (mSum == ReceiveBuff[len]) {
            res = true;
        }
        return res;

    }

    public boolean isDataCheck() {
        boolean res = false;
        if (isFrameHeader() && isFrameLenth() && isSumCheck()) {
            res = true;
        }
        return res;

    }

    private byte[] getReceiveBuff() {
        return ReceiveBuff;
    }

    private void updateBoardInfo(String fridgeId) {
        mMainBoardInfo.updateBoardInfo(fridgeId);
    }

    private void updateBoardInfo(byte[] dataFrame) {
        mMainBoardInfo.updateBoardInfo(dataFrame);
    }

    private String getFridgeId() {
        return mMainBoardInfo.getFridgeId();
    }

    // TODO: 2016/11/14 增加冰箱型号此处要增加对应

    /**
     * 创建主控板模型
     */
    public void createMainBoard() {
        if (getFridgeId().equals(ConstantUtil.BCD251_SN)) {
            MyLogUtil.i(TAG,"fridge mode:251");
            mCurrentModel = ConstantUtil.BCD251_MODEL;
            mMainBoard = new MainBoardTwoFiveOne();
        } else if (getFridgeId().equals(ConstantUtil.BCD476_SN)) {
            MyLogUtil.i(TAG,"fridge mode:476");
            mCurrentModel = ConstantUtil.BCD476_MODEL;
            mMainBoard = new MainBoardFourSevenSix();
        }else {
            MyLogUtil.i(TAG,"fridge mode:default 251");
            mCurrentModel = ConstantUtil.BCD251_MODEL;
            mMainBoard = new MainBoardTwoFiveOne();
        }
        mMainBoard.init();
        isSerialDataReady = true;
    }

    /**
     * 获取主控板控制类
     *
     * @return
     */
    public ArrayList<MainBoardEntry> getMainBoardControl() {
        ArrayList<MainBoardEntry> res = null;
        if(mMainBoard != null) {
            res = mMainBoard.getMainBoardControl();
        }
            return res;

    }

    /**
     * 通过名字获取控制类值
     * @param name
     * @return
     */
    public int getMbcValueByName(String name){
        return mMainBoard.getMainBoardControlByName(name);
    }

    /**
     * 获取主控板状态类
     *
     * @return
     */
    public ArrayList<MainBoardEntry> getMainBoardStatus() {
        ArrayList<MainBoardEntry> res = null;
        if(mMainBoard != null) {
            res = mMainBoard.getMainBoardStatus();
        }
            return res;
    }

    /**
     * 通过名字获取状态类值
     * @param name
     * @return
     */
    public int getMbsValueByName(String name){
        return mMainBoard.getMainBoardStatusByName(name);
    }

    /**
     * 获取主控板调试类
     *
     * @return
     */
    public ArrayList<MainBoardEntry> getMainBoardDebug() {
        ArrayList<MainBoardEntry> res = null;
        if(mMainBoard != null) {
            res = mMainBoard.getMainBoardDebug();
        }
            return res;
    }
    /**
     * 通过名字获取调试类值
     *
     * @return
     */
    public int getMbdValueByName(String name){
        return mMainBoard.getMainBoardDebugByName(name);
    }

    /**
     * 获取主板信息
     *
     * @return
     */
    public MainBoardInfo getMainBoardInfo() {
        return mMainBoardInfo;
    }

    /**
     * 获得档位温度范围
     *
     * @return TargetTempRange
     */
    public TargetTempRange getTargetTempRange() {
        TargetTempRange res = null;
        if(mMainBoard != null) {
            res = mMainBoard.getTargetTempRange();
        }
        return res;

    }

    /**
     * 数据解析
     */
    public void ProcData() {
        switch (ReceiveBuff[3]) {
            case (byte) 0x02: //用户状态帧
                if(mMainBoard != null) {
                    mMainBoard.updataMainBoardParameters(ReceiveBuff);
                    mMainBoard.handleDoorEvents();
                }
                MyLogUtil.i(TAG, "ProcData status BROADCAST_ACTION_STATUS_BACK");
                //TODO: write observer mode by self, consider application context maybe make OOM
                ControlApplication.getInstance().sendBroadcastToService(ConstantUtil.BROADCAST_ACTION_STATUS_BACK);
                break;
            case (byte) 0x03: //无效帧
                if(mMainBoard != null) {
                    mMainBoard.processInVainMessage(ReceiveBuff);
                }
                break;
            case (byte) 0x71: //设备识别码查询应答帧
                updateBoardInfo(ReceiveBuff);
                createMainBoard();
                MyLogUtil.i(TAG, "ProcData query BROADCAST_ACTION_QUERY_BACK");
                //TODO: write observer mode by self, consider application context maybe make OOM
                ControlApplication.getInstance().sendBroadcastToService(ConstantUtil.BROADCAST_ACTION_QUERY_BACK);
                break;
            case (byte) 0xff: //设备调试信息
                if(mMainBoard != null) {
                    mMainBoard.updateMainBoardDebugMessage(ReceiveBuff);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 模式与档位同步,主控板与用户行为保持一致
     *
     * @return
     */
    public ArrayList<byte[]> packSyncMode() {
        ArrayList<byte[]> res = null;
        if(mMainBoard != null) {
            res = mMainBoard.packSyncMode();
        }
        return res;
    }

    public void setCommunicationOverTime(boolean b){
        if(mMainBoard != null) {
            mMainBoard.setCommunicationOverTime(b);
        }
        MyLogUtil.i(TAG,"CommunicationOverTime is "+b);
    }

    public void setCommunicationErr(int value){
        if(mMainBoard != null) {
            mMainBoard.setCommunicationErr(value);
        }
        MyLogUtil.i(TAG,"CommunicationErr counts is "+value);
    }

    public boolean isSerialDataReady() {
        return isSerialDataReady;
    }
}

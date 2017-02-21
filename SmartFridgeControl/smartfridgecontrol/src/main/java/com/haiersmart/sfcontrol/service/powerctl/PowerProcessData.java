/**
 * Copyright  2015,  Smart  Haier
 * All  rights  reserved.
 * Description:
 * Author:  shilin
 * Date:  15-12-3
 * FileName:  shilin.java
 * History:
 * 1.  Date:15-12-3 上午11:51
 * Author:shilin
 * Modification:
 */
package com.haiersmart.sfcontrol.service.powerctl;

import com.haiersmart.sfcontrol.constant.EnumBaseName;

import java.io.IOException;


public class PowerProcessData {
    private final String TAG = "PowerProcessData";
    private PowerSerialOpt mPowerSerialOpt;
    private SerialData mSerialData;
    private boolean TimerThreadSwitch = true;
    private final int mSleepTime = 500;
    private final int mQueryTicks = 1000;//定时查询间隔 ms 需小于mSyncTicks mSleepTime倍数
    private final int mSyncTicks = 5000;//定时同步间隔 ms 需大于mQueryTicks mSleepTime倍数
    private TimerThread mTimerThread;
    private final int mQueryTimes = mQueryTicks/500;
    private final int mSyncTimes= mSyncTicks/500;
    private int mTimerCounts = 0;


    private class TimerThread extends Thread {

        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (TimerThreadSwitch) {
                    if(mSerialData.isSerialDataReady()) {
                        if (mTimerCounts >= mSyncTimes) {
                            if (mPowerSerialOpt.isSendListEmpty()) {
                                sendSyncCmd();
                                mTimerCounts = 0;
                            }
                        } else {
                            if ((mTimerCounts % mQueryTimes) == 0) {
                                if (mPowerSerialOpt.isSendListEmpty()) {
                                    sendQuerryCmd();
                                }
                            }
                            mTimerCounts++;
                        }
                    }
                }
                try {
                    sleep(mSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PowerProcessData() throws IOException {
        mSerialData = SerialData.getInstance();
        mPowerSerialOpt = PowerSerialOpt.getInstance();
        sendGetIdCmd();//启动后发送查询主控板id命令
        mTimerThread = new TimerThread();
        mTimerThread.start();
    }


    public void PowerProcDataStop() {
        TimerThreadSwitch = false;
        mPowerSerialOpt.PowerSerialOptClose();
    }

    public void PowerProcDataStart() {
        mPowerSerialOpt.PowerSerialOptReOpen();
        TimerThreadSwitch = true;
    }


    private void sendGetIdCmd(){
        mPowerSerialOpt.sendCmdById(EnumBaseName.getDeviceId);
    }

    private void sendQuerryCmd(){
        mPowerSerialOpt.sendCmdById(EnumBaseName.getAllProperty);
    }
    private void sendSyncCmd(){
        mPowerSerialOpt.sendCmdArray(mSerialData.packSyncMode());
    }
    public void sendMarketCmd(int value){
        mPowerSerialOpt.sendCmdById(EnumBaseName.marketDemo,(byte)value);
    }

    public boolean isTimerThreadSwitch() {
        return TimerThreadSwitch;
    }

    public void setTimerThreadSwitch(boolean timerThreadSwitch) {
        TimerThreadSwitch = timerThreadSwitch;
    }

}

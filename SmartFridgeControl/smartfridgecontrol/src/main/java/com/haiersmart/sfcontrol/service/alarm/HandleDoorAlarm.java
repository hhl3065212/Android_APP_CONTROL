/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 开门计时，到时间报警，关门关闭报警,报警和停止报警发送广播
 * Author:  Holy.Han
 * Date:  2016/11/28
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service.alarm;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcontrol.constant.ConstantUtil.BROADCAST_ACTION_ALARM;
import static com.haiersmart.sfcontrol.constant.ConstantUtil.DOOR_ALARM_STATUS;

/**
 * <p>function: </p>
 * <p>description:  开门计时，到时间报警，关门关闭报警,报警和停止报警发送广播</p>
 * history:  1. 2016/11/28
 * Author: Holy.Han
 * modification: create
 */
public abstract class HandleDoorAlarm {
    protected static final String TAG = "HandleDoorAlarm";
    /** 从开门到报警时间间隔 */
    private int mOpenToAlarmTime = 0;
    /** 从报警到自动停止时间间隔 */
    private int mAlarmToStopTime = 0;
    /**
     * 门的名字 fridge、freeze、change
     */
    private String mContent;
    /** 是否门开启报警*/
    private boolean isDoorAlarming;

    private Timer timer;
    private TimerTaskStartAlarm timerTaskStartAlarm;
    private CloseAlarmTimer closeAlarmTimer;

    public HandleDoorAlarm(int nStartTime, int nStopTime, String content) {
        mOpenToAlarmTime = nStartTime;
        mAlarmToStopTime = nStopTime;
        mContent = content;
        isDoorAlarming = false;
    }

    /**
     * 开始门报警的任务，发送门报警广播，并开始关闭报警计时
     */
    class TimerTaskStartAlarm extends TimerTask {

        @Override
        public void run() {
            stopOpenDoorTimer();
            sendStartDoorAlarm();
            startCloseAlarmTimer();
        }
    }


    /**
     * 开始门开启报警计时
     */
    private void startOpenDoorTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTaskStartAlarm == null) {
            timerTaskStartAlarm = new TimerTaskStartAlarm();
        }
        if (timer != null && timerTaskStartAlarm != null) {
            timer.schedule(timerTaskStartAlarm, mOpenToAlarmTime);
            MyLogUtil.i(TAG,"TimerTaskStartAlarm is start");
        }
    }

    private void stopOpenDoorTimer() {
        if (timerTaskStartAlarm != null) {
            timerTaskStartAlarm.cancel();
            timerTaskStartAlarm = null;
        }
    }


    /**
     * 结束门报警任务，发送停止门报警广播
     */
    class  CloseAlarmTimer extends TimerTask{

        @Override
        public void run() {
            stopCloseAlarmTimer();
            sendStopDoorAlarm();
        }
    }

    /**
     * 开始门报警停止计时
     */
    private void startCloseAlarmTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (closeAlarmTimer == null) {
            closeAlarmTimer = new CloseAlarmTimer();
        }
        if (timer != null && closeAlarmTimer != null) {
            timer.schedule(closeAlarmTimer, mAlarmToStopTime);
            MyLogUtil.i(TAG,"closeAlarmTimer is start");
        }
    }

    private void stopCloseAlarmTimer() {
        if (closeAlarmTimer != null) {
            closeAlarmTimer.cancel();
            closeAlarmTimer = null;
        }
    }

    /**
     * 开始门报警定时
     */
    public void startAlarmTimer(){
        startOpenDoorTimer();
    }

    /**
     * 停止门报警
     */
    public void stopAll() {
        stopOpenDoorTimer();
        stopCloseAlarmTimer();
        sendStopDoorAlarm();
    }


    private void sendStartDoorAlarm(){
        isDoorAlarming = true;
        StringBuffer stringBuffer = new StringBuffer(mContent);
        stringBuffer.append("DoorAlarmTrue");
        MyLogUtil.i(TAG, "Door alarm status is " + stringBuffer.toString());
        ControlApplication.getInstance().sendBroadcast(BROADCAST_ACTION_ALARM, DOOR_ALARM_STATUS, stringBuffer.toString());
        setDoorErr(true);
    }

    private void sendStopDoorAlarm(){
        if(isDoorAlarming) {
            isDoorAlarming = false;
            StringBuffer stringBuffer = new StringBuffer(mContent);
            stringBuffer.append("DoorAlarmFalse");
            MyLogUtil.i(TAG, "Door alarm status is " + stringBuffer.toString());
            ControlApplication.getInstance().sendBroadcast(BROADCAST_ACTION_ALARM, DOOR_ALARM_STATUS, stringBuffer.toString());
            setDoorErr(false);
        }
    }

    public abstract void setDoorErr(boolean b);


}

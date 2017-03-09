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

import android.content.Intent;
import android.os.PowerManager;

import com.alibaba.fastjson.JSON;
import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    private static HashMap<String,Integer> doorHashMap;

    private Timer timer;
    private TimerTaskStartAlarm timerTaskStartAlarm;
    private CloseAlarmTimer closeAlarmTimer;

    public HandleDoorAlarm(int nStartTime, int nStopTime, String content) {
        mOpenToAlarmTime = nStartTime;
        mAlarmToStopTime = nStopTime;
        mContent = content;
        isDoorAlarming = false;
        if(doorHashMap == null){
            doorHashMap = new HashMap<>();
        }
        doorHashMap.put(mContent,0);
    }

    /**
     * 开始门报警的任务，发送门报警广播，并开始关闭报警计时
     */
    private class TimerTaskStartAlarm extends TimerTask {

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
            MyLogUtil.i(TAG,"TimerTaskStartAlarm is stop");
        }
    }


    /**
     * 结束门报警任务，发送停止门报警广播
     */
    private class  CloseAlarmTimer extends TimerTask{

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
            MyLogUtil.i(TAG,"closeAlarmTimer is stop");
        }
    }

    /**
     * 开始门报警定时
     */
    public void startAlarmTimer(){
        //开门亮屏
        PowerManager pm = (PowerManager) ControlApplication.getInstance().getSystemService(ControlApplication.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "DoorOpen");
        wakeLock.acquire();
        wakeLock.release();
        //开启报警计时
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
        doorHashMap.remove(mContent);
        doorHashMap.put(mContent, 1);
        sendDoorAlarmBroadcast(doorHashMap);
        setDoorErr(true);
    }

    private void sendStopDoorAlarm(){
        if(isDoorAlarming) {
            isDoorAlarming = false;
            StringBuffer stringBuffer = new StringBuffer(mContent);
            stringBuffer.append("DoorAlarmFalse");
            MyLogUtil.i(TAG, "Door alarm status is " + stringBuffer.toString());
            doorHashMap.remove(mContent);
            doorHashMap.put(mContent, 0);
            sendDoorAlarmBroadcast(doorHashMap);
            setDoorErr(false);
        }
    }
    private void sendDoorAlarmBroadcast(HashMap<String, Integer> doorHashMap){
        String doorAlarm = JSON.toJSONString(doorHashMap);

        Intent intent = new Intent();
        intent.putExtra(ConstantUtil.DOOR_ALARM_STATUS,doorAlarm);
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        ControlApplication.getInstance().sendBroadcast(intent);
    }

    public abstract void setDoorErr(boolean b);


}

package com.haiersmart.sfcontrol.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.ui.DoorAlarmActivity;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

/**
 * Created by tingting on 2016/11/29.
 */

public class DoorAlarmReceiver extends BroadcastReceiver {

    private final static String TAG = "DoorAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ConstantUtil.SERVICE_NOTICE)) {
            String alarmInfo = intent.getStringExtra(ConstantUtil.DOOR_ALARM_STATUS);
            if(alarmInfo == null) {
                return;
            }
            if(alarmInfo.equals(ConstantUtil.DOOR_FRIDGE_ALARM_TURE)) {
                MyLogUtil.i(TAG, "DOOR_FRIDGE_ALARM_TURE");
                Intent i = new Intent(ControlApplication.getInstance().mContext, DoorAlarmActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ControlApplication.getInstance().mContext.startActivity(i);
            } else if(alarmInfo.equals(ConstantUtil.DOOR_FRIDGE_ALARM_FALSE)) {
                MyLogUtil.i(TAG, "DOOR_FRIDGE_ALARM_FALSE");
                ControlApplication.getInstance().exitDoorAlarmActivity();
            } else if(alarmInfo.equals(ConstantUtil.DOOR_CHANGE_ALARM_TURE)) {
                MyLogUtil.i(TAG, "DOOR_CHANGE_ALARM_TURE");
                Intent i = new Intent(ControlApplication.getInstance().mContext, DoorAlarmActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ControlApplication.getInstance().mContext.startActivity(i);
            } else if(alarmInfo.equals(ConstantUtil.DOOR_CHANGE_ALARM_FALSE)) {
                MyLogUtil.i(TAG, "DOOR_CHANGE_ALARM_FALSE");
                ControlApplication.getInstance().exitDoorAlarmActivity();
            } else if(alarmInfo.equals(ConstantUtil.DOOR_FREEZE_ALARM_TURE)) {
                MyLogUtil.i(TAG, "DOOR_FREEZE_ALARM_TURE");
                Intent i = new Intent(ControlApplication.getInstance().mContext, DoorAlarmActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ControlApplication.getInstance().mContext.startActivity(i);
            } else if(alarmInfo.equals(ConstantUtil.DOOR_FREEZE_ALARM_FALSE)) {
                MyLogUtil.i(TAG, "DOOR_FREEZE_ALARM_FALSE");
                ControlApplication.getInstance().exitDoorAlarmActivity();
            }
        }
    }
}

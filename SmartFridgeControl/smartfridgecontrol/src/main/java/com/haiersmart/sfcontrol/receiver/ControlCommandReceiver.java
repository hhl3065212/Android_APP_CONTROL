package com.haiersmart.sfcontrol.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

/**
 * Created by tingting on 2016/12/2.
 * modes and setting level command to service
 */

public class ControlCommandReceiver extends BroadcastReceiver {
    private final static String TAG = "ControlCommandReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action =intent.getAction();
        if(action.equals(ConstantUtil.COMMAND_TO_SERVICE)) {
            String contentAction = intent.getStringExtra(ConstantUtil.KEY_MODE);
            MyLogUtil.i(TAG, "contentAction="+contentAction);
            if (contentAction.equals(ConstantUtil.MODE_SMART_ON)) {
                //智能开
                sendCommandToService(context, ConstantUtil.MODE_SMART_ON);
            }else if (contentAction.equals(ConstantUtil.MODE_SMART_OFF)){
                //智能关
                sendCommandToService(context, ConstantUtil.MODE_SMART_OFF);
            }else if (contentAction.equals(ConstantUtil.MODE_HOLIDAY_ON)) {
                //假日开
                sendCommandToService(context, ConstantUtil.MODE_HOLIDAY_ON);
            }else if (contentAction.equals(ConstantUtil.MODE_HOLIDAY_OFF)) {
                //假日关
                sendCommandToService(context, ConstantUtil.MODE_HOLIDAY_OFF);
            }else if (contentAction.equals(ConstantUtil.MODE_FREEZE_ON)) {
                //速冻开
                sendCommandToService(context, ConstantUtil.MODE_FREEZE_ON);
            }else if (contentAction.equals(ConstantUtil.MODE_FREEZE_OFF)) {
                //速冻关
                sendCommandToService(context, ConstantUtil.MODE_FREEZE_OFF);
            }else if (contentAction.equals(ConstantUtil.MODE_COLD_ON)) {
                //速冷开
                sendCommandToService(context, ConstantUtil.MODE_COLD_ON);
            }else if (contentAction.equals(ConstantUtil.MODE_COLD_OFF)) {
                //速冷关
                sendCommandToService(context, ConstantUtil.MODE_COLD_OFF);
            }else if (contentAction.equals(ConstantUtil.REFRIGERATOR_OPEN)) {
                //冷藏开关开
                sendCommandToService(context, ConstantUtil.REFRIGERATOR_OPEN);
            }else if (contentAction.equals(ConstantUtil.REFRIGERATOR_CLOSE)) {
                //冷藏开关关
                sendCommandToService(context, ConstantUtil.REFRIGERATOR_CLOSE);
            }else if(contentAction.equals(ConstantUtil.TEMPER_SETCOLD)) {
                //冷藏档位温度调节
                int tempValue = intent.getIntExtra(ConstantUtil.KEY_SET_FRIDGE_LEVEL, 0);
                MyLogUtil.i(TAG, "fridge tempValue="+tempValue);
                sendTemperCmdToService(context,ConstantUtil.TEMPER_SETCOLD,ConstantUtil.KEY_SET_FRIDGE_LEVEL,tempValue);
            }else if(contentAction.equals(ConstantUtil.TEMPER_SETCUSTOMAREA)) {
                //变温档位温度调节
                int tempValue = intent.getIntExtra(ConstantUtil.KEY_SET_COLD_LEVEL, 0);
                MyLogUtil.i(TAG, "change tempValue="+tempValue);
                sendTemperCmdToService(context,ConstantUtil.TEMPER_SETCUSTOMAREA,ConstantUtil.KEY_SET_COLD_LEVEL,tempValue);
            }else if(contentAction.equals(ConstantUtil.TEMPER_SETFREEZE)) {
                //冷冻档位温度调节
                int tempValue = intent.getIntExtra(ConstantUtil.KEY_SET_FREEZE_LEVEL, 0);
                MyLogUtil.i(TAG, "freeze tempValue="+tempValue);
                sendTemperCmdToService(context,ConstantUtil.TEMPER_SETFREEZE,ConstantUtil.KEY_SET_FREEZE_LEVEL,tempValue);
            }else if(contentAction.equals(ConstantUtil.REFRIGERATOR_CLOSE)) {
                //冷藏关闭关（冷藏室风道开，可以控制冷藏室温度档位）
                sendCommandToService(context, ConstantUtil.REFRIGERATOR_CLOSE);
            }else if(contentAction.equals(ConstantUtil.REFRIGERATOR_OPEN)) {
                //冷藏关闭开（冷藏室关）
                sendCommandToService(context, ConstantUtil.REFRIGERATOR_OPEN);
            }
        }
    }

    private void sendCommandToService(Context context,String action) {
        Intent intent = new Intent(context,ControlMainBoardService.class);
        intent.setAction(action);
        MyLogUtil.v(TAG, "sendCommandToService action=" + action);
        context.startService(intent);
    }

    private void sendTemperCmdToService(Context context,String action, String key, int temper) {
        Intent intent = new Intent(context, ControlMainBoardService.class);
        intent.setAction(action);
        intent.putExtra(key, temper);
        context.startService(intent);
    }
}

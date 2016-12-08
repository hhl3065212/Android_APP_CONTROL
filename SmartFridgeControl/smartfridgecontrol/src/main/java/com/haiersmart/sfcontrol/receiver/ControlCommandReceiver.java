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
            MyLogUtil.i(TAG, "Action="+action);
            String contentAction = intent.getStringExtra(ConstantUtil.KEY_MODE);
            MyLogUtil.i(TAG, "contentAction="+contentAction);
            if((contentAction == null)||(contentAction.length() <= 0)){
                throw new IllegalArgumentException();
            }
            if (contentAction.equals(ConstantUtil.QUERY_CONTROL_READY)) {
                //查询service是否ready
                sendCommandToService(context, ConstantUtil.QUERY_CONTROL_READY);
                MyLogUtil.i(TAG, "contentAction=queryControlReady,send to service");
            } else if(contentAction.equals(ConstantUtil.QUERY_FRIDGE_INFO)) {
                //查询fridge id,type
                sendCommandToService(context, ConstantUtil.QUERY_FRIDGE_INFO);
            } else if (contentAction.equals(ConstantUtil.MODE_SMART_ON)) {
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
                MyLogUtil.i(TAG, "fridge Range tempValue="+tempValue);
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
                //冷藏关闭（冷藏室关）
                sendCommandToService(context, ConstantUtil.REFRIGERATOR_CLOSE);
            }else if(contentAction.equals(ConstantUtil.REFRIGERATOR_OPEN)) {
                //冷藏开启（冷藏室风道开，可以控制冷藏室温度档位）
                sendCommandToService(context, ConstantUtil.REFRIGERATOR_OPEN);
            }else if(contentAction.equals(ConstantUtil.QUERY_CONTROL_INFO)) {
                //查询档位模式Info
                sendCommandToService(context, ConstantUtil.QUERY_CONTROL_INFO);
            }else if(contentAction.equals(ConstantUtil.QUERY_TEMPER_INFO)) {
                //查询温度Info
                sendCommandToService(context, ConstantUtil.QUERY_TEMPER_INFO);
            }else if(contentAction.equals(ConstantUtil.QUERY_ERROR_INFO)) {
                //查询故障Info
                sendCommandToService(context, ConstantUtil.QUERY_ERROR_INFO);
            }else if(contentAction.equals(ConstantUtil.QUERY_FRIDGE_TEMP_RANGE)) {
                //查询冷藏室温度档位范围
                sendCommandToService(context, ConstantUtil.QUERY_FRIDGE_TEMP_RANGE);
            }else if(contentAction.equals(ConstantUtil.QUERY_CHANGE_TEMP_RANGE)) {
                //查询变温室温度档位范围
                sendCommandToService(context, ConstantUtil.QUERY_CHANGE_TEMP_RANGE);
            }else if(contentAction.equals(ConstantUtil.QUERY_FREEZE_TEMP_RANGE)) {
                //查询冷冻室温度档位范围
                sendCommandToService(context, ConstantUtil.QUERY_FREEZE_TEMP_RANGE);
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

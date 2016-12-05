/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 发送命令广播
 * Author:  Holy.Han
 * Date:  2016/12/5
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcdemo.model;

import android.content.Context;
import android.content.Intent;

import com.haiersmart.sfcdemo.constant.EnumBaseName;

import static com.haiersmart.sfcdemo.constant.ConstantUtil.COMMAND_TO_SERVICE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_MODE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_SMART_ON;

/**
 * <p>function: </p>
 * <p>description:  发送命令广播</p>
 * history:  1. 2016/12/5
 * Author: Holy.Han
 * modification:
 */
public class SendCommandBroadcast{
    protected final String TAG = "SendCommandBroadcast";
    private Context mContext;

    public SendCommandBroadcast(Context context) {
        mContext = context;
    }
    public void sendCommand(EnumBaseName cmd,int value){
        Intent intent = new Intent();
        intent.setAction(COMMAND_TO_SERVICE);
        switch (cmd){
            case smartMode:
                if(value == 1){
                    intent.putExtra(KEY_MODE,MODE_SMART_ON);
                }
                break;
            case holidayMode:
                break;
            case quickColdMode:
                break;
            case quickFreezeMode:
                break;
            case fridgeCloseMode:
                break;
            case fridgeTargetTemp:
                break;
            case freezeTargetTemp:
                break;
            case changeTargetTemp:
                break;
            case getDeviceId:
                break;
        }
    }
}

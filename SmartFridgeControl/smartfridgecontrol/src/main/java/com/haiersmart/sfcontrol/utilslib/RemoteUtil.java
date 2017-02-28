/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 给远程控制发送状态码
 * Author:  Holy.Han
 * Date:  2017/2/24
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.utilslib;

import android.content.Intent;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantWifiUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.DBOperation;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.service.MainBoardParameters;

import java.util.HashMap;

/**
 * <p>function: </p>
 * <p>description:  给远程控制发送状态码</p>
 * history:  1. 2017/2/24
 * Author: Holy.Han
 * modification:
 */
public class RemoteUtil {
    protected final String TAG = "RemoteUtil";

    public static void sendQuery() {
        MainBoardParameters mMBParams = MainBoardParameters.getInstance();
        Intent intent = new Intent();
        intent.setAction(ConstantWifiUtil.ACTION_CONTROL);
        intent.putExtra(ConstantWifiUtil.KEY_GETSTATE, getQueryResult());
        byte[] bytes = mMBParams.getDataBaseToBytes();
        if (bytes != null) {
            intent.putExtra(ConstantWifiUtil.KEY_GETBYTES, bytes);
        }
        MyLogUtil.i("printSerialString", PrintUtil.BytesToString(bytes, 16));
        intent.putExtra(ConstantWifiUtil.KEY_TYPE_ID, mMBParams.getTypeId());
        MyLogUtil.i("printSerialString", mMBParams.getFridgeId());
        ControlApplication.getInstance().mContext.sendBroadcast(intent);
        MyLogUtil.i("send query");
    }

    private static HashMap getQueryResult() {
        DBOperation mDBHandle = DBOperation.getInstance();
        HashMap<String, String> stateList = new HashMap<>();
        FridgeControlEntry fridgeControlEntry = new FridgeControlEntry(EnumBaseName.SterilizeMode.toString());
        mDBHandle.getControlDbMgr().queryByName(fridgeControlEntry);
        int valueSterilization = fridgeControlEntry.value;
        stateList.put(ConstantWifiUtil.QUERY_GOOD_FOOD, "65278");
        stateList.put(ConstantWifiUtil.QUERY_UV, "" + valueSterilization);
        return stateList;
    }
}

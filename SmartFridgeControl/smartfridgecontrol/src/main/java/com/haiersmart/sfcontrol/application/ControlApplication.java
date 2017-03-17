package com.haiersmart.sfcontrol.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

/**
 * Created by tingting on 2016/9/28.
 */
public class ControlApplication extends Application {
    public static Context mContext;
    private static ControlApplication mInstance = null;

    private final static String TAG = "ControlApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
        MyLogUtil.on(ConstantUtil.DEBUG);
        MyLogUtil.setLogEnable(true);
        MyLogUtil.i("ControlApplication", "onCreate mContext get ApplicationContext");
        Intent intent = new Intent(this,ControlMainBoardService.class);
        mContext.startService(intent);
    }


    public static ControlApplication getInstance() {
        if (mInstance == null) {
            mInstance = new ControlApplication();
        }
        return mInstance;
    }

    public void sendBroadcastToService(String action) {
        Intent intent = new Intent(mContext, ControlMainBoardService.class);
        intent.setAction(action);
        mContext.startService(intent);
    }
    public void sendBroadcast(String action,String put,String content){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(put,content);
        mContext.sendBroadcast(intent);
    }


}

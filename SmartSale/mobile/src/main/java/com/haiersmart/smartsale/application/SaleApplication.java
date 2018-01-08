/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2017/12/21
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.smartsale.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.ArrayMap;
import android.util.Log;
import android.view.WindowManager;

import com.haiersmart.library.MediaPlayer.PlayFixedVoice;
import com.haiersmart.library.Utils.ConvertData;
import com.haiersmart.rfidlibrary.service.RFIDService;
import com.haiersmart.smartsale.constant.ConstantUtil;
import com.haiersmart.smartsale.function.RFIDEventMgr;
import com.haiersmart.smartsale.module.Smartlock;
import com.haiersmart.smartsale.service.HttpService;

import org.json.JSONException;

import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/12/21
 * Author: Holy.Han
 * modification:
 */
public class SaleApplication extends Application {
    protected final String TAG = "SaleApplication";
    private static SaleApplication sInstance = null;
    public static Context mContext;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    private WindowManager wm;
    private static String mMac;
    private static List<Map<String, String>> macList = new ArrayList<>();
    private RFIDEventMgr mRFIDMgr;

    BroadcastReceiver mReceiverHttp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "receiver brodacast action = " + action);
            if(action.equals(ConstantUtil.HTTP_BROADCAST)) {
                PlayFixedVoice.playVoice(PlayFixedVoice.UNLOCK);
                unlockSmartlock();
            }else if (action.equals(ConstantUtil.DOOR_STATE_BROADCAST)){
                String door = intent.getStringExtra(ConstantUtil.DOOR_STATE);
                if(door.equals("open")){
                    PlayFixedVoice.playVoice(PlayFixedVoice.OPEN);
                }else if(door.equals("close")){
                    PlayFixedVoice.playVoice(PlayFixedVoice.CLOSE);
                }
            }
        }
    };

    private void unlockSmartlock() {
        boolean ret;
        OutputStream outputStream;
        byte[] openCmd = {'1', '\0'};
        Smartlock smartlock = Smartlock.getInstance();
        ret = smartlock.openSmartLock("/dev/smartlock");
        if (!ret)
            return;
        outputStream = smartlock.getOutputStream();
        if (outputStream == null)
            return;
        try {
            outputStream.write(openCmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SaleApplication get() {
        if (sInstance == null) {
            sInstance = new SaleApplication();
        }
        return sInstance;
    }

    public WindowManager.LayoutParams getWmParams() {
        return wmParams;
    }

    public WindowManager getWindowManager() {
        if (wm == null) {
            wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        return wm;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mContext = getApplicationContext();
        getMac();
        Log.i(TAG, "Application onCreate");
        startService(new Intent(mContext, HttpService.class));
//        startService(new Intent(mContext, SmartlockService.class));
        startService(new Intent(mContext,RFIDService.class));
        mRFIDMgr = new RFIDEventMgr(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantUtil.HTTP_BROADCAST);
        filter.addAction(ConstantUtil.DOOR_STATE_BROADCAST);
        registerReceiver(mReceiverHttp, filter);

        registerReceiver(mReceiverRfid, new IntentFilter(ConstantUtil.RFID_BROADCAST));
    }

    private void getMac() {
        try {
            List<NetworkInterface> netList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface net : netList) {
                byte[] buf = net.getHardwareAddress();
                String name = net.getDisplayName();
                if (buf != null && buf.length > 0) {
                    Map<String, String> map = new ArrayMap<>();
                    map.put("name", name);
                    map.put("mac", ConvertData.bytesToString(buf, 16, ""));
                    macList.add(map);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (macList.size() > 0) {
            Log.i(TAG,macList.toString());
            for(Map<String, String> map:macList){
                if (map.get("name").equals("wlan0")){
                    mMac = map.get("mac");
                    Log.i(TAG,"mac="+mMac);
                }
            }

        }
    }

    public String getmMac() {
        return mMac;
    }

    public List<Map<String, String>> getMacList() {
        return macList;
    }

    BroadcastReceiver mReceiverRfid = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "receiver brodacast action = " + action);
            if(action.equals(intent.getStringExtra(ConstantUtil.RFID_BROADCAST) ))
                try {
                    mRFIDMgr.upload2Network("rfidJson");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    };
}

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
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
import android.view.WindowManager;

import com.haiersmart.library.Utils.ConvertData;
import com.haiersmart.smartsale.service.HttpService;

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
public class SaleApplication extends Application{
    protected final String TAG = "SaleApplication";
    private static SaleApplication sInstance = null;
    public static Context mContext;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    private WindowManager wm;
    private static String mMac;
    private static List<Map<String,String>> macList = new ArrayList<>();


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
        Log.i(TAG,"Application onCreate");
        Log.i(TAG,macList.toString());
        startService(new Intent(mContext,HttpService.class));
    }

    private void getMac(){
        try {
            List<NetworkInterface> netList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface net:netList){
                byte[] buf = net.getHardwareAddress();
                String name = net.getDisplayName();
                if(buf != null && buf.length>0) {
                    Map<String,String> map = new ArrayMap<>();
                    map.put("name",name);
                    map.put("mac",ConvertData.bytesToString(buf,16,""));
                    macList.add(map);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if(macList.size()>0){
            mMac = macList.get(0).get("mac");
        }
    }

    public String getmMac() {
        return mMac;
    }

    public List<Map<String, String>> getMacList() {
        return macList;
    }
}

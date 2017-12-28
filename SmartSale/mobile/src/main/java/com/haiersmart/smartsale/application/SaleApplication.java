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
import android.util.Log;
import android.view.WindowManager;

import com.haiersmart.smartsale.service.HttpService;

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
            wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
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
        Log.i(TAG,"Application onCreate");
        startService(new Intent(mContext,HttpService.class));
    }
}

package com.haiersmart.sfcontrol.utilslib;

import android.util.Log;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class MyLogUtil {
    public static final String TAG = "control";
    public static boolean on;

    public MyLogUtil() {
    }

    public static void on(boolean debug) {
        on = debug;
    }

    public static void setLogEnable(boolean flag) {
        on = flag;
    }

    public static void i(String msg) {
        if (on) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (on) {
            Log.i(tag, msg);
        }
    }

    public static void e(String msg) {
        if (on) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (on) {
            Log.e(tag, msg);
        }
    }

    public static void d(String msg) {
        if (on) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (on) {
            Log.d(tag, msg);
        }
    }

    public static void v(String msg) {
        if (on) {
            Log.v(TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (on) {
            Log.v(tag, msg);
        }
    }
}

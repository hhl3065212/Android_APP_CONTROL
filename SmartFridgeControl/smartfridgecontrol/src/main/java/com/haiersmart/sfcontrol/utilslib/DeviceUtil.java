/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/13
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.utilslib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Context.WIFI_SERVICE;
import static com.haiersmart.sfcontrol.utilslib.SystemCmdUtil.runCMD;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/13
 * Author: Holy.Han
 * modification:
 */
public class DeviceUtil {
    protected final String TAG = "DeviceUtil";
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int STATUS_BAR_HEIGHT;

    public DeviceUtil() {
    }

    public static float getRawSize(Context ctx, int unit, float value) {
        Resources res = ctx.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

    public static int dip2px(Context ctx, float dpValue) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public static int px2dip(Context ctx, float pxValue) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static float sp2px(Context ctx, float spValue) {
        float scale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return spValue * scale;
    }

    public static float px2sp(Context ctx, float pxValue) {
        float scale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / scale;
    }

    public static int getStatusBarHeight(Activity mContext) {
        if(STATUS_BAR_HEIGHT != 0) {
            return STATUS_BAR_HEIGHT;
        } else {
            Class c = null;
            Object obj = null;
            Field field = null;
            boolean x = false;

            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                int x1 = Integer.parseInt(field.get(obj).toString());
                STATUS_BAR_HEIGHT = mContext.getResources().getDimensionPixelSize(x1);
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            return STATUS_BAR_HEIGHT;
        }
    }

    public static int getAppInnerHeight(Activity mContext) {
        return SCREEN_HEIGHT - getStatusBarHeight(mContext) - dip2px(mContext, 48.0F);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getFactory() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static float getDenstiy(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageInfo e = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return e.versionName;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            return "";
        }
    }

    public static int getVersionNumber(Context ctx) {
        try {
            PackageInfo e = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return e.versionCode;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            return 0;
        }
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo e = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return e.versionCode;
        } catch (PackageManager.NameNotFoundException var2) {
            var2.printStackTrace();
            return 0;
        }
    }

    public static String getImei(Context context) {
        TelephonyManager phonyManager = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        return getDeviceID(phonyManager);
    }

    public static String getDeviceID(TelephonyManager phonyManager) {
        String id = phonyManager.getDeviceId();
        if(id == null) {
            id = "not available";
        }

        int phoneType = phonyManager.getPhoneType();
        switch(phoneType) {
            case 0:
                return "NONE: " + id;
            case 1:
                return "GSM: IMEI=" + id;
            case 2:
                return "CDMA: MEID/ESN=" + id;
            default:
                return "UNKNOWN: ID=" + id;
        }
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getMacAddress();
    }

    @SuppressLint({"DefaultLocale"})
    public static String getMac(Context ctx) {
        WifiManager wifi = (WifiManager)ctx.getSystemService(WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        if(mac == null) {
            mac = "00:00:00:00:00";
        }

        return mac.toLowerCase();
    }
    public static String getSSID(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String ssid = info.getSSID();
        if(ssid == "0x"){
            ssid = null;
        }
        return ssid;
    }
    public static int getRSSI(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        int ssid = info.getRssi();
        return ssid;
    }
    public static int getWifiState(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(WIFI_SERVICE);
        int state = wifi.getWifiState();
        return state;
    }
    public static List getSSIDList(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(WIFI_SERVICE);
        List list = wifi.getScanResults();
        return list;
    }
    public static int getPirUG() {
        String CommandPir = "cat /sys/class/switch/irda_status/state";
        String Result = runCMD(CommandPir);
        if (Result.equalsIgnoreCase("1")) {
            return 1;
        } else {
            return 0;
        }
    }
}

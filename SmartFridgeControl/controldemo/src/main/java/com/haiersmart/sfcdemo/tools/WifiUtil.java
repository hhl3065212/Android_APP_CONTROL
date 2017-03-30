package com.haiersmart.sfcdemo.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;
import java.util.Objects;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/3/24
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class WifiUtil {

    public static String getLocalMac(Context ctx) {
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    @SuppressLint({"DefaultLocale"})
    public static String getMac(Context ctx) {
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        if(mac == null) {
            mac = "00:00:00:00:00";
        }
        return mac.toLowerCase();
    }
    public static String getSSID(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String ssid = info.getSSID();
        if(Objects.equals(ssid, "0x")){
            ssid = null;
        }
        return ssid;
    }
    public static int getRSSI(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        int ssid = info.getRssi();
        return ssid;
    }
    public static int getWifiState(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        int state = wifi.getWifiState();
        return state;
    }
    public static List getSSIDList(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        List list = wifi.getScanResults();
        return list;
    }
    public static void removeWifiNetwork(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfig = wifi.getConfiguredNetworks();
        if(wifiConfig !=null){
            for (WifiConfiguration wifiConfiguration:wifiConfig){
                Log.d("delete wifi","wificonfig:"+wifiConfiguration.networkId
                        +","+wifiConfiguration.SSID);
                wifi.removeNetwork(wifiConfiguration.networkId);
            }
            wifi.saveConfiguration();
        }
    }
    public static void saveConfiguration(Context ctx,WifiConfiguration config){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfig = wifi.getConfiguredNetworks();
    }
    public static List getConfiguration(Context ctx){
        WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfig = wifi.getConfiguredNetworks();
        return wifiConfig;
    }
}

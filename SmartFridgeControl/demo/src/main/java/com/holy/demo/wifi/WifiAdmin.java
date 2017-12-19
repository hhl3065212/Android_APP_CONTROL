package com.holy.demo.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/10/17
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class WifiAdmin {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfigList;
    WifiLock mWifiLock;

    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void checkState(Context context) {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            Toast.makeText(context, "Wifi正在关闭", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            Toast.makeText(context, "Wifi已经关闭", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            Toast.makeText(context, "Wifi正在开启", Toast.LENGTH_SHORT).show();
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            Toast.makeText(context, "Wifi已经开启", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "没有获取到WiFi状态", Toast.LENGTH_SHORT).show();
        }
    }

    public int getWifiState(){
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        // 判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("demo");
    }

    public List<WifiConfiguration> getWifiConfigList() {
        mWifiConfigList = mWifiManager.getConfiguredNetworks();
        return mWifiConfigList;
    }

    public void connectConfig(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfigList.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigList.get(index).networkId, true);
    }

    public void startScan(Context context) {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfigList = mWifiManager.getConfiguredNetworks();
        if (mWifiList == null) {
            if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                Toast.makeText(context, "当前区域没有无线网络", Toast.LENGTH_SHORT).show();
            } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                Toast.makeText(context, "WiFi正在开启，请稍后重新点击扫描", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "WiFi没有开启，无法扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void startScan(){
        mWifiManager.startScan();
    }

    public List<ScanResult> getWifiList() {
        mWifiList = mWifiManager.getScanResults();
        return mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    public List<String> getWifiConfigString(){
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < mWifiConfigList.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiConfigList.get(i)).toString());
            listString.add(stringBuilder.toString());
        }
        return listString;
    }

    public List<Map<String,String>> getSSIDList(){
        List<Map<String,String>> list = new ArrayList<>();

        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfigList = mWifiManager.getConfiguredNetworks();
        for(int i = 0; i < mWifiList.size(); i++){
            Map<String,String> map = new HashMap<>();
            map.put("ssid",mWifiList.get(i).SSID);
            map.put("bssid",mWifiList.get(i).BSSID);
            list.add(map);
        }
        return list;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public WifiInfo getmWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo;
    }

    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b =  mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public void removeWifi(int netId) {
        disconnectWifi(netId);
        mWifiManager.removeNetwork(netId);
    }
}

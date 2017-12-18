package com.holy.demo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.holy.demo.R;
import com.holy.demo.constant.DividerItemDecoration;
import com.holy.demo.constant.WifiScanResultHandleUtil;
import com.holy.demo.constant.WifiViewAdapter;
import com.holy.demo.wifi.WifiAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private WifiAdmin mWifiAdmin;
    private RecyclerView listview_wifiscan;
    private SimpleAdapter mWifiListAdapter;
    private Button wifi_switch, wifi_scan,wifi_test;


    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiAdmin = new WifiAdmin(this);

        findView();
//        mHandler.sendEmptyMessageDelayed(0x01, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifiScanResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findView() {
        listview_wifiscan = (RecyclerView) findViewById(R.id.listview_wifiscan);
        wifi_switch = (Button) findViewById(R.id.wifi_switch);
        wifi_switch.setOnClickListener(this);
        wifi_scan = (Button) findViewById(R.id.wifi_scan);
        wifi_scan.setOnClickListener(this);
        wifi_test = (Button) findViewById(R.id.wifi_test);
        wifi_test.setOnClickListener(this);
        setWifiUI();
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> scanResults = new ArrayList<>();
        List<WifiConfiguration> ListConfig = mWifiAdmin.getWifiConfigList();
        List<ScanResult> ListScan = mWifiAdmin.getWifiList();
        return scanResults;
    }

    private List<Map<String, Object>> getListSort() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> listSave = new ArrayList<>();
        List<Map<String, Object>> listNone = new ArrayList<>();
        WifiInfo wifiInfo = mWifiAdmin.getmWifiInfo();
        List<WifiConfiguration> ListConfig = mWifiAdmin.getWifiConfigList();
        List<ScanResult> ListScan = mWifiAdmin.getWifiList();
        WifiScanResultHandleUtil.sortByRSSI(ListScan);
        WifiScanResultHandleUtil.removeRepeatBySSID(ListScan);

        for (int i = 0; i < ListScan.size(); i++) {
            if (ListScan.get(i).BSSID.equals(wifiInfo.getBSSID())) {
                Map<String, Object> map = new HashMap<>();
                map.put("ssid", ListScan.get(i).SSID);
                map.put("bssid", ListScan.get(i).BSSID);
                map.put("rssi", ListScan.get(i).level);
                map.put("key", ListScan.get(i).capabilities);
                map.put("state", "已连接");
                list.add(map);
            } else {
                int j = 0;
                for (; j < ListConfig.size(); j++) {
                    if (ListScan.get(i).SSID.equals(ListConfig.get(j).SSID.substring(1, ListConfig.get(j).SSID.length() - 1))) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ssid", ListScan.get(i).SSID);
                        map.put("bssid", ListScan.get(i).BSSID);
                        map.put("rssi", ListScan.get(i).level);
                        map.put("key", ListScan.get(i).capabilities);
                        map.put("state", "已保存");
                        listSave.add(map);
                        break;
                    }

                }
                if (j >= ListConfig.size()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ssid", ListScan.get(i).SSID);
                    map.put("bssid", ListScan.get(i).BSSID);
                    map.put("rssi", ListScan.get(i).level);
                    map.put("key", ListScan.get(i).capabilities);
                    map.put("state", "");
                    listNone.add(map);
                }
            }
        }

        list.addAll(listSave);
        list.addAll(listNone);
        return list;
    }



    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(receiverWifiScanResults, filter);
    }

    private BroadcastReceiver receiverWifiScanResults = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.i(TAG,"received wifi scan results");
                listview_wifiscan = (RecyclerView) findViewById(R.id.listview_wifiscan);
                listview_wifiscan.setLayoutManager(new LinearLayoutManager(MainActivity.this,
                        LinearLayoutManager.VERTICAL,false));
                listview_wifiscan.addItemDecoration(new DividerItemDecoration(MainActivity.this,
                        LinearLayoutManager.VERTICAL));
                listview_wifiscan.setItemAnimator(new DefaultItemAnimator());
                listview_wifiscan.setAdapter(new WifiViewAdapter(MainActivity.this,getListSort()));
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                setWifiUI();
            }
        }
    };

    public void setWifiUI() {
        switch (mWifiAdmin.getWifiState()) {
            case WifiManager.WIFI_STATE_DISABLING:
                wifi_switch.setText("WIFI正在关闭");
                wifi_switch.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                wifi_switch.setText("打开WIFI");
                wifi_switch.setEnabled(true);
                wifi_scan.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                wifi_switch.setText("WIFI正在打开");
                wifi_switch.setEnabled(false);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                wifi_switch.setText("关闭WIFI");
                wifi_switch.setEnabled(true);
                wifi_scan.setEnabled(true);
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_switch:
                switch (mWifiAdmin.getWifiState()) {
                    case WifiManager.WIFI_STATE_DISABLING:
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        mWifiAdmin.openWifi();
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        mWifiAdmin.closeWifi();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.wifi_scan:
                if (mWifiAdmin.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    mWifiAdmin.startScan();
                    Log.i(TAG,"onclick start wifi scan");
                }
                break;
            case R.id.wifi_test:
                Intent intent = new Intent(MainActivity.this,TestActivity.class);
                startActivity(intent);
                break;
        }
    }


}

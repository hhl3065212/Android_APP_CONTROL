package com.holy.demo.constant;

import android.net.wifi.ScanResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/10/18
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class WifiScanResultHandleUtil {
    static public void sortByRSSI(List<ScanResult> list){
        Collections.sort(list, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult o1, ScanResult o2) {
                if (o1.level > o2.level)
                    return -1;
                else if (o1.level < o2.level)
                    return 1;
                else
                    return 0;
            }
        });
    }

    static public void removeRepeatBySSID(List<ScanResult> list){
        for (int i = 0; i < list.size(); i++) {
            String string = list.get(i).SSID;
            for (int j = i + 1; j < list.size(); j++) {
                if (string.equals(list.get(j).SSID)) {
                    list.remove(j);
                }
            }
        }
    }
}

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/8
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcdemo;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/8
 * Author: Holy.Han
 * modification:
 */
public class NetRunnable implements Runnable {
    protected final String TAG = "NetRunnable";
    private int mCounts =0;
    private int timeoutCounts = 0;
    private int requestCounts = 0;
    private long timeStamp = 0;
    private String time;
//    private String ntpHost = "asia.pool.ntp.org";
    private String ntpHost = "time.windows.com";
    private boolean isRun = false;
    private Thread mThread;

    public void start(){
        if(mThread == null){
            isRun = true;
            mThread = new Thread(this,"sntp");
            mThread.start();
        }
    }
    public void stop(){
        if(mThread != null){
            isRun = false;
            mThread.interrupt();
            mThread = null;
        }
    }

    private void changeStampToTime(){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = simpleDateFormat.format(date);
    }

    @Override
    public void run() {
        synchronized(this) {
            while (isRun) {
//                String ntpHost = "asia.pool.ntp.org";
//                ntpHost = "dns1.synet.edu.cn";
                Log.i(TAG, "get time from : " + ntpHost);
                SntpClient client = new SntpClient();
                boolean isSuccessful = client.requestTime(ntpHost, 30000);
                requestCounts++;
                if (isSuccessful) {
                    mCounts=0;
                    timeStamp = client.getNtpTime();
                    changeStampToTime();
                } else {
                    mCounts++;
                    if(mCounts>=3) {
                        mCounts=0;
                        timeoutCounts++;
                    }
                }
                Log.i(TAG, "timeoutCounts:" + timeoutCounts + " requestCounts:" + requestCounts + " timeStamp:" + timeStamp);
            }
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getTimeoutCounts() {
        return timeoutCounts;
    }

    public int getRequestCounts() {
        return requestCounts;
    }

    public String getTime() {
        return time;
    }

    public String getNtpHost() {
        return ntpHost;
    }
}

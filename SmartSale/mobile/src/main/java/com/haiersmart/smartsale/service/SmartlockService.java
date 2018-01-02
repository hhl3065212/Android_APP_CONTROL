package com.haiersmart.smartsale.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;


import com.haiersmart.smartsale.module.Smartlock;

import java.io.InputStream;

public class SmartlockService extends Service {
    private PowerManager.WakeLock mWakeLock = null;
    InputStream mInputStream;
    Smartlock mSmartlock;
    HandlerThread mHandlerThread;
    Handler mHandler;

    private class SmartlockThread extends Thread {
        @Override
        public void run() {
            super.run();

        }
    }

    public SmartlockService() {
    }

    private void openSmartlock() {
        mSmartlock = Smartlock.getInstance();
        mSmartlock.openSmartLock("/dev/smartlock");
        mInputStream = mSmartlock.getInputStream();
    }

    private void getSmartlockState() {

    }

    private void acquireWakelock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SmartlockService");
            if (mWakeLock != null) {
                mWakeLock.acquire();
            }
        }
    }

    private void releaseWakelock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakelock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret;
        ret = super.onStartCommand(intent, flags, startId);
        mHandlerThread = new HandlerThread("sendBeatThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
//        mHandler.post(new BeatTask());
        return ret;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakelock();
    }
}

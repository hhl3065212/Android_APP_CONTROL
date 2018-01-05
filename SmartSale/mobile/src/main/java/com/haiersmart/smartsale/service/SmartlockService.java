package com.haiersmart.smartsale.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import com.haiersmart.smartsale.constant.ConstantUtil;
import com.haiersmart.smartsale.module.Smartlock;

import java.io.InputStream;

public class SmartlockService extends Service {
    private static final String TAG = "SmartlockService";
    private PowerManager.WakeLock mWakeLock = null;
    InputStream mInputStream;
    Smartlock mSmartlock;

    public SmartlockService() {
    }

    private void openSmartlock() {
        mSmartlock = Smartlock.getInstance();
        mSmartlock.openSmartLock("/dev/smartlock");
        mInputStream = mSmartlock.getInputStream();
    }

    private void closeSmartlock() {
        mSmartlock = Smartlock.getInstance();
        mSmartlock.closeSmartLock();
    }

    public byte getSmartlockState() {
        Log.i(TAG,"getSmartlockState" );
        byte[] buf = new byte[2];
        int len = 0;
        try {
            if ((len = mInputStream.read(buf)) != -1) {
                return buf[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
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
        openSmartlock();
    }

    private void sendBroadcastDoorState(String state) {
        Log.i(TAG,"sendBroadcastDoorState state=" + state);
        Intent intent = new Intent(ConstantUtil.DOOR_STATE_BROADCAST);
        intent.putExtra(ConstantUtil.DOOR_STATE, state);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret;
        ret = super.onStartCommand(intent, flags, startId);
        new Thread() {
            byte cmd;
            @Override
            public void run() {
                super.run();
                while (true) {
                    cmd = getSmartlockState();
                    if (cmd == '0') {
                        sendBroadcastDoorState("open");
                        continue;
                    }
                    if (cmd == '1') {
                        sendBroadcastDoorState("close");
                        continue;
                    }

                }
            }
        }.start();
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
        closeSmartlock();
    }
}

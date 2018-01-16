package com.haiersmart.smartsale.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import com.haiersmart.smartsale.activity.JniPir;
import com.haiersmart.smartsale.constant.ConstantUtil;
import com.haiersmart.smartsale.module.Smartlock;

import java.io.InputStream;

public class SmartlockService extends Service {
    private static final String TAG = "SmartlockService";
    private static final int GPIO_B5 = 13;
    private PowerManager.WakeLock mWakeLock = null;
    InputStream mInputStream;
    Smartlock mSmartlock;
    private JniPir mPirHandler;
    private int mPirGpioNum = 0;

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
        mPirGpioNum = GPIO_B5;//227==>GPIO7_A3, 263==>GPIO8_A7
        mPirHandler = new JniPir();
        int res = mPirHandler.openGpioDev();
        Log.i(TAG,"onCreate pir openGpioDev=" + res);
        res = mPirHandler.probe(GPIO_B5, 0);
        Log.i(TAG,"onCreate pir probe=" + res);
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
                    }
                    if (cmd == '1') {
                        sendBroadcastDoorState("close");
                    }
                }
            }
        }.start();
        new Thread(runnable_pir).start();
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
        mPirHandler.releaseGpio(mPirGpioNum);
        releaseWakelock();
        closeSmartlock();
    }

    private int getPirStatus() {
        int value = mPirHandler.getGpio(mPirGpioNum);
//        Log.i(TAG, "getPirStatus get gpio value = "+value);
        int res = mPirHandler.releaseGpio(mPirGpioNum);
//        Log.i(TAG, "getPirStatus releaseGpio res = "+res);
        return value;
    }


    private void sendBroadcastPirState(int pirValue) {
        Log.i(TAG,"sendBroadcastDoorState state=" + pirValue);
        Intent intent = new Intent(ConstantUtil.PIR_STATE_BROADCAST);
        intent.putExtra(ConstantUtil.PIR_STATE, pirValue);
        sendBroadcast(intent);
    }

    private Runnable runnable_pir = new Runnable() {

        @Override
        public void run() {
            int pirValue = 0;
            boolean isFirstTime = true;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int curPir = getPirStatus();
                if(curPir >= 0) {
                    if(isFirstTime) {
                        pirValue = curPir;
                        isFirstTime = false;
                    } else  {
                        if(pirValue != curPir ) {
                            sendBroadcastPirState(curPir);
                        }
                        pirValue = curPir;
                    }
                }
            }
        }
    };

}

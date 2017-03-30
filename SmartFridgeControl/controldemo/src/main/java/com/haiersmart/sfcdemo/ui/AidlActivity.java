package com.haiersmart.sfcdemo.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haiersmart.sfcdemo.R;
import com.haiersmart.sfcdemo.constant.ConstantUtil;
import com.haiersmart.sfcdemo.constant.EnumBaseName;
import com.haiersmart.sfcdemo.constant.QrCodeUtil;
import com.haiersmart.sfcdemo.constant.TypeIdUtil;
import com.haiersmart.sfcdemo.draw.AlarmWindow;
import com.haiersmart.sfcdemo.draw.MyTestButton;
import com.haiersmart.sfcdemo.model.FridgeModel;
import com.haiersmart.sfcontrol.ControlService;
import com.haiersmart.sfcontrol.ICallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class AidlActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AidlActivity";
    private Context mContext;

    private static FridgeModel mModel = new FridgeModel();

    private Timer mTimer;
    private TimerTask mWaitTask, mTimerTask;

    private LinearLayout lineEnvTemp, lineEnvHum, lineFridgeTemp, lineFreezeTemp, lineChangeTemp,
            lineFridgeTarget, lineFreezeTarget, lineChangeTarger, lineSterilize;
    private TextView tvFridgeModel, tvStatusCode, tvEnvTemp, tvEnvHum, tvFridgeTemp, tvFreezeTemp, tvChangeTemp,
            tvFridgeTarget, tvFreezeTarget, tvChangeTarget, tvTime, tvTest;
    private Button btnReturn,btnWifi;
    private MyTestButton btnSterilizeSmart, btnSterilizeStrong, btnSterilize3, btnSterilize4, btnSterilize5,
            btnSterilize6, btnSterilize7, btnSterilize8, btnSterilize9;
    private TextView tvSterilizeRuntime, tvSterilizeInterval;
    private MyTestButton btnSmart, btnHoliday, btnQuickCold, btnQuickFreeze, btnFridgeSwitch, btnTidbit, btnPurify;
    private SeekBar skbFridge, skbFreeze, skbChange;
    private ImageView imvQrCode;
    private String statusCode = "no status code!";
    private TextView tvTitleStatusCode;

    private int countsSterilize = 0;

    private int onclickCounts = 0;
    private boolean isReady = false;
    private boolean isServiceReady = false;

    private AlarmWindow alarmWindow;

    private ControlService mService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ControlService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            startQueryType();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        mContext = getApplicationContext();

        Intent serviceIntent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.haiersmart.sfcontrol",
                "com.haiersmart.sfcontrol.service.ControlMainBoardService");
        serviceIntent.setComponent(componentName);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
//        registerBroadcast();
        findView();
        //        startQueryType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReady) {
//            startRefreshUI();
        } else {
            startQueryType();
        }
        //        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopRefreshUI();
        //        unregisterReceiver(receiveUpdateUI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopRefreshUI();
        stopSterilizeTimer();
        unbindService(connection);
    }



    private void findView() {
        lineEnvTemp = (LinearLayout) findViewById(R.id.linear_demo_env_temp);
        lineEnvHum = (LinearLayout) findViewById(R.id.linear_demo_env_hum);
        lineFridgeTemp = (LinearLayout) findViewById(R.id.linear_demo_fridge_temp);
        lineFreezeTemp = (LinearLayout) findViewById(R.id.linear_demo_freeze_temp);
        lineChangeTemp = (LinearLayout) findViewById(R.id.linear_demo_change_temp);
        lineFridgeTarget = (LinearLayout) findViewById(R.id.linear_demo_skb_fridge);
        lineFreezeTarget = (LinearLayout) findViewById(R.id.linear_demo_skb_freeze);
        lineChangeTarger = (LinearLayout) findViewById(R.id.linear_demo_skb_change);
        lineSterilize = (LinearLayout) findViewById(R.id.linear_demo_sterilize);

        tvFridgeModel = (TextView) findViewById(R.id.text_demo_fridge_model);
        tvFridgeModel.setOnClickListener(this);
        tvTitleStatusCode = (TextView) findViewById(R.id.title_demo_status_code);
        tvTitleStatusCode.setOnClickListener(this);
        tvStatusCode = (TextView) findViewById(R.id.text_demo_status_code);
        tvEnvTemp = (TextView) findViewById(R.id.text_demo_env_temp);
        tvEnvHum = (TextView) findViewById(R.id.text_demo_env_hum);
        tvFridgeTemp = (TextView) findViewById(R.id.text_demo_fridge_temp);
        tvFreezeTemp = (TextView) findViewById(R.id.text_demo_freeze_temp);
        tvChangeTemp = (TextView) findViewById(R.id.text_demo_change_temp);
        tvFridgeTarget = (TextView) findViewById(R.id.text_demo_fridge_target);
        tvFreezeTarget = (TextView) findViewById(R.id.text_demo_freeze_target);
        tvChangeTarget = (TextView) findViewById(R.id.text_demo_change_target);
        tvTime = (TextView) findViewById(R.id.text_demo_time);
        tvTest = (TextView) findViewById(R.id.text_demo_test);
        tvSterilizeRuntime = (TextView) findViewById(R.id.text_demo_sterilize_runtime);
        tvSterilizeInterval = (TextView) findViewById(R.id.text_demo_sterilize_interval);

        btnReturn = (Button) findViewById(R.id.btn_demo_return);
        btnReturn.setOnClickListener(this);
        btnWifi = (Button) findViewById(R.id.btn_demo_wifi);
        btnWifi.setOnClickListener(this);

        btnSterilizeSmart = (MyTestButton) findViewById(R.id.btn_demo_sterilize_smart);
        btnSterilizeStrong = (MyTestButton) findViewById(R.id.btn_demo_sterilize_strong);
        btnSterilize3 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_3);
        btnSterilize4 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_4);
        btnSterilize5 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_5);
        btnSterilize6 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_6);
        btnSterilize7 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_7);
        btnSterilize8 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_8);
        btnSterilize9 = (MyTestButton) findViewById(R.id.btn_demo_sterilize_9);
        btnSterilizeSmart.setOnClickListener(this);
        btnSterilizeStrong.setOnClickListener(this);
        btnSterilize3.setOnClickListener(this);
        btnSterilize4.setOnClickListener(this);
        btnSterilize5.setOnClickListener(this);
        btnSterilize6.setOnClickListener(this);
        btnSterilize7.setOnClickListener(this);
        btnSterilize8.setOnClickListener(this);
        btnSterilize9.setOnClickListener(this);

        skbFridge = (SeekBar) findViewById(R.id.skb_demo_fridge);
        skbFreeze = (SeekBar) findViewById(R.id.skb_demo_freeze);
        skbChange = (SeekBar) findViewById(R.id.skb_demo_change);


        imvQrCode = (ImageView) findViewById(R.id.imv_demo_test);

    }

    private void setView() throws RemoteException {
        listenerSeekBar();
        switch (mModel.mFridgeModel) {
            case ConstantUtil.BCD251_MODEL:
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineChangeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                lineChangeTarger.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_demo_top_left);
                initHoliday(R.id.btn_demo_top_right);
                initQuickCold(R.id.btn_demo_center_left);
                initQuickFreeze(R.id.btn_demo_center_right);
                initFridgeOpen(R.id.btn_demo_bottom_left);
                break;
            case ConstantUtil.BCD256_MODEL:
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineChangeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                lineChangeTarger.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_demo_top_left);
                initHoliday(R.id.btn_demo_top_right);
                initQuickCold(R.id.btn_demo_center_left);
                initQuickFreeze(R.id.btn_demo_center_right);
                initFridgeOpen(R.id.btn_demo_bottom_left);
                initTidbit(R.id.btn_demo_bottom_right);
                break;
            case ConstantUtil.BCD401_MODEL:
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_demo_top_left);
                initPurify(R.id.btn_demo_top_right);
                initQuickCold(R.id.btn_demo_center_left);
                initQuickFreeze(R.id.btn_demo_center_right);
                break;
            case ConstantUtil.BCD476_MODEL:
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                lineSterilize.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_demo_top_left);
                initHoliday(R.id.btn_demo_top_right);
                initQuickCold(R.id.btn_demo_center_left);
                initQuickFreeze(R.id.btn_demo_center_right);
                break;
        }
        isReady = true;
        tvTest.setText("使用馨小厨APP扫码绑定");
        QrCodeUtil.createQRCode(imvQrCode, TypeIdUtil.getCode(mContext, mModel.mTypeId), 300);

//        startRefreshUI();
    }

    private void setModel() throws RemoteException {
        //        if(mModel.mFridgeModel != null) {
        //        mModel.mFridgeModel = ConstantUtil.BCD251_MODEL;
        if (mModel.mFridgeModel.equals(ConstantUtil.BCD251_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel);
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD256_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel + "/" + mModel.mFridgeModel + "(S)");
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD401_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel + "/" + mModel.mFridgeModel + "(S)");
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD476_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel);
        }
        setView();
    }

    private void startQueryType() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mWaitTask == null) {
            Log.i(TAG, "mWaitTask is null,now creat!");
            mWaitTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0x01);
                }
            };
            mTimer.schedule(mWaitTask, 0, 500);
        }
    }

    private void stopQueryType() {
        if (mWaitTask != null) {
            Log.i(TAG, "mWaitTask cancel!");
            mWaitTask.cancel();
            mWaitTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startRefreshUI() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            Log.i(TAG, "mTimerTask is null,now creat!");
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0x02);
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    private void stopRefreshUI() {
        if (mTimerTask != null) {
            Log.i(TAG, "mTimerTask cancel!");
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    try {
                        isServiceReady = mService.getIsReady();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (isServiceReady) {
                        stopQueryType();
                        try {
                            mModel.mTypeId = mService.getHardId();
                            Log.i(TAG,"mModel.mTypeId = "+mModel.mTypeId);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        try {
                            mModel.mFridgeModel = mService.getHardType();
                            Log.i(TAG,"mModel.mFridgeModel = "+mModel.mFridgeModel);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        try {
                            setModel();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        try {
                            setTempRange();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 0x02:
                    updateSterilizeTime();
                    break;
                case 0x03:
                    handleDoorStatus();
                    break;
                case 0x04:
                    handleDoorAlarm();
                    break;
                case 0x05:
                    updateShowTemp(showTempStatus);
                    break;
                case 0x06:
                    try {
                        updateModeLevel(modeStauts);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    private void refreshUI() throws RemoteException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);
        tvTime.setText(strTime);
        tvStatusCode.setText(mService.getHardStatusCoder());
        updateModeLevel(mService.getModeInfo());
        //        tvTest.setText(mNetRunnable.getNtpHost()+"\n"+mNetRunnable.getTimeoutCounts()+":"+mNetRunnable.getRequestCounts()
        //                +"\n"+mNetRunnable.getTimeStamp()+"\n"+mNetRunnable.getTime());
        if (mModel.mFridgeModel.equals(ConstantUtil.BCD251_MODEL)) {
            tvFridgeTemp.setText(mModel.mFridgeShow + " ℃");
            tvFreezeTemp.setText(mModel.mFreezeShow + " ℃");
            tvChangeTemp.setText(mModel.mChangeShow + " ℃");
            if (mModel.isSmart) {
                btnSmart.setOn();
            } else {
                btnSmart.setOff();
            }
            if (mModel.isHoliday) {
                btnHoliday.setOn();
            } else {
                btnHoliday.setOff();
            }
            if (mModel.isQuickCold) {
                btnQuickCold.setOn();
            } else {
                btnQuickCold.setOff();
            }
            if (mModel.isQuickFreeze) {
                btnQuickFreeze.setOn();
            } else {
                btnQuickFreeze.setOff();
            }
            if (mModel.isFridgeOpen) {
                btnFridgeSwitch.setOn();
                btnFridgeSwitch.setText("冷藏开");
            } else {
                btnFridgeSwitch.setOff();
                btnFridgeSwitch.setText("冷藏关");
            }
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD256_MODEL)) {
            tvFridgeTemp.setText(mModel.mFridgeShow + " ℃");
            tvFreezeTemp.setText(mModel.mFreezeShow + " ℃");
            tvChangeTemp.setText(mModel.mChangeShow + " ℃");
            if (mModel.isSmart) {
                btnSmart.setOn();
            } else {
                btnSmart.setOff();
            }
            if (mModel.isHoliday) {
                btnHoliday.setOn();
            } else {
                btnHoliday.setOff();
            }
            if (mModel.isQuickCold) {
                btnQuickCold.setOn();
            } else {
                btnQuickCold.setOff();
            }
            if (mModel.isQuickFreeze) {
                btnQuickFreeze.setOn();
            } else {
                btnQuickFreeze.setOff();
            }
            if (mModel.isFridgeOpen) {
                btnFridgeSwitch.setOn();
                btnFridgeSwitch.setText("冷藏开");
            } else {
                btnFridgeSwitch.setOff();
                btnFridgeSwitch.setText("冷藏关");
            }
            if (mModel.isTidbit) {
                btnTidbit.setOn();
            } else {
                btnTidbit.setOff();
            }
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD401_MODEL)) {
            tvFridgeTemp.setText(mModel.mFridgeShow + " ℃");
            tvFreezeTemp.setText(mModel.mFreezeShow + " ℃");
            if (mModel.isSmart) {
                btnSmart.setOn();
            } else {
                btnSmart.setOff();
            }
            if (mModel.isPurify) {
                btnPurify.setOn();
            } else {
                btnPurify.setOff();
            }
            if (mModel.isQuickCold) {
                btnQuickCold.setOn();
            } else {
                btnQuickCold.setOff();
            }
            if (mModel.isQuickFreeze) {
                btnQuickFreeze.setOn();
            } else {
                btnQuickFreeze.setOff();
            }
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD476_MODEL)) {
            tvFridgeTemp.setText(mModel.mFridgeShow + " ℃");
            tvFreezeTemp.setText(mModel.mFreezeShow + " ℃");
            if (mModel.isSmart) {
                btnSmart.setOn();
            } else {
                btnSmart.setOff();
            }
            if (mModel.isHoliday) {
                btnHoliday.setOn();
            } else {
                btnHoliday.setOff();
            }
            if (mModel.isQuickCold) {
                btnQuickCold.setOn();
            } else {
                btnQuickCold.setOff();
            }
            if (mModel.isQuickFreeze) {
                btnQuickFreeze.setOn();
            } else {
                btnQuickFreeze.setOff();
            }
            switch (mModel.mSterilizeMode) {
                case 0:
                    tvSterilizeRuntime.setText("30:00");
                    setSterilizeButtonOff();
                    break;
                case 1:
                    setSterilizeButtonOff();
                    btnSterilizeSmart.setOn();
                    break;
                case 2:
                    setSterilizeButtonOff();
                    btnSterilizeStrong.setOn();
                    break;
                case 3:
                    setSterilizeButtonOff();
                    btnSterilize3.setOn();
                    break;
                case 4:
                    setSterilizeButtonOff();
                    btnSterilize4.setOn();
                    break;
                case 5:
                    setSterilizeButtonOff();
                    btnSterilize5.setOn();
                    break;
                case 6:
                    setSterilizeButtonOff();
                    btnSterilize6.setOn();
                    break;
                case 7:
                    setSterilizeButtonOff();
                    btnSterilize7.setOn();
                    break;
                case 8:
                    setSterilizeButtonOff();
                    btnSterilize8.setOn();
                    break;
                case 9:
                    setSterilizeButtonOff();
                    btnSterilize9.setOn();
                    break;
            }
            if (mModel.mSterilizeMode != 0 && isSterilizeRun) {
                SimpleDateFormat timeSterilize = new SimpleDateFormat("mm:ss");
                String strTimeSterilize = timeSterilize.format(new Date(countsSterilize * 1000));
                tvSterilizeRuntime.setText(strTimeSterilize);
            }
        }
    }

    private void updateSterilize(){
        switch (mModel.mSterilizeMode) {
            case 0:
                tvSterilizeRuntime.setText("30:00");
                setSterilizeButtonOff();
                break;
            case 1:
                setSterilizeButtonOff();
                btnSterilizeSmart.setOn();
                break;
            case 2:
                setSterilizeButtonOff();
                btnSterilizeStrong.setOn();
                break;
            case 3:
                setSterilizeButtonOff();
                btnSterilize3.setOn();
                break;
            case 4:
                setSterilizeButtonOff();
                btnSterilize4.setOn();
                break;
            case 5:
                setSterilizeButtonOff();
                btnSterilize5.setOn();
                break;
            case 6:
                setSterilizeButtonOff();
                btnSterilize6.setOn();
                break;
            case 7:
                setSterilizeButtonOff();
                btnSterilize7.setOn();
                break;
            case 8:
                setSterilizeButtonOff();
                btnSterilize8.setOn();
                break;
            case 9:
                setSterilizeButtonOff();
                btnSterilize9.setOn();
                break;
        }
    }
    private void updateSterilizeTime(){
        if (mModel.mSterilizeMode != 0 && isSterilizeRun) {
            SimpleDateFormat timeSterilize = new SimpleDateFormat("mm:ss");
            String strTimeSterilize = timeSterilize.format(new Date(countsSterilize * 1000));
            tvSterilizeRuntime.setText(strTimeSterilize);
        }
    }
    private Timer timerSterilize;
    private TimerTask taskSterilize;
    private boolean isSterilizeRun = false;
    private void startSterilizeTimer(){
        if(timerSterilize == null){
            timerSterilize = new Timer();
        }
        if(taskSterilize == null){
            taskSterilize = new TimerTask() {
                @Override
                public void run() {
                    if(isSterilizeRun){
                        if (countsSterilize > 0) {
                            countsSterilize--;
                            mHandler.sendEmptyMessage(0x02);
                        }
                    }
                }
            };
            timerSterilize.schedule(taskSterilize,0,1000);
        }
    }
    private void stopSterilizeTimer(){
        if(taskSterilize != null){
            taskSterilize.cancel();
            taskSterilize = null;
        }
        if(timerSterilize != null){
            timerSterilize.cancel();
            timerSterilize = null;
        }
        countsSterilize = 0;
    }

    private void setSterilizeButtonOff() {
        btnSterilizeSmart.setOff();
        btnSterilizeStrong.setOff();
        btnSterilize3.setOff();
        btnSterilize4.setOff();
        btnSterilize5.setOff();
        btnSterilize6.setOff();
        btnSterilize7.setOff();
        btnSterilize8.setOff();
        btnSterilize9.setOff();
    }

    private void initSmart(final int idButton) {
        btnSmart = (MyTestButton) findViewById(idButton);
        btnSmart.setText("智能");
        btnSmart.setEnabled(true);
        btnSmart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnSmart.isPress()) {
                        try {
                            mService.setSmartMode(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setSmartMode(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void initHoliday(final int idButton) {
        btnHoliday = (MyTestButton) findViewById(idButton);
        btnHoliday.setText("假日");
        btnHoliday.setEnabled(true);

        btnHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnHoliday.isPress()) {
                        try {
                            mService.setHolidayMode(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setHolidayMode(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void initQuickCold(final int idButton) {
        btnQuickCold = (MyTestButton) findViewById(idButton);
        btnQuickCold.setText("速冷");
        btnQuickCold.setEnabled(true);

        btnQuickCold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnQuickCold.isPress()) {
                        try {
                            mService.setQuickColdMode(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setQuickColdMode(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        btnQuickCold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableQuickCold.equals("none")) {
                    return false;
                } else {
                    String show = mModel.mDisableQuickCold + "模式已开启，如要开启速冷请先退出" + mModel.mDisableQuickCold + "模式";
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
            }
        });
    }

    private void initQuickFreeze(final int idButton) {
        btnQuickFreeze = (MyTestButton) findViewById(idButton);
        btnQuickFreeze.setText("速冻");
        btnQuickFreeze.setEnabled(true);

        btnQuickFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnQuickFreeze.isPress()) {
                        try {
                            mService.setQuickFreezeMode(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setQuickFreezeMode(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void initFridgeOpen(final int idButton) {
        btnFridgeSwitch = (MyTestButton) findViewById(idButton);
        //        btnFridgeSwitch.setText("冷藏开关");
        btnFridgeSwitch.setEnabled(true);

        btnFridgeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnFridgeSwitch.isPress()) {
                        try {
                            mService.setFridgeSwitch(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setFridgeSwitch(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        btnFridgeSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableFridgeOpen.equals("none")) {
                    return false;
                } else {
                    String show;
                    if (mModel.mDisableFridgeOpen.equals("关闭")) {
                        show = "冷藏室已" + mModel.mDisableFridgeOpen + "，如要调节温度请先开启冷藏室";
                    } else {
                        show = mModel.mDisableFridgeOpen + "模式已开启，如要关闭冷藏室请先退出" + mModel.mDisableFridgeOpen + "模式";
                    }
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
            }
        });
    }

    private void initTidbit(final int idButton) {
        btnTidbit = (MyTestButton) findViewById(idButton);
        btnTidbit.setText("珍品");
        btnTidbit.setEnabled(true);
        btnTidbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnTidbit.isPress()) {
                        try {
                            mService.setTidbitMode(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setTidbitMode(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void initPurify(final int idButton) {
        btnPurify = (MyTestButton) findViewById(idButton);
        btnPurify.setText("净化");
        btnPurify.setEnabled(true);
        btnPurify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnPurify.isPress()) {
                        try {
                            mService.setPurifyMode(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setPurifyMode(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_demo_return:
                finish();
                break;
            case R.id.btn_demo_wifi:
//                Intent intentWifi = new Intent(this,WifiActivity.class);
//                intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentWifi);
                break;
            case R.id.title_demo_status_code:
                Intent intentActivity = new Intent(this,MainActivity.class);
                startActivity(intentActivity);
                finish();
                break;
            case R.id.text_demo_fridge_model:
                onclickCounts++;
                if (onclickCounts > 5) {
                    onclickCounts = 0;
                    PackageManager manager = getApplicationContext().getPackageManager();
                    PackageInfo info;
                    String tftVersion = "none";
                    try {
                        info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                        tftVersion = info.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.haiersmart.sfcontrol", "com.haiersmart.sfcontrol.ui.FactoryStatusActivity"));
                    intent.setAction("FactoryStatusActivity");
                    intent.putExtra("version", tftVersion);
                    startActivity(intent);
                }
                break;
            case R.id.btn_demo_sterilize_smart:
                if (btnSterilizeSmart.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("06:00");
                    try {
                        String res = mService.setSterilizeMode(1);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_strong:
                if (btnSterilizeStrong.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("04:00");
                    try {
                        String res = mService.setSterilizeMode(2);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_3:
                if (btnSterilize3.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("03:00");
                    try {
                        String res = mService.setSterilizeMode(3);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_4:
                if (btnSterilize4.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("04:00");
                    try {
                        String res = mService.setSterilizeMode(4);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_5:
                if (btnSterilize5.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("05:00");
                    try {
                        String res = mService.setSterilizeMode(5);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_6:
                if (btnSterilize6.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("06:00");
                    try {
                        String res = mService.setSterilizeMode(6);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_7:
                if (btnSterilize7.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("07:00");
                    try {
                        String res = mService.setSterilizeMode(7);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_8:
                if (btnSterilize8.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("08:00");
                    try {
                        String res = mService.setSterilizeMode(8);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_demo_sterilize_9:
                if (btnSterilize9.isPress()) {
                    tvSterilizeInterval.setText("00:00");
                    try {
                        String res = mService.setSterilizeMode(0);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvSterilizeInterval.setText("09:00");
                    try {
                        String res = mService.setSterilizeMode(9);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void listenerSeekBar() {
        skbFridge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFridgeTarget.setText(Integer.toString(progress + mModel.mFridgeMin) + " ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    String res = mService.setFridgeTemp(progress + mModel.mFridgeMin);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        skbFridge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableFridge.equals("none")) {
                    return false;
                } else {
                    String show;
                    if (mModel.mDisableFridge.equals("关闭")) {
                        show = "冷藏室已" + mModel.mDisableFridge + "，如要调节温度请先开启冷藏室";
                    } else {
                        show = mModel.mDisableFridge + "模式已开启，如要调节温度请先退出" + mModel.mDisableFridge + "模式";
                    }
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
            }
        });
        skbFreeze.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFreezeTarget.setText(Integer.toString(progress + mModel.mFreezeMin) + " ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    String res = mService.setFreezeTemp(progress + mModel.mFreezeMin);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        skbFreeze.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableFreeze.equals("none")) {
                    return false;
                } else {
                    String show;
                    if (mModel.mDisableFreeze.equals("关闭")) {
                        show = "冷冻室已" + mModel.mDisableFreeze + "，如要调节温度请先开启冷冻室";
                    } else {
                        show = mModel.mDisableFreeze + "模式已开启，如要调节温度请先退出" + mModel.mDisableFreeze + "模式";
                    }
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }

            }
        });
        skbChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvChangeTarget.setText(Integer.toString(progress + mModel.mChangeMin) + " ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    String res = mService.setChangeTemp(progress + mModel.mChangeMin);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        skbChange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableChange.equals("none")) {
                    return false;
                } else {
                    String show;
                    if (mModel.mDisableChange.equals("关闭")) {
                        show = "变温室已" + mModel.mDisableFreeze + "，如要调节温度请先开启变温室";
                    } else {
                        show = mModel.mDisableChange + "模式已开启，如要调节温度请先退出" + mModel.mDisableChange + "模式";
                    }
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
            }
        });
    }

    private String showTempStatus = new String();
    private void updateShowTemp(String showTempJson) {
        JSONArray jsonArray = JSONArray.parseArray(showTempJson);
        Log.i(TAG, "show temp jsonArray:" + jsonArray);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = (String) jsonObject.get("name");
            int value = (int) jsonObject.get("value");
            Log.i(TAG, i + " name:" + name + " value:" + value);
            if (name.equals(EnumBaseName.fridgeShowTemp.toString())) {
                mModel.mFridgeShow = value;
            } else if (name.equals(EnumBaseName.freezeShowTemp.toString())) {
                mModel.mFreezeShow = value;
            } else if (name.equals(EnumBaseName.changeShowTemp.toString())) {
                mModel.mChangeShow = value;
            }
        }
        tvFridgeTemp.setText(mModel.mFridgeShow + " ℃");
        tvFreezeTemp.setText(mModel.mFreezeShow + " ℃");
        tvChangeTemp.setText(mModel.mChangeShow + " ℃");

    }

    private String modeStauts = new String();
    private void updateModeLevel(String modeJson) throws RemoteException {
        JSONArray jsonArray = JSONArray.parseArray(modeJson);
        Log.i(TAG, "mode level jsonArray:" + jsonArray);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = (String) jsonObject.get("name");
            int value = (int) jsonObject.get("value");
            String disable = (String) jsonObject.get("disable");
//            Log.i(TAG, i + " name:" + name + " value:" + value + " disable:" + disable);
            if (name.equals(EnumBaseName.fridgeTargetTemp.toString())) {
                mModel.mFridgeTarget = value;
                mModel.mDisableFridge = disable;
                skbFridge.setProgress(mModel.mFridgeTarget - mModel.mFridgeMin);
                if (mModel.mDisableFridge.equals("none")) {
                    //            skbFridge.setEnabled(true);
                    tvFridgeTarget.setText(Integer.toString(mModel.mFridgeTarget) + " ℃");
                } else {
                    //            skbFridge.setEnabled(false);
                    tvFridgeTarget.setText(mModel.mDisableFridge);
                }
            } else if (name.equals(EnumBaseName.freezeTargetTemp.toString())) {
                mModel.mFreezeTarget = value;
                mModel.mDisableFreeze = disable;
                skbFreeze.setProgress(mModel.mFreezeTarget - mModel.mFreezeMin);
                if (mModel.mDisableFreeze.equals("none")) {
                    //            skbFreeze.setEnabled(true);
                    tvFreezeTarget.setText(Integer.toString(mModel.mFreezeTarget) + " ℃");
                } else {
                    //            skbFreeze.setEnabled(false);
                    tvFreezeTarget.setText(mModel.mDisableFreeze);
                }
            } else if (name.equals(EnumBaseName.changeTargetTemp.toString())) {
                mModel.mChangeTarget = value;
                mModel.mDisableChange = disable;
                skbChange.setProgress(mModel.mChangeTarget - mModel.mChangeMin);
                if (mModel.mDisableChange.equals("none")) {
                    //            skbChange.setEnabled(true);
                    tvChangeTarget.setText(Integer.toString(mModel.mChangeTarget) + " ℃");
                } else {
                    //            skbChange.setEnabled(false);
                    tvChangeTarget.setText(mModel.mDisableChange);
                }
            } else if (name.equals(EnumBaseName.smartMode.toString())) {
                if(value==1){
                    mModel.isSmart = true;
                    btnSmart.setOn();
                }else {
                    mModel.isSmart = false;
                    btnSmart.setOff();
                }
                mModel.mDisableSmart = disable;
            } else if (name.equals(EnumBaseName.holidayMode.toString())) {
                if(value==1){
                    mModel.isHoliday = true;
                    btnHoliday.setOn();
                }else {
                    mModel.isHoliday = false;
                    btnHoliday.setOff();
                }
                mModel.mDisableHoliday = disable;
            } else if (name.equals(EnumBaseName.quickColdMode.toString())) {
                if(value==1){
                    mModel.isQuickCold = true;
                    btnQuickCold.setOn();
                }else {
                    mModel.isQuickCold = false;
                    btnQuickCold.setOff();
                }
                mModel.mDisableQuickCold = disable;
            } else if (name.equals(EnumBaseName.quickFreezeMode.toString())) {
                if(value==1){
                    mModel.isQuickFreeze = true;
                    btnQuickFreeze.setOn();
                }else {
                    mModel.isQuickFreeze = false;
                    btnQuickFreeze.setOff();
                }
                mModel.mDisableQuickFreeze = disable;
            } else if (name.equals(EnumBaseName.fridgeSwitch.toString())) {
                mModel.isFridgeOpen = (value == 1) ? true : false;
                mModel.mDisableFridgeOpen = disable;
            } else if (name.equals(EnumBaseName.tidbitMode.toString())) {
                mModel.isTidbit = (value == 1) ? true : false;
                mModel.mDisableTidbit = disable;
            } else if (name.equals(EnumBaseName.purifyMode.toString())) {
                mModel.isPurify = (value == 1);
                mModel.mDisablePurify = disable;
            } else if (name.equals(EnumBaseName.SterilizeMode.name())) {
                mModel.mSterilizeMode = value;
                mModel.mDisableSterilize = disable;
                updateSterilize();
            } else if (name.equals(EnumBaseName.SterilizeSwitch.name())) {
                if (value == 1) {
                    mModel.isSterilize = true;
                    if(mService.getSterilizeRemanTime() > 0) {
                        countsSterilize = mService.getSterilizeRemanTime();
                    }else {
                        countsSterilize = 0;
                    }
                    isSterilizeRun = mService.getSterilizeRunStatus();
                    startSterilizeTimer();
//                    countsSterilize = 30 * 60;
                } else {
                    mModel.isSterilize = false;
                    stopSterilizeTimer();
                }
            }
        }
    }

    private String alarmStauts = new String();
    private void handleDoorAlarm() {
        JSONObject jsonObject = JSONObject.parseObject(alarmStauts);
        Log.i(TAG, "alarm jsonObject:" + jsonObject);
        Integer value = (Integer) jsonObject.get("fridge");
        if (value == null || value == 0) {
            cancelAlarmWindow("冷藏");
        } else {
            popAlarmWindow("冷藏");
        }
        value = (Integer) jsonObject.get("freeze");
        if (value == null || value == 0) {
            cancelAlarmWindow("冷冻");
        } else {
            popAlarmWindow("冷冻");
        }
        value = (Integer) jsonObject.get("change");
        if (value == null || value == 0) {
            cancelAlarmWindow("变温");
        } else {
            popAlarmWindow("变温");
        }
    }

    private String doorStauts = new String();
    private void handleDoorStatus() {
        JSONObject jsonObject = JSONObject.parseObject(doorStauts);
        Log.i(TAG, "door jsonObject:" + jsonObject);

    }

    private void handleSterilizeStatus(String SterilizeJson) {
        JSONObject jsonObject = JSONObject.parseObject(SterilizeJson);
        int time = (int) jsonObject.get("time");
        if(time > 0) {
            countsSterilize = time;
        }else {
            countsSterilize = 0;
        }
        if ((int) jsonObject.get("run") == 1) {
            isSterilizeRun = true;
        } else {
            isSterilizeRun = false;
        }

    }

    private ICallback mCallback = new ICallback.Stub(){

        @Override
        public void notifyShowTemp(String showTemp) throws RemoteException {
//            updateShowTemp(showTemp);
            showTempStatus = showTemp;
            mHandler.sendEmptyMessage(0x05);
        }

        @Override
        public void notifyErrorInfo(String errorInfo) throws RemoteException {

        }

        @Override
        public void notifyDoorStatus(String doorStatus) throws RemoteException {
            doorStauts = doorStatus;
            mHandler.sendEmptyMessage(0x03);
//            handleDoorStatus(doorStatus);
        }

        @Override
        public void notifyDooralarm(String doorAlarm) throws RemoteException {
            alarmStauts = doorAlarm;
            mHandler.sendEmptyMessage(0x04);
//            handleDoorAlarm(doorAlarm);
        }

        @Override
        public void notifySterilizeRun(String run) throws RemoteException {
            handleSterilizeStatus(run);
        }

        @Override
        public void notifyMode(String mode) throws RemoteException {
            modeStauts = mode;
            mHandler.sendEmptyMessage(0x06);
//            updateModeLevel(mode);
        }

    };

    private void setTempRange() throws RemoteException {

        String jsonRange = mService.getTempRange();
        JSONObject jsonObject = JSONObject.parseObject(jsonRange);
        Log.i(TAG,"jsonRange = "+jsonRange);
        mModel.mFridgeMin = (Integer) jsonObject.get("fridgeMinValue");
        mModel.mFridgeMax = (Integer) jsonObject.get("fridgeMaxValue");
        mModel.mFreezeMin = (Integer) jsonObject.get("freezeMinValue");
        mModel.mFreezeMax = (Integer) jsonObject.get("freezeMaxValue");
        mModel.mChangeMin = (Integer) jsonObject.get("changeMinValue");
        mModel.mChangeMax = (Integer) jsonObject.get("changeMaxValue");

        skbFridge.setMax(mModel.mFridgeMax - mModel.mFridgeMin);
        skbChange.setMax(mModel.mChangeMax - mModel.mChangeMin);
        skbFreeze.setMax(mModel.mFreezeMax - mModel.mFreezeMin);
        updateModeLevel(mService.getModeInfo());
        updateShowTemp(mService.getShowTemp());
    }

    private void popAlarmWindow(String show) {
        if (alarmWindow == null) {
            alarmWindow = new AlarmWindow(this);
        }
        alarmWindow.addShow(show);
        alarmWindow.showDialog();
    }

    private void cancelAlarmWindow(String show) {
        if (alarmWindow != null) {
            alarmWindow.deleteShow(show);
            if (alarmWindow.isNoAlarm()) {
                alarmWindow = null;
            }
        }
    }


}


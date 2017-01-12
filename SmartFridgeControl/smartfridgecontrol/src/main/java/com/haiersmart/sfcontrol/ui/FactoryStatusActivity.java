/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 工厂测试界面
 * Author:  Holy.Han
 * Date:  2016/11/30
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.draw.MyTestAudioButton;
import com.haiersmart.sfcontrol.draw.PopInputListener;
import com.haiersmart.sfcontrol.draw.PopWindowNormalInput;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.service.MainBoardParameters;
import com.haiersmart.sfcontrol.utilslib.DeviceUtil;
import com.haiersmart.sfcontrol.utilslib.FactoryAudioUtil;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.SystemCmdUtil;

import java.util.Timer;
import java.util.TimerTask;



/**
 * <p>function: </p>
 * <p>description:  工厂测试界面</p>
 * history:  1. 2016/11/30
 * Author: Holy.Han
 * modification:
 */
public class FactoryStatusActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "FactoryStatusActivity";
    private final String PASSWORD = "lkjh";

    private MainBoardParameters mMBParam;
    private String mFridgeModel, mTftVersion, mOsVersion;
    RadioButton rbtVersion, rbtReset, rbtStatus, rbtCamera, rbtTP, rbtAudio, rbtMarket, rbtDebug;
    LinearLayout llVersion, llReset, llStatus, llCamera, llTP, llAudio, llMarket, llDebug;
    LinearLayout llEnvTemp, llEnvHum, llFridge, llFreeze, llChange, llDefrostSensor, llFreezeDefrost,
    llFreezeFan,llFreezeDefrostSensor;
    LinearLayout llFridgeDoor;
    TextView tvEnvTemp, tvEnvHum, tvFridge, tvFreeze, tvChange, tvDefrostSensor, tvFreezeDefrost,
    tvFreezeFan,tvFreezeDefrostSensor;
    TextView tvFridgeDoor, tvCommunication, tvPir, tvWifi;
    TextView tvFridgeModel, tvTftVersion, tvOsVersion, tvMac, tvTP;
    TextView tvTftVersionTitle,tvOsVersionTitle, tvMacTitle;
    Button btnReturn, btnResetEnter;
    MyTestAudioButton btnRecord, btnPlayAll, btnPlayLeft, btnPlayRight;
    TextView tvRecord, tvPlayAll, tvPlayLeft, tvPlayRight;
    ProgressBar prbRecord, prbPlayAll, prbPlayLeft, prbPlayRight;
    FactoryAudioUtil audioUtil;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int countsOnClickTft = 0, countsOnClickOs = 0, countsOnClickMac = 0;
    private int currentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stauts_factory);
        findViews();
        Intent intent = new Intent();
        intent.setClass(this, ControlMainBoardService.class);
        startService(intent);
        mMBParam = MainBoardParameters.getInstance();
        mFridgeModel = mMBParam.getFridgeType();
        mTftVersion = this.getIntent().getStringExtra("version");
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        deleteAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLinearContent(currentView);
    }

    private void findViews() {
        rbtVersion = (RadioButton) findViewById(R.id.rbt_factory_version);
        rbtReset = (RadioButton) findViewById(R.id.rbt_factory_reset);
        rbtStatus = (RadioButton) findViewById(R.id.rbt_factory_status);
        rbtCamera = (RadioButton) findViewById(R.id.rbt_factory_camera);
        rbtTP = (RadioButton) findViewById(R.id.rbt_factory_TP);
        rbtAudio = (RadioButton) findViewById(R.id.rbt_factory_audio);
        rbtMarket = (RadioButton) findViewById(R.id.rbt_factory_market);
        rbtDebug = (RadioButton) findViewById(R.id.rbt_factory_debug);
        rbtVersion.setOnClickListener(this);
        rbtReset.setOnClickListener(this);
        rbtStatus.setOnClickListener(this);
        rbtCamera.setOnClickListener(this);
        rbtTP.setOnClickListener(this);
        rbtAudio.setOnClickListener(this);
        rbtMarket.setOnClickListener(this);
        rbtDebug.setOnClickListener(this);

        llVersion = (LinearLayout) findViewById(R.id.linear_factory_version);
        llReset = (LinearLayout) findViewById(R.id.linear_factory_reset);
        llStatus = (LinearLayout) findViewById(R.id.linear_factory_status);
        llCamera = (LinearLayout) findViewById(R.id.linear_factory_camera);
        llTP = (LinearLayout) findViewById(R.id.linear_factory_TP);
        llAudio = (LinearLayout) findViewById(R.id.linear_factory_audio);
        llMarket = (LinearLayout) findViewById(R.id.linear_factory_market);
        llDebug = (LinearLayout) findViewById(R.id.linear_factory_debug);

        llEnvTemp = (LinearLayout) findViewById(R.id.linear_factory_env_temp);
        llEnvHum = (LinearLayout) findViewById(R.id.linear_factory_env_hum);
        llFridge = (LinearLayout) findViewById(R.id.linear_factory_fridge);
        llFreeze = (LinearLayout) findViewById(R.id.linear_factory_freeze);
        llChange = (LinearLayout) findViewById(R.id.linear_factory_change);
        llDefrostSensor = (LinearLayout) findViewById(R.id.linear_factory_defrost_sensor);
        llFreezeDefrost = (LinearLayout) findViewById(R.id.linear_factory_freeze_defrost);
        llFreezeFan = (LinearLayout) findViewById(R.id.linear_factory_freeze_fan);
        llFridgeDoor = (LinearLayout) findViewById(R.id.linear_factory_fridge_door);
        llFreezeDefrostSensor = (LinearLayout) findViewById(R.id.linear_factory_freeze_defrost_sensor);

        tvEnvTemp = (TextView) findViewById(R.id.text_factory_env_temp);
        tvEnvHum = (TextView) findViewById(R.id.text_factory_env_hum);
        tvFridge = (TextView) findViewById(R.id.text_factory_fridge);
        tvFreeze = (TextView) findViewById(R.id.text_factory_freeze);
        tvChange = (TextView) findViewById(R.id.text_factory_change);
        tvDefrostSensor = (TextView) findViewById(R.id.text_factory_defrost_sensor);
        tvFreezeDefrost = (TextView) findViewById(R.id.text_factory_freeze_defrost);
        tvFreezeFan = (TextView) findViewById(R.id.text_factory_freeze_fan);
        tvFridgeDoor = (TextView) findViewById(R.id.text_factory_fridge_door);
        tvCommunication = (TextView) findViewById(R.id.text_factory_communication);
        tvPir = (TextView) findViewById(R.id.text_factory_pir);
        tvWifi = (TextView) findViewById(R.id.text_factory_wifi);
        tvFreezeDefrostSensor= (TextView) findViewById(R.id.text_factory_freeze_defrost_sensor);

        tvFridgeModel = (TextView) findViewById(R.id.text_factory_fridge_model);
        tvTftVersion = (TextView) findViewById(R.id.text_factory_tft_version);
        tvTftVersionTitle = (TextView) findViewById(R.id.title_factory_tft_version);
        tvTftVersionTitle.setOnClickListener(this);
        tvOsVersion = (TextView) findViewById(R.id.text_factory_os_version);
        tvOsVersionTitle = (TextView) findViewById(R.id.title_factory_os_version);
        tvOsVersionTitle.setOnClickListener(this);
        tvMac = (TextView) findViewById(R.id.text_factory_mac);
        tvMacTitle = (TextView) findViewById(R.id.title_factory_mac);
        tvMacTitle.setOnClickListener(this);
        tvTP = (TextView) findViewById(R.id.text_factory_TP);

        btnReturn = (Button) findViewById(R.id.btn_factory_return);
        btnReturn.setOnClickListener(this);
        btnResetEnter = (Button) findViewById(R.id.btn_factory_reset);
        btnResetEnter.setOnClickListener(this);

        btnRecord = (MyTestAudioButton) findViewById(R.id.linear_factory_record);
        btnPlayAll = (MyTestAudioButton) findViewById(R.id.linear_factory_play_all);
        btnPlayLeft = (MyTestAudioButton) findViewById(R.id.linear_factory_play_left);
        btnPlayRight = (MyTestAudioButton) findViewById(R.id.linear_factory_play_right);
        btnRecord.setOnClickListener(this);
        btnPlayAll.setOnClickListener(this);
        btnPlayLeft.setOnClickListener(this);
        btnPlayRight.setOnClickListener(this);
        tvRecord = (TextView) findViewById(R.id.text_factory_record);
        tvPlayAll = (TextView) findViewById(R.id.text_factory_play_all);
        tvPlayLeft = (TextView) findViewById(R.id.text_factory_play_left);
        tvPlayRight = (TextView) findViewById(R.id.text_factory_play_right);
        prbRecord = (ProgressBar) findViewById(R.id.prb_factory_record);
        prbPlayAll = (ProgressBar) findViewById(R.id.prb_factory_play_all);
        prbPlayLeft = (ProgressBar) findViewById(R.id.prb_factory_play_left);
        prbPlayRight = (ProgressBar) findViewById(R.id.prb_factory_play_right);
    }

    private void initViews() {
        setLinearContent(R.id.rbt_factory_version);//
        if (mFridgeModel.equals(ConstantUtil.BCD251_MODEL)) {
            tvFridgeModel.setText(mFridgeModel);
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
        }else if(mFridgeModel.equals(ConstantUtil.BCD401_MODEL)){
            tvFridgeModel.setText(mFridgeModel+"/"+mFridgeModel+"(S)");
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
        }else if (mFridgeModel.equals(ConstantUtil.BCD256_MODEL)){
            tvFridgeModel.setText(mFridgeModel+"/"+mFridgeModel+"(S)");
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
        }
        initStatusView();
        initDebugView();
    }

    private void initStatusView() {
        if (mFridgeModel.equals(ConstantUtil.BCD251_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llChange.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
        }else if(mFridgeModel.equals(ConstantUtil.BCD401_MODEL)){
            llEnvTemp.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llFreezeDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
        }else if (mFridgeModel.equals(ConstantUtil.BCD256_MODEL)){
            llEnvTemp.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llChange.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
        }
    }

    private void initDebugView() {
        if (mFridgeModel.equals(ConstantUtil.BCD658_MODEL)) {
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_factory_return:
                finish();
                break;
            case R.id.title_factory_tft_version:
                countsOnClickTft++;
                if (countsOnClickTft > 5) {
                    countsOnClickTft = 0;
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
                break;
            case R.id.title_factory_os_version:
                countsOnClickOs++;
                if (countsOnClickOs > 5) {
                    countsOnClickOs = 0;
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.launcher", "com.android.launcher2.Launcher"));
                    startActivity(intent);
                }
                break;
            case R.id.title_factory_mac:
                countsOnClickMac++;
                if (countsOnClickMac > 5) {
                    countsOnClickMac = 0;
                    Intent intent = new Intent(this, DebugActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.linear_factory_record:
                if (btnRecord.isPress()) {
                    audioUtil.stopAudioRecord();
                } else {
                    audioUtil.startAudioRecord();
                }
                break;
            case R.id.linear_factory_play_all:
                if (btnPlayAll.isPress()) {
                    audioUtil.stopAudioPlay(true, true);
                } else {
                    audioUtil.startAudioPlay(true, true);
                }
                break;
            case R.id.linear_factory_play_left:
                if (btnPlayLeft.isPress()) {
                    audioUtil.stopAudioPlay(true, false);
                } else {
                    audioUtil.startAudioPlay(true, false);
                }
                break;
            case R.id.linear_factory_play_right:
                if (btnPlayRight.isPress()) {
                    audioUtil.stopAudioPlay(false, true);
                } else {
                    audioUtil.startAudioPlay(false, true);
                }
                break;
            case R.id.btn_factory_reset:
                popResetPassWin();
                break;
            default:
                setLinearContent(v.getId());
                break;
        }
    }

    private void setLinearContent(int num) {
        currentView = num;
        stopTimer();
        deleteAudio();
        llVersion.setVisibility(View.GONE);
        llReset.setVisibility(View.GONE);
        llStatus.setVisibility(View.GONE);
        llCamera.setVisibility(View.GONE);
        llTP.setVisibility(View.GONE);
        llAudio.setVisibility(View.GONE);
        llMarket.setVisibility(View.GONE);
        llDebug.setVisibility(View.GONE);
        switch (num) {
            case R.id.rbt_factory_version:
                rbtVersion.setChecked(true);
                setVersionUI();
                llVersion.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_reset:
                rbtReset.setChecked(true);
                llReset.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_status:
                rbtStatus.setChecked(true);
                startTimer();
                llStatus.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_camera:
                rbtCamera.setChecked(true);
                llStatus.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_TP:
                rbtTP.setChecked(true);
                setTpNoContent();
                llTP.setVisibility(View.VISIBLE);
                try {
                    checkTpState();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rbt_factory_audio:
                rbtAudio.setChecked(true);
                createAudio();
                llAudio.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_market:
                rbtMarket.setChecked(true);
                llMarket.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_debug:
                rbtDebug.setChecked(true);
                llDebug.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startTimer() {
        MyLogUtil.i(TAG, "startTimer");
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0x01);
                }
            };
        }

        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, 0, 300);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    refreshStatusUI();
                    break;
                case 0x02:
                    setTpOkContent();
                    break;
                case 0x03:
                    setTpFailContent();
                    break;
            }
        }
    };

    private void refreshStatusUI() {
        boolean isCommunSuccess = mMBParam.getMbsValueByName(EnumBaseName.communicationOverTime.toString()) == 0 ? true : false;
        if (isCommunSuccess) {
            if(mFridgeModel.equals(ConstantUtil.BCD401_MODEL)){
                if (mMBParam.getMbsValueByName(EnumBaseName.envTempSensorErr.toString()) == 0) {
                    tvEnvTemp.setText((float)mMBParam.getMbsValueByName(EnumBaseName.envRealTemp.toString())/10 + "℃");
                    tvEnvTemp.setTextColor(getResources().getColor(R.color.black2));
                } else {
                    tvEnvTemp.setText(getResources().getString(R.string.text_factory_error));
                    tvEnvTemp.setTextColor(getResources().getColor(R.color.red));
                }
            }else {
                if (mMBParam.getMbsValueByName(EnumBaseName.envTempSensorErr.toString()) == 0) {
                    tvEnvTemp.setText(mMBParam.getMbsValueByName(EnumBaseName.envShowTemp.toString()) + "℃");
                    tvEnvTemp.setTextColor(getResources().getColor(R.color.black2));
                } else {
                    tvEnvTemp.setText(getResources().getString(R.string.text_factory_error));
                    tvEnvTemp.setTextColor(getResources().getColor(R.color.red));
                }
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.envHumSensorErr.toString()) == 0) {
                tvEnvHum.setText(mMBParam.getMbsValueByName(EnumBaseName.envShowHum.toString()) + "%");
                tvEnvHum.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvEnvHum.setText(getResources().getString(R.string.text_factory_error));
                tvEnvHum.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeSensorErr.toString()) == 0) {
                tvFridge.setText(mMBParam.getMbsValueByName(EnumBaseName.fridgeShowTemp.toString()) + "℃");
                tvFridge.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridge.setText(getResources().getString(R.string.text_factory_error));
                tvFridge.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.freezeSensorErr.toString()) == 0) {
                tvFreeze.setText(mMBParam.getMbsValueByName(EnumBaseName.freezeShowTemp.toString()) + "℃");
                tvFreeze.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFreeze.setText(getResources().getString(R.string.text_factory_error));
                tvFreeze.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.changeSensorErr.toString()) == 0) {
                tvChange.setText(mMBParam.getMbsValueByName(EnumBaseName.changeShowTemp.toString()) + "℃");
                tvChange.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvChange.setText(getResources().getString(R.string.text_factory_error));
                tvChange.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.defrostSensorErr.toString()) == 0) {
                tvDefrostSensor.setText(getResources().getString(R.string.text_factory_normal));
                tvDefrostSensor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
                tvDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.freezeDefrostSensorErr.toString()) == 0) {
                tvFreezeDefrostSensor.setText(getResources().getString(R.string.text_factory_normal));
                tvFreezeDefrostSensor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFreezeDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
                tvFreezeDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.freezeDefrostErr.toString()) == 0) {
                tvFreezeDefrost.setText(getResources().getString(R.string.text_factory_normal));
                tvFreezeDefrost.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFreezeDefrost.setText(getResources().getString(R.string.text_factory_error));
                tvFreezeDefrost.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.freezeFanErr.name()) == 0) {
                tvFreezeFan.setText(getResources().getString(R.string.text_factory_normal));
                tvFreezeFan.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFreezeFan.setText(getResources().getString(R.string.text_factory_error));
                tvFreezeFan.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeDoorStatus.toString()) == 0) {
                tvFridgeDoor.setText(getResources().getString(R.string.text_door_close));
                tvFridgeDoor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridgeDoor.setText(getResources().getString(R.string.text_door_open));
                tvFridgeDoor.setTextColor(getResources().getColor(R.color.black2));
            }
            tvCommunication.setText(getResources().getString(R.string.text_factory_normal));
            tvCommunication.setTextColor(getResources().getColor(R.color.black2));
            //        tvWifi = (TextView)findViewById(R.id.text_factory_wifi);
        } else {
            tvEnvTemp.setText(getResources().getString(R.string.text_factory_error));
            tvEnvTemp.setTextColor(getResources().getColor(R.color.red));
            tvEnvHum.setText(getResources().getString(R.string.text_factory_error));
            tvEnvHum.setTextColor(getResources().getColor(R.color.red));
            tvFridge.setText(getResources().getString(R.string.text_factory_error));
            tvFridge.setTextColor(getResources().getColor(R.color.red));
            tvFreeze.setText(getResources().getString(R.string.text_factory_error));
            tvFreeze.setTextColor(getResources().getColor(R.color.red));
            tvChange.setText(getResources().getString(R.string.text_factory_error));
            tvChange.setTextColor(getResources().getColor(R.color.red));
            tvDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
            tvDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            tvFreezeDefrost.setText(getResources().getString(R.string.text_factory_error));
            tvFreezeDefrost.setTextColor(getResources().getColor(R.color.red));
            tvFreezeFan.setText(getResources().getString(R.string.text_factory_error));
            tvFreezeFan.setTextColor(getResources().getColor(R.color.red));
            tvFridgeDoor.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeDoor.setTextColor(getResources().getColor(R.color.red));
            tvCommunication.setText(getResources().getString(R.string.text_factory_error));
            tvCommunication.setTextColor(getResources().getColor(R.color.red));
        }
        setPirContent();
        setWifiContent();
    }

    private void setVersionUI() {
        mOsVersion = mMBParam.getOSVersion();
        String macString = DeviceUtil.getLocalMacAddress(getApplicationContext());
        tvTftVersion.setText(mTftVersion);
        tvOsVersion.setText(mOsVersion);
        tvMac.setText(macString);
    }

    private void setTpNoContent() {
        tvTP.setText(getResources().getString(R.string.text_factory_tp_start));
        tvTP.setTextColor(getResources().getColor(R.color.black2));
    }

    private void setTpOkContent() {
        tvTP.setText(getResources().getString(R.string.text_factory_tp_ok));
        tvTP.setTextColor(getResources().getColor(R.color.black2));
    }

    private void setTpFailContent() {
        tvTP.setText(getResources().getString(R.string.text_factory_tp_faild));
        tvTP.setTextColor(getResources().getColor(R.color.red));
    }

    private void checkTpState() throws RemoteException {
        //        String mFridgeModel = mIService.getSystemModel();//FridgeApplication.getInstance().getSpUtil().getFridgeMode();
        if (mMBParam.getOSType().contains("UG")) {
            // Start TP checking
            SystemCmdUtil.runCMD("echo 1 > /sys/devices/platform/mtk-tpd/calibrate");
            new Thread() {
                int[] times = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                String tpState = null;

                @Override
                public void run() {
                    super.run();
                    boolean isOK = false;
                    for (int i : times) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                        tpState = SystemCmdUtil.runCMD("cat /sys/devices/platform/mtk-tpd/calibrate");
                        if (tpState.indexOf("0xf 0xf") >= 0) {
                            isOK = true;
                            break;
                        }
                    }
                    if (isOK) {
                        mHandler.sendEmptyMessage(0x02); //更新TP校准成功界面
                    } else {
                        mHandler.sendEmptyMessage(0x03); //更新TP校准失败界面
                    }
                }
            }.start();
        }
    }

    private void setWifiContent() {
        int wifiState = DeviceUtil.getWifiState(getApplicationContext());
        if (wifiState == 3) {
            String ssidValue = DeviceUtil.getSSID(getApplicationContext());
            if (ssidValue != null) {
                int rssiValue = DeviceUtil.getRSSI(getApplicationContext());
                int rssiLevel = 0;
                if (rssiValue > -55) {
                    rssiLevel = 5;
                } else if (rssiValue > -70 && rssiValue <= -50) {
                    rssiLevel = 4;
                } else if (rssiValue > -85 && rssiValue <= -70) {
                    rssiLevel = 3;
                } else if (rssiValue > -100 && rssiValue <= -80) {
                    rssiLevel = 2;
                } else if (rssiValue <= -100) {
                    rssiLevel = 1;
                }
                String showSsidValue = ssidValue.substring(1, ssidValue.length() - 1);
                tvWifi.setText("已连接\n" + "(" + showSsidValue + ")" + rssiLevel);
            } else {
                ScanResult listWifi = (ScanResult) DeviceUtil.getSSIDList(getApplicationContext()).get(0);
                ssidValue = listWifi.SSID;
                int rssiValue = listWifi.level;
                int rssiLevel = 0;
                if (rssiValue > -55) {
                    rssiLevel = 5;
                } else if (rssiValue > -70 && rssiValue <= -50) {
                    rssiLevel = 4;
                } else if (rssiValue > -85 && rssiValue <= -70) {
                    rssiLevel = 3;
                } else if (rssiValue > -100 && rssiValue <= -80) {
                    rssiLevel = 2;
                } else if (rssiValue <= -100) {
                    rssiLevel = 1;
                }
                tvWifi.setText("未连接\n" + "(" + ssidValue + ")" + rssiLevel);
            }
        } else {
            tvWifi.setText("wifi未打开");
        }

    }

    private void setPirContent() {
        if (DeviceUtil.getPirUG() == 1) {
            tvPir.setText(getResources().getString(R.string.text_factory_has_person));
        } else {
            tvPir.setText(getResources().getString(R.string.text_factory_no_person));
        }
    }

    private void createAudio() {
        if (audioUtil == null) {
            audioUtil = new FactoryAudioUtil(btnRecord, btnPlayAll, btnPlayLeft, btnPlayRight,
                    tvRecord, tvPlayAll, tvPlayLeft, tvPlayRight,
                    prbRecord, prbPlayAll, prbPlayLeft, prbPlayRight);
        }
    }

    private void deleteAudio() {
        if (audioUtil != null) {
            audioUtil.deleteAudioDir();
            audioUtil = null;
        }
    }

    private void popResetPassWin() {
        final PopWindowNormalInput normalInput = new PopWindowNormalInput(this,"密码","提醒！恢复出厂设置后，本地账户信息将被清除。","请输入恢复出厂设置密码");
        normalInput.showDialog();
        normalInput.setPopListener(new PopInputListener() {
            @Override
            public void onOkClick(String content) {
                if(content.equals(PASSWORD)){
                    normalInput.dismiss();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            SystemClock.sleep(1000);

                            SystemCmdUtil.runCMD("pm clear " + getPackageName());
                        }
                    }.start();
                }else {
                    normalInput.setContentText("密码错误！请重新输入。");
                    normalInput.setmEdittext("");
                }
            }

            @Override
            public void onCancelClick() {

            }
        });
    }


}

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

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.haiersmart.imagerecognition.CaptureImageFromUsbCamera;
import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.draw.MyMarketButton;
import com.haiersmart.sfcontrol.draw.MyTestAudioButton;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.service.MainBoardParameters;
import com.haiersmart.sfcontrol.service.powerctl.PowerSerialOpt;
import com.haiersmart.sfcontrol.utilslib.DeviceUtil;
import com.haiersmart.sfcontrol.utilslib.FactoryAudioUtil;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.SystemCmdUtil;

import java.io.IOException;
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
    private final static int REQUESTCODE = 1; // RFID Verify返回的结果码

    private MainBoardParameters mMBParam;
    private PowerSerialOpt mPowerSerialOpt;
    private String mFridgeModel, mTftVersion, mOsVersion, mFridgeType;
    RadioButton rbtVersion, rbtReset, rbtStatus, rbtCamera, rbtTP, rbtAudio, rbtMarket, rbtDebug, mRBtnRFID;
    LinearLayout llVersion, llReset, llStatus, llCamera, llTP, llAudio, llMarket, llDebug;
    LinearLayout llEnvTemp, llEnvHum, llFridge, llFreeze, llChange, llDefrostSensor, llColdFan,
            llFridgeDefrost, llFridgeFan, llFridgeDefrostSensor,
            llFreezeDefrost, llFreezeFan, llFreezeDefrostSensor,
            llChangeDefrost, llChangeFan, llChangeDefrostSensor,
            llDrySensor;
    LinearLayout llFridgeDoor, llFreezeDoor, llChangeDoor, llInsideDoor, llFridgeLeftDoor, llFridgeRightDoor;
    LinearLayout llEnvRealTemp, llEnvRealHum, llFridgeReal, llFreezeReal, llChangeReal,
            llFreezeDefrostTemp, llFridgeDefrostTemp, llChangeDefrostTemp, llDryAreaRealTemp;
    LinearLayout llFreezeFanVol, llPressOneFreq, llFridgeAirDoor, llChangeAirDoor, llDefrostHeater,
            llChangeHeater, llFridgeLight, llVerticalBridgeHeater, llFreezeLight, llHandleLight,
            llTestMode, llFridgeFanVol, llChangeFanVol, llColdFanVol, llAirDoor, llAirDoorValve,
            llFridgeHeater, llFreezeHeater, llAirDoorHeater, llDoorBorderHeater,
            llFridgeTopLight, llFridgeBackLight, llChangeLight;
    private LinearLayout tft_rfid_linearLayout;
    TextView tvEnvTemp, tvEnvHum, tvFridge, tvFreeze, tvChange, tvDefrostSensor, tvColdFan,
            tvFridgeDefrost, tvFridgeFan, tvFridgeDefrostSensor,
            tvFreezeDefrost, tvFreezeFan, tvFreezeDefrostSensor,
            tvChangeDefrost, tvChangeFan, tvChangeDefrostSensor,
            tvDrySensor;
    TextView tvFridgeDoor, tvCommunication, tvPir, tvWifi, tvFreezeDoor, tvChangeDoor, tvInsideDoor,
            tvFridgeLeftDoor, tvFridgeRightDoor;
    TextView tvFridgeModel, tvTftVersion, tvOsVersion, tvMac, tvTP;
    TextView tvTftVersionTitle, tvOsVersionTitle, tvMacTitle;
    TextView tvEnvRealTemp, tvEnvRealHum, tvFridgeReal, tvFreezeReal, tvChangeReal,
            tvFreezeDefrostTemp, tvFridgeDefrostTemp, tvChangeDefrostTemp, tvDryAreaRealTemp;
    TextView tvFreezeFanVol, tvPressOneFreq, tvFridgeAirDoor, tvChangeAirDoor, tvDefrostHeater,
            tvChangeHeater, tvFridgeLight, tvVerticalBridgeHeater, tvFreezeLight, tvHandleLight,
            tvFridgeFanVol, tvChangeFanVol, tvColdFanVol, tvAirDoor, tvAirDoorValve,
            tvFridgeHeater, tvFreezeHeater, tvAirDoorHeater, tvDoorBorderHeater,
            tvFridgeTopLight, tvFridgeBackLight, tvChangeLight;
    Button btnReturn, btnResetEnter, btnTestMode, btnMipiCamera, mBtnTftRFID,
            btnUsbCamera1, btnUsbCamera2, btnUsbCamera3, btnUsbCamera4, btnUsbCamera5;
    TextView tvReset;
    TextView tft_rfid_result;
    MyMarketButton btnMarket;
    MyTestAudioButton btnRecord, btnPlayAll, btnPlayLeft, btnPlayRight;
    TextView tvRecord, tvPlayAll, tvPlayLeft, tvPlayRight;
    ProgressBar prbRecord, prbPlayAll, prbPlayLeft, prbPlayRight;
    FactoryAudioUtil audioUtil;
    CheckBox chbMmarket;


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
        try {
            mPowerSerialOpt = PowerSerialOpt.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMBParam = MainBoardParameters.getInstance();
        mFridgeModel = mMBParam.getFridgeType();
        mFridgeType = this.getIntent().getStringExtra("model");
        mTftVersion = this.getIntent().getStringExtra("version");
        MyLogUtil.i(TAG, "model mFridgeType:" + mFridgeType + " mFridgeModel:" + mFridgeModel + " mTftVersion:" + mTftVersion);
        if (mFridgeType == null || mFridgeType == "") {
            mFridgeType = mFridgeModel;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLogUtil.d(TAG, "onActivityResult ！");
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                boolean res = false;
                String rfidRes = data.getStringExtra("rfidResult");
                tft_rfid_result.setText(rfidRes);
                setLinearContent(R.id.gbtn_rfid_verify);
            }
        }
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
        mRBtnRFID = (RadioButton) findViewById(R.id.gbtn_rfid_verify);

        rbtVersion.setOnClickListener(this);
        rbtReset.setOnClickListener(this);
        rbtStatus.setOnClickListener(this);
        rbtCamera.setOnClickListener(this);
        rbtTP.setOnClickListener(this);
        rbtAudio.setOnClickListener(this);
        rbtMarket.setOnClickListener(this);
        rbtDebug.setOnClickListener(this);
        mRBtnRFID.setOnClickListener(this);

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
        llFreezeDoor = (LinearLayout) findViewById(R.id.linear_factory_freeze_door);
        llChangeDoor = (LinearLayout) findViewById(R.id.linear_factory_change_door);
        llInsideDoor = (LinearLayout) findViewById(R.id.linear_factory_inside_door);
        llFridgeLeftDoor = (LinearLayout) findViewById(R.id.linear_factory_fridge_left_door);
        llFridgeRightDoor = (LinearLayout) findViewById(R.id.linear_factory_fridge_right_door);
        llFridgeDefrostSensor = (LinearLayout) findViewById(R.id.linear_factory_fridge_defrost_sensor);
        llFridgeDefrost = (LinearLayout) findViewById(R.id.linear_factory_fridge_defrost);
        llFridgeFan = (LinearLayout) findViewById(R.id.linear_factory_fridge_fan);
        llChangeDefrostSensor = (LinearLayout) findViewById(R.id.linear_factory_change_defrost_sensor);
        llChangeDefrost = (LinearLayout) findViewById(R.id.linear_factory_change_defrost);
        llChangeFan = (LinearLayout) findViewById(R.id.linear_factory_change_fan);
        llColdFan = (LinearLayout) findViewById(R.id.linear_factory_cold_fan);
        llDrySensor = (LinearLayout) findViewById(R.id.linear_factory_dry_sensor);

        llEnvRealTemp = (LinearLayout) findViewById(R.id.linear_factory_env_real_temp);
        llEnvRealHum = (LinearLayout) findViewById(R.id.linear_factory_env_real_hum);
        llFridgeReal = (LinearLayout) findViewById(R.id.linear_factory_fridge_real_temp);
        llFreezeReal = (LinearLayout) findViewById(R.id.linear_factory_freeze_real_temp);
        llChangeReal = (LinearLayout) findViewById(R.id.linear_factory_change_real_temp);
        llFridgeDefrostTemp = (LinearLayout) findViewById(R.id.linear_factory_fridge_defrost_real_temp);
        llFreezeDefrostTemp = (LinearLayout) findViewById(R.id.linear_factory_freeze_defrost_real_temp);
        llChangeDefrostTemp = (LinearLayout) findViewById(R.id.linear_factory_change_defrost_real_temp);
        llDryAreaRealTemp = (LinearLayout) findViewById(R.id.linear_factory_dry_area_real_temp);
        llFridgeFanVol = (LinearLayout) findViewById(R.id.linear_factory_fridge_fan_voltage);
        llFreezeFanVol = (LinearLayout) findViewById(R.id.linear_factory_freeze_fan_voltage);
        llChangeFanVol = (LinearLayout) findViewById(R.id.linear_factory_change_fan_voltage);
        llColdFanVol = (LinearLayout) findViewById(R.id.linear_factory_cold_fan_voltage);
        llPressOneFreq = (LinearLayout) findViewById(R.id.linear_factory_press_one_freq);
        llFridgeAirDoor = (LinearLayout) findViewById(R.id.linear_factory_fridge_air_door);
        llChangeAirDoor = (LinearLayout) findViewById(R.id.linear_factory_change_air_door);
        llAirDoor = (LinearLayout) findViewById(R.id.linear_factory_air_door);
        llAirDoorValve = (LinearLayout) findViewById(R.id.linear_factory_air_door_valve);
        llDefrostHeater = (LinearLayout) findViewById(R.id.linear_factory_defrost_heater);
        llFridgeHeater = (LinearLayout) findViewById(R.id.linear_factory_fridge_heater);
        llFreezeHeater = (LinearLayout) findViewById(R.id.linear_factory_freeze_heater);
        llChangeHeater = (LinearLayout) findViewById(R.id.linear_factory_change_heater);
        llAirDoorHeater = (LinearLayout) findViewById(R.id.linear_factory_air_door_heater);
        llDoorBorderHeater = (LinearLayout) findViewById(R.id.linear_factory_door_border_heater);
        llVerticalBridgeHeater = (LinearLayout) findViewById(R.id.linear_factory_verticalBridge_heater);
        llFridgeLight = (LinearLayout) findViewById(R.id.linear_factory_fridge_light);
        llFreezeLight = (LinearLayout) findViewById(R.id.linear_factory_freeze_light);
        llHandleLight = (LinearLayout) findViewById(R.id.linear_factory_handle_light);
        llChangeLight = (LinearLayout) findViewById(R.id.linear_factory_change_light);
        llFridgeTopLight = (LinearLayout) findViewById(R.id.linear_factory_fridge_top_light);
        llFridgeBackLight = (LinearLayout) findViewById(R.id.linear_factory_fridge_back_light);
        llTestMode = (LinearLayout) findViewById(R.id.linear_factory_test_mode);


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
        tvFreezeDefrostSensor = (TextView) findViewById(R.id.text_factory_freeze_defrost_sensor);
        tvFreezeDoor = (TextView) findViewById(R.id.text_factory_freeze_door);
        tvChangeDoor = (TextView) findViewById(R.id.text_factory_change_door);
        tvInsideDoor = (TextView) findViewById(R.id.text_factory_inside_door);
        tvFridgeLeftDoor = (TextView) findViewById(R.id.text_factory_fridge_left_door);
        tvFridgeRightDoor = (TextView) findViewById(R.id.text_factory_fridge_right_door);
        tvFridgeDefrostSensor = (TextView) findViewById(R.id.text_factory_fridge_defrost_sensor);
        tvFridgeDefrost = (TextView) findViewById(R.id.text_factory_fridge_defrost);
        tvFridgeFan = (TextView) findViewById(R.id.text_factory_fridge_fan);
        tvChangeDefrostSensor = (TextView) findViewById(R.id.text_factory_change_defrost_sensor);
        tvChangeDefrost = (TextView) findViewById(R.id.text_factory_change_defrost);
        tvChangeFan = (TextView) findViewById(R.id.text_factory_change_fan);
        tvColdFan = (TextView) findViewById(R.id.text_factory_cold_fan);
        tvDrySensor = (TextView) findViewById(R.id.text_factory_dry_sensor);

        tvEnvRealTemp = (TextView) findViewById(R.id.text_factory_env_real_temp);
        tvEnvRealHum = (TextView) findViewById(R.id.text_factory_env_real_hum);
        tvFridgeReal = (TextView) findViewById(R.id.text_factory_fridge_real_temp);
        tvFreezeReal = (TextView) findViewById(R.id.text_factory_freeze_real_temp);
        tvChangeReal = (TextView) findViewById(R.id.text_factory_change_real_temp);
        tvFridgeDefrostTemp = (TextView) findViewById(R.id.text_factory_fridge_defrost_real_temp);
        tvFreezeDefrostTemp = (TextView) findViewById(R.id.text_factory_freeze_defrost_real_temp);
        tvChangeDefrostTemp = (TextView) findViewById(R.id.text_factory_change_defrost_real_temp);
        tvDryAreaRealTemp = (TextView) findViewById(R.id.text_factory_dry_area_real_temp);
        tvFreezeFanVol = (TextView) findViewById(R.id.text_factory_freeze_fan_voltage);
        tvPressOneFreq = (TextView) findViewById(R.id.text_factory_press_one_freq);
        tvFridgeAirDoor = (TextView) findViewById(R.id.text_factory_fridge_air_door);
        tvChangeAirDoor = (TextView) findViewById(R.id.text_factory_change_air_door);
        tvDefrostHeater = (TextView) findViewById(R.id.text_factory_defrost_heater);
        tvChangeHeater = (TextView) findViewById(R.id.text_factory_change_heater);
        tvFridgeLight = (TextView) findViewById(R.id.text_factory_fridge_light);
        tvVerticalBridgeHeater = (TextView) findViewById(R.id.text_factory_verticalBridge_heater);
        tvFreezeLight = (TextView) findViewById(R.id.text_factory_freeze_light);
        tvHandleLight = (TextView) findViewById(R.id.text_factory_handle_light);
        tvFridgeFanVol = (TextView) findViewById(R.id.text_factory_fridge_fan_voltage);
        tvChangeFanVol = (TextView) findViewById(R.id.text_factory_change_fan_voltage);
        tvColdFanVol = (TextView) findViewById(R.id.text_factory_cold_fan_voltage);
        tvAirDoor = (TextView) findViewById(R.id.text_factory_air_door);
        tvAirDoorValve = (TextView) findViewById(R.id.text_factory_air_door_valve);
        tvFridgeHeater = (TextView) findViewById(R.id.text_factory_fridge_heater);
        tvFreezeHeater = (TextView) findViewById(R.id.text_factory_freeze_heater);
        tvAirDoorHeater = (TextView) findViewById(R.id.text_factory_air_door_heater);
        tvDoorBorderHeater = (TextView) findViewById(R.id.text_factory_door_border_heater);
        tvFridgeTopLight = (TextView) findViewById(R.id.text_factory_fridge_top_light);
        tvFridgeBackLight = (TextView) findViewById(R.id.text_factory_fridge_back_light);
        tvChangeLight = (TextView) findViewById(R.id.text_factory_change_light);

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

        tvReset = (TextView) findViewById(R.id.title_factory_reset);

        btnReturn = (Button) findViewById(R.id.btn_factory_return);
        btnReturn.setOnClickListener(this);
        btnResetEnter = (Button) findViewById(R.id.btn_factory_reset);
        btnResetEnter.setOnClickListener(this);
        btnMarket = (MyMarketButton) findViewById(R.id.btn_factory_market);
        btnMarket.setOnClickListener(this);
        btnTestMode = (Button) findViewById(R.id.btn_factory_test_mode);
        btnTestMode.setOnClickListener(this);
        btnMipiCamera = (Button) findViewById(R.id.btn_factory_mipi_camera);
        btnMipiCamera.setOnClickListener(this);
        btnUsbCamera1 = (Button) findViewById(R.id.btn_factory_usb1_camera);
        btnUsbCamera1.setOnClickListener(this);
        btnUsbCamera2 = (Button) findViewById(R.id.btn_factory_usb2_camera);
        btnUsbCamera2.setOnClickListener(this);
        btnUsbCamera3 = (Button) findViewById(R.id.btn_factory_usb3_camera);
        btnUsbCamera3.setOnClickListener(this);
        btnUsbCamera4 = (Button) findViewById(R.id.btn_factory_usb4_camera);
        btnUsbCamera4.setOnClickListener(this);
        btnUsbCamera5 = (Button) findViewById(R.id.btn_factory_usb5_camera);
        btnUsbCamera5.setOnClickListener(this);

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

        tft_rfid_linearLayout = (LinearLayout) findViewById(R.id.tft_rfid_linearLayout);
        mBtnTftRFID = (Button) findViewById(R.id.btn_rfid_verify);
        mBtnTftRFID.setOnClickListener(this);
        tft_rfid_result = (TextView) findViewById(R.id.tft_rfid_result);
    }

    private void initViews() {
        setLinearContent(R.id.rbt_factory_version);//
        tvFridgeModel.setText(mFridgeType);
        if (mFridgeType.equals(ConstantUtil.BCD251_MODEL)) {
            //            tvFridgeModel.setText(mFridgeModel);
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
            mRBtnRFID.setVisibility(View.GONE);
        } else if (mFridgeType.equals(ConstantUtil.BCD401_MODEL)) {
            tvFridgeModel.setText(mFridgeType + "/" + mFridgeType + "(S)");
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
            mRBtnRFID.setVisibility(View.GONE);
        } else if (mFridgeType.equals(ConstantUtil.BCD256_MODEL)) {
            tvFridgeModel.setText(mFridgeType + "/" + mFridgeType + "(S)");
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
            mRBtnRFID.setVisibility(View.GONE);
        } else if (mFridgeType.equals(ConstantUtil.BCD475_MODEL)) {
            tvFridgeModel.setText(mFridgeType + "/" + mFridgeType + "(Z)");
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.VISIBLE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.VISIBLE);
            rbtDebug.setVisibility(View.VISIBLE);
            mRBtnRFID.setVisibility(View.GONE);
        } else if (mFridgeType.equals(ConstantUtil.BCD476_MODEL)) {
            //            tvFridgeModel.setText(mFridgeModel);
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.VISIBLE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.VISIBLE);
            rbtDebug.setVisibility(View.VISIBLE);
            mRBtnRFID.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD476_RFID_MODEL)) {
            //            tvFridgeModel.setText(mFridgeModel);
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.VISIBLE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.VISIBLE);
            rbtDebug.setVisibility(View.VISIBLE);
            mRBtnRFID.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD658_MODEL)) {
            //            tvFridgeModel.setText(mFridgeModel);
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.VISIBLE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.VISIBLE);
            rbtDebug.setVisibility(View.VISIBLE);
            mRBtnRFID.setVisibility(View.GONE);
        }
        initStatusView();
        initDebugView();
    }

    private void initStatusView() {
        if (mFridgeType.equals(ConstantUtil.BCD251_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llChange.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD401_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llFreezeDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD256_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llChange.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD475_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llEnvHum.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
            llFreezeDoor.setVisibility(View.VISIBLE);
            btnUsbCamera1.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD476_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llEnvHum.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
            llFreezeDoor.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD476_RFID_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llEnvHum.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDoor.setVisibility(View.VISIBLE);
            llFreezeDoor.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD658_MODEL)) {
            llEnvTemp.setVisibility(View.VISIBLE);
            llEnvHum.setVisibility(View.VISIBLE);
            llFridge.setVisibility(View.VISIBLE);
            llFreeze.setVisibility(View.VISIBLE);
            llChange.setVisibility(View.VISIBLE);
            llFreezeDefrostSensor.setVisibility(View.VISIBLE);
            llFreezeDefrost.setVisibility(View.VISIBLE);
            llFreezeFan.setVisibility(View.VISIBLE);
            llFridgeDefrostSensor.setVisibility(View.VISIBLE);
            llFridgeDefrost.setVisibility(View.VISIBLE);
            llFridgeFan.setVisibility(View.VISIBLE);
            llChangeDefrostSensor.setVisibility(View.VISIBLE);
            llChangeDefrost.setVisibility(View.VISIBLE);
            llChangeFan.setVisibility(View.VISIBLE);
            llColdFan.setVisibility(View.VISIBLE);
            llDrySensor.setVisibility(View.VISIBLE);
            llFridgeLeftDoor.setVisibility(View.VISIBLE);
            llFridgeRightDoor.setVisibility(View.VISIBLE);
            llFreezeDoor.setVisibility(View.VISIBLE);
            llChangeDoor.setVisibility(View.VISIBLE);
            llInsideDoor.setVisibility(View.VISIBLE);
        }
    }

    private void initDebugView() {
        if (mFridgeType.equals(ConstantUtil.BCD476_MODEL)) {
            llEnvRealTemp.setVisibility(View.VISIBLE);
            llEnvRealHum.setVisibility(View.VISIBLE);
            llFridgeReal.setVisibility(View.VISIBLE);
            llFreezeReal.setVisibility(View.VISIBLE);
            llFreezeDefrostTemp.setVisibility(View.VISIBLE);

            llFreezeFanVol.setVisibility(View.VISIBLE);
            llPressOneFreq.setVisibility(View.VISIBLE);
            llFridgeAirDoor.setVisibility(View.VISIBLE);

            llDefrostHeater.setVisibility(View.VISIBLE);
            llVerticalBridgeHeater.setVisibility(View.VISIBLE);

            llFridgeLight.setVisibility(View.VISIBLE);
            llFreezeLight.setVisibility(View.VISIBLE);
            llHandleLight.setVisibility(View.VISIBLE);
            llTestMode.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD476_RFID_MODEL)) {
            llEnvRealTemp.setVisibility(View.VISIBLE);
            llEnvRealHum.setVisibility(View.VISIBLE);
            llFridgeReal.setVisibility(View.VISIBLE);
            llFreezeReal.setVisibility(View.VISIBLE);
            llFreezeDefrostTemp.setVisibility(View.VISIBLE);

            llFreezeFanVol.setVisibility(View.VISIBLE);
            llPressOneFreq.setVisibility(View.VISIBLE);
            llFridgeAirDoor.setVisibility(View.VISIBLE);

            llDefrostHeater.setVisibility(View.VISIBLE);
            llVerticalBridgeHeater.setVisibility(View.VISIBLE);

            llFridgeLight.setVisibility(View.VISIBLE);
            llFreezeLight.setVisibility(View.VISIBLE);
            llHandleLight.setVisibility(View.VISIBLE);
            llTestMode.setVisibility(View.VISIBLE);
        } else if (mFridgeType.equals(ConstantUtil.BCD658_MODEL)) {
            llEnvRealTemp.setVisibility(View.VISIBLE);
            llEnvRealHum.setVisibility(View.VISIBLE);
            llFridgeReal.setVisibility(View.VISIBLE);
            llFreezeReal.setVisibility(View.VISIBLE);
            llChangeReal.setVisibility(View.VISIBLE);
            llFridgeDefrostTemp.setVisibility(View.VISIBLE);
            llFreezeDefrostTemp.setVisibility(View.VISIBLE);
            llChangeDefrostTemp.setVisibility(View.VISIBLE);
            llDryAreaRealTemp.setVisibility(View.VISIBLE);

            llFridgeFanVol.setVisibility(View.VISIBLE);
            llFreezeFanVol.setVisibility(View.VISIBLE);
            llChangeFanVol.setVisibility(View.VISIBLE);
            llColdFanVol.setVisibility(View.VISIBLE);
            llPressOneFreq.setVisibility(View.VISIBLE);

            llAirDoor.setVisibility(View.VISIBLE);
            llAirDoorValve.setVisibility(View.VISIBLE);

            llFridgeHeater.setVisibility(View.VISIBLE);
            llFreezeHeater.setVisibility(View.VISIBLE);
            llChangeHeater.setVisibility(View.VISIBLE);
            llAirDoorHeater.setVisibility(View.VISIBLE);
            llVerticalBridgeHeater.setVisibility(View.VISIBLE);
            llDoorBorderHeater.setVisibility(View.VISIBLE);

            llFridgeTopLight.setVisibility(View.VISIBLE);
            llFridgeBackLight.setVisibility(View.VISIBLE);
            llFreezeLight.setVisibility(View.VISIBLE);
            llChangeLight.setVisibility(View.VISIBLE);
            llTestMode.setVisibility(View.VISIBLE);
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
                tvReset.setText("正在恢复出厂，请稍等！自动回到激活页面......");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //                            SystemClock.sleep(1000);
                        SystemCmdUtil.RootCCTCommand("rm -rf /sdcard/video*.jpg");//清除工厂模式的照片
                        SystemCmdUtil.RootCCTCommand("rm -rf /sdcard/*.png");//清除截图照片
                        SystemCmdUtil.RootCCTCommand("rm -rf /sdcard/haier");//
                        SystemCmdUtil.RootCCTCommand("rm -rf /sdcard/Android");//
                        SystemCmdUtil.RootCCTCommand("pm clear com.haiersmart.web");//
                        SystemCmdUtil.RootCCTCommand("pm clear com.haiersmart.shop");//
                        SystemCmdUtil.RootCCTCommand("pm clear com.haiersmart.user");//
                        SystemCmdUtil.RootCCTCommand("pm clear com.haiersmart.sfnation");//
                        DeviceUtil.removeWifiNetwork(getApplicationContext());//
                        resetFridgeControl();//
                    }
                }.start();
                //                popResetPassWin();
                break;
            case R.id.btn_factory_market:
                if (btnMarket.isPress()) {
                    mPowerSerialOpt.sendCmdById(EnumBaseName.marketDemo, 0);
                    btnMarket.setOff();
                } else {
                    mPowerSerialOpt.sendCmdById(EnumBaseName.marketDemo, 1);
                    btnMarket.setOn();
                }
                break;
            case R.id.btn_factory_test_mode:
                int testMode = mMBParam.getMbdValueByName(EnumBaseName.testMode.name());
                mPowerSerialOpt.sendCmdById(EnumBaseName.testMode, testMode + 1);
                break;
            case R.id.btn_factory_mipi_camera:
                startActivity(new Intent(this, CameraMipiActivity.class));
                break;
            case R.id.btn_factory_usb1_camera:
                startActivity(new Intent(this, CameraUsbActivity.class));
                break;
            case R.id.btn_factory_usb2_camera:
                startActivity(new Intent(this, CameraUsbActivity.class));
                break;
            case R.id.btn_rfid_verify:
                //                Intent rfidIntent = new Intent(TftActivity.this, RFIDVerifyActivity.class);
                Intent rfidIntent = new Intent(Intent.ACTION_MAIN);
                rfidIntent.setComponent(new ComponentName(
                        "com.haiersmart.sfnation",
                        "com.haiersmart.sfnation.ui.rfid.RFIDVerifyActivity"));
                String rfidRes = "验证结果：";
                rfidIntent.putExtra("rfidResult", rfidRes);
                startActivityForResult(rfidIntent, REQUESTCODE);
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
        tft_rfid_linearLayout.setVisibility(View.GONE);
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
                llCamera.setVisibility(View.VISIBLE);
                setUsbCameraButtun();
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
                btnMarket.setOff();
                boolean isMarket = mMBParam.getMbsValueByName(EnumBaseName.marketDemo.name()) == 1;
                if (isMarket) {
                    btnMarket.setOn();
                }
                break;
            case R.id.rbt_factory_debug:
                rbtDebug.setChecked(true);
                mPowerSerialOpt.sendCmdById(EnumBaseName.getDebug);
                startDebugTimer();
                llDebug.setVisibility(View.VISIBLE);
                break;
            case R.id.gbtn_rfid_verify:
                mRBtnRFID.setChecked(true);
                tft_rfid_linearLayout.setVisibility(View.VISIBLE);
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

    private void startDebugTimer() {
        MyLogUtil.i(TAG, "startTimer");
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0x04);
                }
            };
        }

        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, 1000, 1000);
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
                case 0x04:
                    mPowerSerialOpt.sendCmdById(EnumBaseName.getDebug);
                    refreshDebugUI();
                    break;
            }
        }

    };

    private void refreshStatusUI() {
        boolean isCommunSuccess = mMBParam.getMbsValueByName(EnumBaseName.communicationOverTime.toString()) == 0 ? true : false;
        if (isCommunSuccess) {
            if (mFridgeModel.equals(ConstantUtil.BCD401_MODEL)) {
                if (mMBParam.getMbsValueByName(EnumBaseName.envTempSensorErr.toString()) == 0) {
                    tvEnvTemp.setText((float) mMBParam.getMbsValueByName(EnumBaseName.envRealTemp.toString()) / 10 + "℃");
                    tvEnvTemp.setTextColor(getResources().getColor(R.color.black2));
                } else {
                    tvEnvTemp.setText(getResources().getString(R.string.text_factory_error));
                    tvEnvTemp.setTextColor(getResources().getColor(R.color.red));
                }
            } else {
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
            if (mMBParam.getMbsValueByName(EnumBaseName.freezeDoorStatus.toString()) == 0) {
                tvFreezeDoor.setText(getResources().getString(R.string.text_door_close));
                tvFreezeDoor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFreezeDoor.setText(getResources().getString(R.string.text_door_open));
                tvFreezeDoor.setTextColor(getResources().getColor(R.color.black2));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.changeDoorStatus.toString()) == 0) {
                tvChangeDoor.setText(getResources().getString(R.string.text_door_close));
                tvChangeDoor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvChangeDoor.setText(getResources().getString(R.string.text_door_open));
                tvChangeDoor.setTextColor(getResources().getColor(R.color.black2));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.insideDoorStatus.toString()) == 0) {
                tvInsideDoor.setText(getResources().getString(R.string.text_door_close));
                tvInsideDoor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvInsideDoor.setText(getResources().getString(R.string.text_door_open));
                tvInsideDoor.setTextColor(getResources().getColor(R.color.black2));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeDoorStatus.toString()) == 0) {
                tvFridgeLeftDoor.setText(getResources().getString(R.string.text_door_close));
                tvFridgeLeftDoor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridgeLeftDoor.setText(getResources().getString(R.string.text_door_open));
                tvFridgeLeftDoor.setTextColor(getResources().getColor(R.color.black2));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeRightDoorStatus.toString()) == 0) {
                tvFridgeRightDoor.setText(getResources().getString(R.string.text_door_close));
                tvFridgeRightDoor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridgeRightDoor.setText(getResources().getString(R.string.text_door_open));
                tvFridgeRightDoor.setTextColor(getResources().getColor(R.color.black2));
            }

            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeDefrostSensorErr.toString()) == 0) {
                tvFridgeDefrostSensor.setText(getResources().getString(R.string.text_factory_normal));
                tvFridgeDefrostSensor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridgeDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
                tvFridgeDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeDefrostErr.toString()) == 0) {
                tvFridgeDefrost.setText(getResources().getString(R.string.text_factory_normal));
                tvFridgeDefrost.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridgeDefrost.setText(getResources().getString(R.string.text_factory_error));
                tvFridgeDefrost.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.fridgeFanErr.name()) == 0) {
                tvFridgeFan.setText(getResources().getString(R.string.text_factory_normal));
                tvFridgeFan.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvFridgeFan.setText(getResources().getString(R.string.text_factory_error));
                tvFridgeFan.setTextColor(getResources().getColor(R.color.red));
            }

            if (mMBParam.getMbsValueByName(EnumBaseName.changeDefrostSensorErr.toString()) == 0) {
                tvChangeDefrostSensor.setText(getResources().getString(R.string.text_factory_normal));
                tvChangeDefrostSensor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvChangeDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
                tvChangeDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.changeDefrostErr.toString()) == 0) {
                tvChangeDefrost.setText(getResources().getString(R.string.text_factory_normal));
                tvChangeDefrost.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvChangeDefrost.setText(getResources().getString(R.string.text_factory_error));
                tvChangeDefrost.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.changeFanErr.name()) == 0) {
                tvChangeFan.setText(getResources().getString(R.string.text_factory_normal));
                tvChangeFan.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvChangeFan.setText(getResources().getString(R.string.text_factory_error));
                tvChangeFan.setTextColor(getResources().getColor(R.color.red));
            }

            if (mMBParam.getMbsValueByName(EnumBaseName.coldFanErr.name()) == 0) {
                tvColdFan.setText(getResources().getString(R.string.text_factory_normal));
                tvColdFan.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvColdFan.setText(getResources().getString(R.string.text_factory_error));
                tvColdFan.setTextColor(getResources().getColor(R.color.red));
            }
            if (mMBParam.getMbsValueByName(EnumBaseName.dryAreaSensorErr.name()) == 0) {
                tvDrySensor.setText(getResources().getString(R.string.text_factory_normal));
                tvDrySensor.setTextColor(getResources().getColor(R.color.black2));
            } else {
                tvDrySensor.setText(getResources().getString(R.string.text_factory_error));
                tvDrySensor.setTextColor(getResources().getColor(R.color.red));
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
            tvFridgeDoor.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeDoor.setTextColor(getResources().getColor(R.color.red));
            tvFreezeDoor.setText(getResources().getString(R.string.text_factory_error));
            tvFreezeDoor.setTextColor(getResources().getColor(R.color.red));
            tvChangeDoor.setText(getResources().getString(R.string.text_factory_error));
            tvChangeDoor.setTextColor(getResources().getColor(R.color.red));
            tvInsideDoor.setText(getResources().getString(R.string.text_factory_error));
            tvInsideDoor.setTextColor(getResources().getColor(R.color.red));
            tvFridgeLeftDoor.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeLeftDoor.setTextColor(getResources().getColor(R.color.red));
            tvFridgeRightDoor.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeRightDoor.setTextColor(getResources().getColor(R.color.red));
            tvCommunication.setText(getResources().getString(R.string.text_factory_error));
            tvCommunication.setTextColor(getResources().getColor(R.color.red));
            tvFridgeDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            tvFreezeDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
            tvFreezeDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            tvChangeDefrostSensor.setText(getResources().getString(R.string.text_factory_error));
            tvChangeDefrostSensor.setTextColor(getResources().getColor(R.color.red));
            tvFridgeDefrost.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeDefrost.setTextColor(getResources().getColor(R.color.red));
            tvFreezeDefrost.setText(getResources().getString(R.string.text_factory_error));
            tvFreezeDefrost.setTextColor(getResources().getColor(R.color.red));
            tvChangeDefrost.setText(getResources().getString(R.string.text_factory_error));
            tvChangeDefrost.setTextColor(getResources().getColor(R.color.red));
            tvFridgeFan.setText(getResources().getString(R.string.text_factory_error));
            tvFridgeFan.setTextColor(getResources().getColor(R.color.red));
            tvFreezeFan.setText(getResources().getString(R.string.text_factory_error));
            tvFreezeFan.setTextColor(getResources().getColor(R.color.red));
            tvChangeFan.setText(getResources().getString(R.string.text_factory_error));
            tvChangeFan.setTextColor(getResources().getColor(R.color.red));
            tvColdFan.setText(getResources().getString(R.string.text_factory_error));
            tvColdFan.setTextColor(getResources().getColor(R.color.red));
            tvDrySensor.setText(getResources().getString(R.string.text_factory_error));
            tvDrySensor.setTextColor(getResources().getColor(R.color.red));
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


    private void refreshDebugUI() {
        tvEnvRealTemp.setText((float) mMBParam.getMbdValueByName(EnumBaseName.envRealTemp.toString()) / 10 + "℃");
        tvFridgeReal.setText((float) mMBParam.getMbdValueByName(EnumBaseName.fridgeRealTemp.toString()) / 10 + "℃");
        tvFreezeReal.setText((float) mMBParam.getMbdValueByName(EnumBaseName.freezeRealTemp.toString()) / 10 + "℃");
        tvChangeReal.setText((float) mMBParam.getMbdValueByName(EnumBaseName.changeRealTemp.toString()) / 10 + "℃");
        tvFridgeDefrostTemp.setText((float) mMBParam.getMbdValueByName(EnumBaseName.fridgeDefrostRealTemp.toString()) / 10 + "℃");
        tvFreezeDefrostTemp.setText((float) mMBParam.getMbdValueByName(EnumBaseName.freezeDefrostRealTemp.toString()) / 10 + "℃");
        tvChangeDefrostTemp.setText((float) mMBParam.getMbdValueByName(EnumBaseName.changeDefrostRealTemp.toString()) / 10 + "℃");
        tvDryAreaRealTemp.setText((float) mMBParam.getMbdValueByName(EnumBaseName.dryAreaRealTemp.toString()) / 10 + "℃");
        tvEnvRealHum.setText(mMBParam.getMbdValueByName(EnumBaseName.envRealHum.toString()) + "%");

        tvFridgeFanVol.setText(mMBParam.getMbdValueByName(EnumBaseName.fridgeFanVoltage.toString()) + "V");
        tvFreezeFanVol.setText(mMBParam.getMbdValueByName(EnumBaseName.freezeFanVoltage.toString()) + "V");
        tvChangeFanVol.setText(mMBParam.getMbdValueByName(EnumBaseName.changeFanVoltage.toString()) + "V");
        tvColdFanVol.setText(mMBParam.getMbdValueByName(EnumBaseName.coldFanVoltage.toString()) + "V");
        if (mMBParam.getMbdValueByName(EnumBaseName.pressorOneFreq.toString()) == 0) {
            tvPressOneFreq.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvPressOneFreq.setText(mMBParam.getMbdValueByName(EnumBaseName.pressorOneFreq.toString()) + "");
        }

        if (mMBParam.getMbdValueByName(EnumBaseName.fridgeAirDoor.toString()) == 0) {
            tvFridgeAirDoor.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFridgeAirDoor.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.changeAirDoor.toString()) == 0) {
            tvChangeAirDoor.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvChangeAirDoor.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.airDoor.toString()) == 0) {
            tvAirDoor.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvAirDoor.setText(getResources().getString(R.string.text_door_open));
        }
        tvAirDoorValve.setText(mMBParam.getMbdValueByName(EnumBaseName.airDoorValve.toString()) + "");

        if (mMBParam.getMbdValueByName(EnumBaseName.defrostHeater.toString()) == 0) {
            tvDefrostHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvDefrostHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.fridgeHeater.toString()) == 0) {
            tvFridgeHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFridgeHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.freezeHeater.toString()) == 0) {
            tvFreezeHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFreezeHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.changeHeater.toString()) == 0) {
            tvChangeHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvChangeHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.airDoorHeater.toString()) == 0) {
            tvAirDoorHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvAirDoorHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.doorBorderHeater.toString()) == 0) {
            tvDoorBorderHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvDoorBorderHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.fridgeLight.toString()) == 0) {
            tvFridgeLight.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFridgeLight.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.verticalBridgeHeater.toString()) == 0) {
            tvVerticalBridgeHeater.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvVerticalBridgeHeater.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.freezeLight.toString()) == 0) {
            tvFreezeLight.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFreezeLight.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.handleLight.toString()) == 0) {
            tvHandleLight.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvHandleLight.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.fridgeTopLight.toString()) == 0) {
            tvFridgeTopLight.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFridgeTopLight.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.fridgeBackLight.toString()) == 0) {
            tvFridgeBackLight.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvFridgeBackLight.setText(getResources().getString(R.string.text_door_open));
        }
        if (mMBParam.getMbdValueByName(EnumBaseName.changeLight.toString()) == 0) {
            tvChangeLight.setText(getResources().getString(R.string.text_door_close));
        } else {
            tvChangeLight.setText(getResources().getString(R.string.text_door_open));
        }
        btnTestMode.setText(getResources().getString(R.string.title_factory_test_mode) + " " + mMBParam.getMbdValueByName(EnumBaseName.testMode.name()));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUsbCameraButtun() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String[] CameraIdList = null;
        try {
            CameraIdList = cameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (CameraIdList.length == 0) {
            btnMipiCamera.setText(getString(R.string.text_factory_mipi_camera_broken));
            btnMipiCamera.setEnabled(false);
            MyLogUtil.i("mCamera is null");
        } else {
            btnMipiCamera.setText(getString(R.string.text_factory_mipi_camera));
            btnMipiCamera.setEnabled(true);
            MyLogUtil.i("mCamera is exist");
        }

        if (CaptureImageFromUsbCamera.mCheckCameraExist()) {
            btnUsbCamera1.setText(getString(R.string.text_factory_usb_camera));
            btnUsbCamera1.setEnabled(true);
        } else {
            btnUsbCamera1.setText(getString(R.string.text_factory_usb_camera_none));
            btnUsbCamera1.setEnabled(false);
        }
    }

    private void removeWifiDate() {

    }

    private void resetFridgeControl() {
        sendCommandToService(this, ConstantUtil.MODE_HOLIDAY_OFF); //假日关
        sendCommandToService(this, ConstantUtil.MODE_FREEZE_OFF); //速冻关
        sendCommandToService(this, ConstantUtil.MODE_COLD_OFF); //速冷关
        sendCommandToService(this, ConstantUtil.MODE_TIDBIT_OFF); //珍品关
        sendCommandToService(this, ConstantUtil.MODE_PURIFY_OFF); //净化关
        sendTemperCmdToService(this, ConstantUtil.MODE_STERILIZE_ON, ConstantUtil.MODE_UV, 0); //杀菌模式
        sendCommandToService(this, ConstantUtil.REFRIGERATOR_OPEN); //冷藏开启（冷藏室风道开，可以控制冷藏室温度档位）
        sendCommandToService(this, ConstantUtil.MODE_SMART_ON); //智能开
        sendBroadcastToService(ConstantUtil.TEMPER_SETCOLD, ConstantUtil.KEY_SET_FRIDGE_LEVEL, 5);     //冷藏区温控
        sendBroadcastToService(ConstantUtil.TEMPER_SETCUSTOMAREA, ConstantUtil.KEY_SET_COLD_LEVEL, 0); //变温区温控
        sendBroadcastToService(ConstantUtil.TEMPER_SETFREEZE, ConstantUtil.KEY_SET_FREEZE_LEVEL, -18); //冷冻区温控
    }

    private void sendBroadcastToService(String action, String key, int value) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.COMMAND_TO_SERVICE);
        intent.putExtra(ConstantUtil.KEY_MODE, action);
        intent.putExtra(key, value);
        sendBroadcast(intent);
    }

    private void sendCommandToService(Context context, String action) {
        Intent intent = new Intent(context, ControlMainBoardService.class);
        intent.setAction(action);
        MyLogUtil.v(TAG, "sendCommandToService action=" + action);
        context.startService(intent);
    }

    private void sendTemperCmdToService(Context context, String action, String key, int temper) {
        Intent intent = new Intent(context, ControlMainBoardService.class);
        intent.setAction(action);
        intent.putExtra(key, temper);
        context.startService(intent);
    }
}

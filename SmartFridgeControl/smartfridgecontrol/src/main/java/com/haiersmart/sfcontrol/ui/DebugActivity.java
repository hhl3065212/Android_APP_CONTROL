package com.haiersmart.sfcontrol.ui;

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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haiersmart.sfcontrol.ControlService;
import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeInfoDbMgr;
import com.haiersmart.sfcontrol.database.FridgeInfoEntry;
import com.haiersmart.sfcontrol.draw.MyTestButton;
import com.haiersmart.sfcontrol.draw.PopInputListener;
import com.haiersmart.sfcontrol.draw.PopWindowNormalInput;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.service.MainBoardParameters;
import com.haiersmart.sfcontrol.service.configtable.TargetTempRange;
import com.haiersmart.sfcontrol.service.powerctl.SerialData;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD251_MODEL;
import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD256_MODEL;
import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD401_MODEL;
import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD476_MODEL;
import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD658_MODEL;

public class DebugActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DebugActivity";
    private ControlMainBoardService mControlService;
    private boolean mIsBound = false;
    private SerialData mSerialData;
    private MainBoardParameters mMainBoardParameters;
    private String mModel;
    private boolean isready = false;
    private TargetTempRange mTargetTempRange;
    private int mFridgeMin, mFridgeMax, mFreezeMin, mFreezeMax, mChangeMin, mChangeMax;

    private Timer mTimer;
    private TimerTask mWaitTask, mTimerTask;

    private LinearLayout lineEnvTemp, lineEnvHum, lineFridgeTemp, lineFreezeTemp, lineChangeTemp,
            lineFridgeTarget, lineFreezeTarget, lineChangeTarger;
    private TextView tvFridgeModel, tvStatusCode, tvEnvTemp, tvEnvHum, tvFridgeTemp, tvFreezeTemp, tvChangeTemp,
            tvFridgeTarget, tvFreezeTarget, tvChangeTarget;
    private TextView tvTitleStatusCode;
    private Button btnReturn;
    private MyTestButton btnSmart, btnHoliday, btnQuickCold, btnQuickFreeze, btnFridgeClose, btnFridgeDoor, btnPurify;
    private SeekBar skbFridge, skbFreeze, skbChange;

    private int onclickCounts = 0;
    private ControlService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        bindService(new Intent(this, ControlMainBoardService.class), conn, Context.BIND_AUTO_CREATE);
        mSerialData = SerialData.getInstance();
        mMainBoardParameters = MainBoardParameters.getInstance();
        findView();
        //        mTimer = new Timer();
        //        mTimer.schedule(mWaitTask,0,1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isready) {
            stopTimerTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isready) {
            startTimerTask();
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLogUtil.v(TAG, "ControlMainBoardService onServiceConnected");
            mIsBound = true;
            //            ControlMainBoardService.CmbBinder binder = (ControlMainBoardService.CmbBinder) service;
            //            mControlService = binder.getService();
            mService = ControlService.Stub.asInterface(service);
            startWaitTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
            MyLogUtil.v(TAG, "ControlMainBoardService onServiceDisconnected");
        }
    };

    private void findView() {
        lineEnvTemp = (LinearLayout) findViewById(R.id.linear_debug_env_temp);
        lineEnvHum = (LinearLayout) findViewById(R.id.linear_debug_env_hum);
        lineFridgeTemp = (LinearLayout) findViewById(R.id.linear_debug_fridge_temp);
        lineFreezeTemp = (LinearLayout) findViewById(R.id.linear_debug_freeze_temp);
        lineChangeTemp = (LinearLayout) findViewById(R.id.linear_debug_change_temp);
        lineFridgeTarget = (LinearLayout) findViewById(R.id.linear_debug_skb_fridge);
        lineFreezeTarget = (LinearLayout) findViewById(R.id.linear_debug_skb_freeze);
        lineChangeTarger = (LinearLayout) findViewById(R.id.linear_debug_skb_change);

        tvFridgeModel = (TextView) findViewById(R.id.text_debug_fridge_model);
        tvFridgeModel.setOnClickListener(this);
        tvStatusCode = (TextView) findViewById(R.id.text_debug_status_code);
        tvEnvTemp = (TextView) findViewById(R.id.text_debug_env_temp);
        tvEnvHum = (TextView) findViewById(R.id.text_debug_env_hum);
        tvFridgeTemp = (TextView) findViewById(R.id.text_debug_fridge_temp);
        tvFreezeTemp = (TextView) findViewById(R.id.text_debug_freeze_temp);
        tvChangeTemp = (TextView) findViewById(R.id.text_debug_change_temp);
        tvFridgeTarget = (TextView) findViewById(R.id.text_debug_fridge_target);
        tvFreezeTarget = (TextView) findViewById(R.id.text_debug_freeze_target);
        tvChangeTarget = (TextView) findViewById(R.id.text_debug_change_target);
        tvTitleStatusCode = (TextView) findViewById(R.id.title_debug_status_code);
        tvTitleStatusCode.setOnClickListener(this);


        btnReturn = (Button) findViewById(R.id.btn_debug_return);
        btnReturn.setOnClickListener(this);

        skbFridge = (SeekBar) findViewById(R.id.skb_debug_fridge);
        skbFreeze = (SeekBar) findViewById(R.id.skb_debug_freeze);
        skbChange = (SeekBar) findViewById(R.id.skb_debug_change);
        listenerSeekBar();

    }

    private void setView() throws RemoteException {
        initSeekBar();
        switch (mModel) {
            case BCD251_MODEL:
                lineEnvTemp.setVisibility(View.VISIBLE);
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineChangeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                lineChangeTarger.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_debug_top_left);
                initHoliday(R.id.btn_debug_top_right);
                initQuickCold(R.id.btn_debug_center_left);
                initQuickFreeze(R.id.btn_debug_center_right);
                initFridgeClose(R.id.btn_debug_bottom_left);
                break;
            case BCD256_MODEL:
                lineEnvTemp.setVisibility(View.VISIBLE);
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineChangeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                lineChangeTarger.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_debug_top_left);
                initHoliday(R.id.btn_debug_top_right);
                initQuickCold(R.id.btn_debug_center_left);
                initQuickFreeze(R.id.btn_debug_center_right);
                initFridgeClose(R.id.btn_debug_bottom_left);
                break;
            case BCD401_MODEL:
                lineEnvTemp.setVisibility(View.VISIBLE);
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_debug_top_left);
                initPurify(R.id.btn_debug_top_right);
                initQuickCold(R.id.btn_debug_center_left);
                initQuickFreeze(R.id.btn_debug_center_right);
                break;
            case BCD476_MODEL:
                lineEnvTemp.setVisibility(View.VISIBLE);
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_debug_top_left);
                initHoliday(R.id.btn_debug_top_right);
                initQuickCold(R.id.btn_debug_center_left);
                initQuickFreeze(R.id.btn_debug_center_right);
                break;
            case BCD658_MODEL:
                lineEnvTemp.setVisibility(View.VISIBLE);
                lineFridgeTemp.setVisibility(View.VISIBLE);
                lineFreezeTemp.setVisibility(View.VISIBLE);
                lineChangeTemp.setVisibility(View.VISIBLE);
                lineFridgeTarget.setVisibility(View.VISIBLE);
                lineFreezeTarget.setVisibility(View.VISIBLE);
                lineChangeTarger.setVisibility(View.VISIBLE);
                initSmart(R.id.btn_debug_top_left);
                initHoliday(R.id.btn_debug_top_right);
                initQuickCold(R.id.btn_debug_center_left);
                initQuickFreeze(R.id.btn_debug_center_right);
                initFridgeClose(R.id.btn_debug_bottom_left);
                initPir(R.id.btn_debug_bottom_right);
                break;
        }
    }

    private void initSeekBar() throws RemoteException {
        mTargetTempRange = mMainBoardParameters.getTargetTempRange();
        mFridgeMin = mTargetTempRange.getFridgeMinValue();
        mFreezeMin = mTargetTempRange.getFreezeMinValue();
        mChangeMin = mTargetTempRange.getChangeMinValue();
        mFridgeMax = mTargetTempRange.getFridgeMaxValue();
        mFreezeMax = mTargetTempRange.getFreezeMaxValue();
        mChangeMax = mTargetTempRange.getChangeMaxValue();
        MyLogUtil.i(TAG, mFridgeMax + "," + mFridgeMin + "," + mFreezeMax + "," + mFreezeMin + "," + mChangeMax + "," + mChangeMin);
        skbFridge.setMax(mFridgeMax - mFridgeMin);
        skbFreeze.setMax(mFreezeMax - mFreezeMin);
        skbChange.setMax(mChangeMax - mChangeMin);

        int fridgeTarget = mService.getFridgeTargetTemp();
        int freezeTarget = mService.getFreezeTargetTemp();
        int changeTarget = mService.getChangeTargetTemp();
        skbFridge.setProgress(fridgeTarget - mFridgeMin);
        skbFreeze.setProgress(freezeTarget - mFreezeMin);
        skbChange.setProgress(changeTarget - mChangeMin);
        tvFridgeTarget.setText(fridgeTarget + " ℃");
        tvFreezeTarget.setText(freezeTarget + " ℃");
        tvChangeTarget.setText(changeTarget + " ℃");
    }

    private void setModel() throws RemoteException {
        if (mSerialData.isSerialDataReady()) {
            stopWaitTask();
            mModel = mSerialData.getCurrentModel();
            if (mModel.equals(ConstantUtil.BCD251_MODEL)) {
                tvFridgeModel.setText(ConstantUtil.BCD251_MODEL);
            } else if (mModel.equals(ConstantUtil.BCD256_MODEL)) {
                tvFridgeModel.setText(ConstantUtil.BCD256_MODEL + "/" + ConstantUtil.BCD256_MODEL + "(S)");
            } else if (mModel.equals(ConstantUtil.BCD401_MODEL)) {
                tvFridgeModel.setText(ConstantUtil.BCD401_MODEL + "/" + ConstantUtil.BCD401_MODEL + "(S)");
            } else if (mModel.equals(ConstantUtil.BCD476_MODEL)) {
                tvFridgeModel.setText(ConstantUtil.BCD475_MODEL+" "+
                        ConstantUtil.BCD476_MODEL+" "+
                        ConstantUtil.BCD476_RFID_MODEL);
            } else if (mModel.equals(ConstantUtil.BCD658_MODEL)) {
                tvFridgeModel.setText(ConstantUtil.BCD658_MODEL);
            }
            setView();
            isready = true;
            startTimerTask();
            MyLogUtil.i(TAG, "fridge model is " + mModel);
        }
    }

    private void startWaitTask() {
        if (mWaitTask == null) {
            mWaitTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0x01);
                }
            };
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(mWaitTask, 0, 1000);
    }

    private void stopWaitTask() {
        if (mWaitTask != null) {
            mWaitTask.cancel();
            mWaitTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startTimerTask() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0x02);
                }
            };
            mTimer.schedule(mTimerTask, 0, 200);
        }
    }

    private void stopTimerTask() {
        if (mTimerTask != null) {
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
                        setModel();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x02:
                    try {
                        refreshUI();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    private void refreshUI() throws RemoteException {
        tvStatusCode.setText(mMainBoardParameters.getFrameDataString());

        switch (mModel) {
            case BCD251_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp) + " ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp) + " ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp) + " ℃");
                tvChangeTemp.setText(getStatusValueByName(EnumBaseName.changeShowTemp) + " ℃");

                if (mService.getSmartMode()) {
                    btnSmart.setOn();
                } else {
                    btnSmart.setOff();
                }

                if (mService.getHolidayMode()) {
                    btnHoliday.setOn();
                } else {
                    btnHoliday.setOff();
                }

                if (mService.getQuickColdMode()) {
                    btnQuickCold.setOn();
                } else {
                    btnQuickCold.setOff();
                }

                if (mService.getQuickFreezeMode()) {
                    btnQuickFreeze.setOn();
                } else {
                    btnQuickFreeze.setOff();
                }


                if (mService.getFridgeSwitch()) {
                    btnFridgeClose.setOn();
                    btnFridgeClose.setText("冷藏开");
                } else {
                    btnFridgeClose.setOff();
                    btnFridgeClose.setText("冷藏关");
                }
                break;
            case BCD256_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp) + " ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp) + " ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp) + " ℃");
                tvChangeTemp.setText(getStatusValueByName(EnumBaseName.changeShowTemp) + " ℃");
                if (mService.getSmartMode()) {
                    btnSmart.setOn();
                } else {
                    btnSmart.setOff();
                }

                if (mService.getHolidayMode()) {
                    btnHoliday.setOn();
                } else {
                    btnHoliday.setOff();
                }

                if (mService.getQuickColdMode()) {
                    btnQuickCold.setOn();
                } else {
                    btnQuickCold.setOff();
                }

                if (mService.getQuickFreezeMode()) {
                    btnQuickFreeze.setOn();
                } else {
                    btnQuickFreeze.setOff();
                }

                if (mService.getFridgeSwitch()) {
                    btnFridgeClose.setOn();
                    btnFridgeClose.setText("冷藏开");
                } else {
                    btnFridgeClose.setOff();
                    btnFridgeClose.setText("冷藏关");
                }
                break;
            case BCD401_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp) + " ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp) + " ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp) + " ℃");

                if (mService.getSmartMode()) {
                    btnSmart.setOn();
                } else {
                    btnSmart.setOff();
                }

                if (mService.getPurifyMode()) {
                    btnPurify.setOn();
                } else {
                    btnPurify.setOff();
                }

                if (mService.getQuickColdMode()) {
                    btnQuickCold.setOn();
                } else {
                    btnQuickCold.setOff();
                }

                if (mService.getQuickFreezeMode()) {
                    btnQuickFreeze.setOn();
                } else {
                    btnQuickFreeze.setOff();
                }
                break;
            case BCD476_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp) + " ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp) + " ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp) + " ℃");

                if (mService.getSmartMode()) {
                    btnSmart.setOn();
                } else {
                    btnSmart.setOff();
                }

                if (mService.getHolidayMode()) {
                    btnHoliday.setOn();
                } else {
                    btnHoliday.setOff();
                }

                if (mService.getQuickColdMode()) {
                    btnQuickCold.setOn();
                } else {
                    btnQuickCold.setOff();
                }

                if (mService.getQuickFreezeMode()) {
                    btnQuickFreeze.setOn();
                } else {
                    btnQuickFreeze.setOff();
                }
                break;
            case BCD658_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp) + " ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp) + " ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp) + " ℃");
                tvChangeTemp.setText(getStatusValueByName(EnumBaseName.changeShowTemp) + " ℃");

                if (mService.getSmartMode()) {
                    btnSmart.setOn();
                } else {
                    btnSmart.setOff();
                }

                if (mService.getHolidayMode()) {
                    btnHoliday.setOn();
                } else {
                    btnHoliday.setOff();
                }

                if (mService.getQuickColdMode()) {
                    btnQuickCold.setOn();
                } else {
                    btnQuickCold.setOff();
                }

                if (mService.getQuickFreezeMode()) {
                    btnQuickFreeze.setOn();
                } else {
                    btnQuickFreeze.setOff();
                }


                if (mService.getFridgeSwitch()) {
                    btnFridgeClose.setOn();
                    btnFridgeClose.setText("冷藏开");
                } else {
                    btnFridgeClose.setOff();
                    btnFridgeClose.setText("冷藏关");
                }
                if(mService.getPirSwitch()){
                    btnFridgeDoor.setOn();
                    btnFridgeDoor.setText("人感开");
                }else {
                    btnFridgeDoor.setOff();
                    btnFridgeDoor.setText("人感关");
                }
                break;
        }

    }

    private String getStatusValueByName(EnumBaseName enumBaseName) {
        return Integer.toString(mMainBoardParameters.getMbsValueByName(enumBaseName.toString()));
    }

    /*private String getControlValueByName(EnumBaseName enumBaseName){
        return Integer.toString(mMainBoardParameters.getMbcValueByName(enumBaseName.toString()));
    }*/
    private int getControlValueByName(EnumBaseName enumBaseName) {
        return mMainBoardParameters.getMbcValueByName(enumBaseName.toString());
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

    private void initFridgeClose(final int idButton) {
        btnFridgeClose = (MyTestButton) findViewById(idButton);
        //        btnFridgeClose.setText("冷藏开关");
        btnFridgeClose.setEnabled(true);

        btnFridgeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnFridgeClose.isPress()) {
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
    }

    private void initPir(final int idButton) {
        btnFridgeDoor = (MyTestButton) findViewById(idButton);
        btnFridgeDoor.setEnabled(true);

        btnFridgeDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == idButton) {
                    if (btnFridgeDoor.isPress()) {
                        try {
                            mService.setPirSwitch(false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mService.setPirSwitch(true);
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
            case R.id.btn_debug_return:
                finish();
                break;
            case R.id.text_debug_fridge_model:
                popResetPassWin();
                break;
            case R.id.title_debug_status_code:
                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info;
                String tftVersion = "none";
                try {
                    info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                    tftVersion = info.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, FactoryStatusActivity.class);
                intent.putExtra("version", tftVersion);
                startActivity(intent);
                break;
        }
    }

    private void listenerSeekBar() {
        skbFridge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFridgeTarget.setText(Integer.toString(progress + mFridgeMin) + " ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mIsBound) {
                    try {
                        mService.setFridgeTemp(progress + mFridgeMin);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        skbFreeze.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFreezeTarget.setText(Integer.toString(progress + mFreezeMin) + " ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mIsBound) {
                    try {
                        mService.setFreezeTemp(progress + mFreezeMin);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        skbChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvChangeTarget.setText(Integer.toString(progress + mChangeMin) + " ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mIsBound) {
                    try {
                        mService.setChangeTemp(progress + mChangeMin);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void popResetPassWin() {
        //        final PopWindowNormalInput normalInput = new PopWindowNormalInput(this, "密码", "提醒！恢复出厂设置后，本地账户信息将被清除。", "请输入恢复出厂设置密码");
        final PopWindowNormalInput normalInput = new PopWindowNormalInput(this,
                "提醒！", "未接电控板时，可选择冰箱控制模型！\r\n确定后请重启冰箱！",mModel);
        normalInput.showDialog();
        normalInput.setPopListener(new PopInputListener() {
            @Override
            public void onOkClick(int content) {
                FridgeInfoDbMgr fridgeInfoDbMgr = FridgeInfoDbMgr.getInstance();
                FridgeInfoEntry mFridgeInfoEntry = new FridgeInfoEntry();
                mFridgeInfoEntry.name = "fridgeId";
                switch (content){
                    case R.id.pop_content_251:
                        mFridgeInfoEntry.value = ConstantUtil.BCD251_SN;
                        fridgeInfoDbMgr.updateValue(mFridgeInfoEntry);
                        break;
                    case R.id.pop_content_256:
                        mFridgeInfoEntry.value = ConstantUtil.BCD256_SN;
                        fridgeInfoDbMgr.updateValue(mFridgeInfoEntry);
                        break;
                    case R.id.pop_content_401:
                        mFridgeInfoEntry.value = ConstantUtil.BCD401_SN;
                        fridgeInfoDbMgr.updateValue(mFridgeInfoEntry);
                        break;
                    case R.id.pop_content_476:
                        mFridgeInfoEntry.value = ConstantUtil.BCD476_SN;
                        fridgeInfoDbMgr.updateValue(mFridgeInfoEntry);
                        break;
                    case R.id.pop_content_658:
                        mFridgeInfoEntry.value = ConstantUtil.BCD658_SN;
                        fridgeInfoDbMgr.updateValue(mFridgeInfoEntry);
                        break;
                }
                normalInput.dismiss();
            }

            @Override
            public void onCancelClick() {

            }
        });
    }


}

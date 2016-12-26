package com.haiersmart.sfcontrol.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.service.MainBoardParameters;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import static com.haiersmart.sfcontrol.constant.ConstantUtil.REFRIGERATOR_CLOSE;
import static com.haiersmart.sfcontrol.constant.ConstantUtil.REFRIGERATOR_OPEN;

public class TestMainActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "TestMainActivity";
    private Intent mServiceIntent;
    private ControlMainBoardService mService;
    private MainBoardParameters mMainBoardParameters;
    private Button mBtnSmart, //智能
            mBtnHoliday,//假日
            mBtnQuickCold,//速冷
            mBtnQuickFreeze;//速冻
    private VerticalSeekBar mColdTempSeekbar; //冷藏室档位
    private VerticalSeekBar mFreezeTempSeekbar;//冷冻室档位
    private VerticalSeekBar mVarialableTempSeekbar;//变温室档位
    private ToggleButton tbColdingOpen;//冷藏室开关
    private TextView mTvPicCold, mTvPicVariable, mTvPicFreeze,//Fridge picture text
            mTvSettingCold, mTvSettingVariable, mTvSettingFreeze, mTvSettingColdContent,
            mTvStatusCode,mTvEnvTemp,mTvEnvHum;
    private boolean mIsSmart, mIsHoliday, mIsCold, mIsFreeze;
    private int mFridgeMinValue, mFridgeMaxValue, mFreezeMinValue, mFreezeMaxValue, mChangeMinValue, mChangeMaxValue;
    private boolean mIsBound = false;
    private ToggleButton mTbColdSwitch;
    private LinearLayout mLlColdLevel, mLlVariableLevel, mLlFreezeLevel;
    private String mSettingColdWarn,mSettingVariableWarn,mSettingFreezeWarn,mTbColdSwitchWarn;
    private int mQueryReadyTimes = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainBoardParameters = MainBoardParameters.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        registerBroadcast();
        initService();
        initViews();
        sendBroadcastToService(ConstantUtil.QUERY_CONTROL_READY);
        mQueryReadyTimes++;
        MyLogUtil.v(TAG, "onCreate finished");
    }

    private void initService() {
        bindService(new Intent(this, ControlMainBoardService.class), conn, Context.BIND_AUTO_CREATE);
    }

    private void initViews() {
        //Fridge picture text
        mTvPicCold = (TextView) findViewById(R.id.text_fridge_temp);
        mTvPicVariable = (TextView) findViewById(R.id.text_change_temp);
        mTvPicFreeze = (TextView) findViewById(R.id.text_freeze_temp);
        //冷藏室
        mTvSettingColdContent = (TextView) findViewById(R.id.tVColdSettingContent);
        //冷藏开关
        tbColdingOpen = (ToggleButton) findViewById(R.id.toggle_colding);
        //状态码
        mTvStatusCode = (TextView)findViewById(R.id.text_status_code);
        mTvEnvTemp = (TextView)findViewById(R.id.text_env_temp) ;
        mTvEnvHum = (TextView)findViewById(R.id.text_env_hum) ;

        //mode button
        mBtnSmart = (Button) findViewById(R.id.btnSmart);
        mBtnHoliday = (Button) findViewById(R.id.btnHoliday);
        mBtnQuickFreeze = (Button) findViewById(R.id.btnQuickFreeze);
        mBtnQuickCold = (Button) findViewById(R.id.btnQuickCold);

        mBtnSmart.setOnClickListener(this);
        mBtnHoliday.setOnClickListener(this);
        mBtnQuickFreeze.setOnClickListener(this);
        mBtnQuickCold.setOnClickListener(this);

        //setting temp and seakbar
        //cold
        mLlColdLevel = (LinearLayout) findViewById(R.id.cold_layout);
        mLlColdLevel.setOnTouchListener(new SettingOnTouchListener());
        mTvSettingCold = (TextView) findViewById(R.id.tVColdSettingTemp);
        mColdTempSeekbar = (VerticalSeekBar) findViewById(R.id.seekBarCold);
        mColdTempSeekbar.setOnSeekBarChangeListener(new SettingSeekBarChangeListener());
        //variable
        mLlVariableLevel = (LinearLayout) findViewById(R.id.bianwenshi_layout);
        mLlVariableLevel.setOnTouchListener(new SettingOnTouchListener());
        mTvSettingVariable = (TextView) findViewById(R.id.tVVariableSettingTemp);
        mVarialableTempSeekbar = (VerticalSeekBar) findViewById(R.id.seekBarVariable);
        mVarialableTempSeekbar.setOnSeekBarChangeListener(new SettingSeekBarChangeListener());
        //freeze
        mLlFreezeLevel = (LinearLayout) findViewById(R.id.freezeLayout);
        mLlFreezeLevel.setOnTouchListener(new SettingOnTouchListener());
        mTvSettingFreeze = (TextView) findViewById(R.id.tvFreezeSettingTemp);
        mFreezeTempSeekbar = (VerticalSeekBar) findViewById(R.id.seekBarFreeze);
        mFreezeTempSeekbar.setOnSeekBarChangeListener(new SettingSeekBarChangeListener());
        // 冷藏开关
        mTbColdSwitch = (ToggleButton) findViewById(R.id.toggle_colding);
        mTbColdSwitch.setOnTouchListener(new SettingOnTouchListener());
        //冷藏开关默认值为0
        mTbColdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.getId()==R.id.toggle_colding) {
                    MyLogUtil.d(TAG,"onCheckedChanged modedebug toggle_colding");
                }

                if(mTbColdSwitchWarn.equals(ConstantUtil.NO_WARNING)) {
                    MyLogUtil.d(TAG,"isChecked modedebug mTbColdSwitch.isChecked = "+mTbColdSwitch.isChecked());
                    if(mTbColdSwitch.isChecked()) {
                        sendBroadcastToService(REFRIGERATOR_OPEN);
                        MyLogUtil.d(TAG,"onCheckedChanged modedebug set fridge close");
                    } else {
                        sendBroadcastToService(REFRIGERATOR_CLOSE);
                        MyLogUtil.d(TAG,"onCheckedChanged modedebug set fridge open");
                    }
                }
            }
        } );
        MyLogUtil.v(TAG, "initViews finished");
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLogUtil.v(TAG, "ControlMainBoardService onServiceConnected");
            mIsBound = true;
            ControlMainBoardService.CmbBinder binder = (ControlMainBoardService.CmbBinder) service;
            mService = binder.getService();
            mServiceIntent = new Intent(TestMainActivity.this, ControlMainBoardService.class);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
            MyLogUtil.v(TAG, "ControlMainBoardService onServiceDisconnected");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(!mIsBound) {
            initService();
        }
//        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(receiveUpdateUI);
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        stopService(mServiceIntent);
        unregisterReceiver(receiveUpdateUI);
        if(mQueryReadyTimes > 0) {
            readyHandler.removeCallbacks(readyRunnable);
        }
        statusHandler.removeCallbacks(statusRunnable);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSmart: {
                if (mIsSmart) {
                    MyLogUtil.i(TAG, "onClick smart on");
                    sendBroadcastToService(ConstantUtil.MODE_SMART_OFF);
                } else {
                    MyLogUtil.i(TAG, "onClick smart off");
                    sendBroadcastToService(ConstantUtil.MODE_SMART_ON);
                }
            }
            break;
            case R.id.btnHoliday: {
//                if (mIsHoliday) {
//                    mServiceIntent.setAction(ConstantUtil.MODE_HOLIDAY_OFF);
//                } else {
//                    mServiceIntent.setAction(ConstantUtil.MODE_HOLIDAY_ON);
//                }
//                startService(mServiceIntent);

                if (mIsHoliday) {
                    MyLogUtil.i(TAG, "onClick holiday on to off");
                    sendBroadcastToService(ConstantUtil.MODE_HOLIDAY_OFF);
                } else {
                    MyLogUtil.i(TAG, "onClick holiday off to on");
                    sendBroadcastToService(ConstantUtil.MODE_HOLIDAY_ON);
                }
            }
            break;
            case R.id.btnQuickFreeze: {
//                if (mIsFreeze) {
//                    mServiceIntent.setAction(ConstantUtil.MODE_FREEZE_OFF);
//                } else {
//                    mServiceIntent.setAction(ConstantUtil.MODE_FREEZE_ON);
//                }
//                startService(mServiceIntent);
                if (mIsFreeze) {
                    MyLogUtil.i(TAG, "onClick freeze on to off");
                    sendBroadcastToService(ConstantUtil.MODE_FREEZE_OFF);
                } else {
                    MyLogUtil.i(TAG, "onClick freeze off to on");
                    sendBroadcastToService(ConstantUtil.MODE_FREEZE_ON);
                }
            }
            break;
            case R.id.btnQuickCold: {
//                if (mIsCold) {
//                    mServiceIntent.setAction(ConstantUtil.MODE_COLD_OFF);
//                } else {
//                    mServiceIntent.setAction(ConstantUtil.MODE_COLD_ON);
//                }
//                startService(mServiceIntent);


                if (mIsCold) {
                    MyLogUtil.i(TAG, "onClick cold on to off");
                    sendBroadcastToService(ConstantUtil.MODE_COLD_OFF);
                } else {
                    MyLogUtil.i(TAG, "onClick cold off to on");
                    sendBroadcastToService(ConstantUtil.MODE_COLD_ON);
                }
            }
            break;
            default:
                break;
        }
    }

    private void sendBroadcastToService(String actionCmd) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.COMMAND_TO_SERVICE);
        intent.putExtra(ConstantUtil.KEY_MODE,actionCmd);
        sendBroadcast(intent);
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_READY);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_FRIDGE_INFO);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_FRIDGE_RANGE);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_CHANGE_RANGE);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_FREEZE_RANGE);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_CONTROL);//模式和档位信息广播
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_TEMPER);//温度广播
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_ERROR);//错误或故障信息广播
        //intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_ALARM);//报警信息广播
        intentFilter.addAction(ConstantUtil.SERVICE_NOTICE);
        registerReceiver(receiveUpdateUI, intentFilter);
    }


    private BroadcastReceiver receiveUpdateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLogUtil.i(TAG, "BroadcastReceiver onReceive Range action="+ action);
            if(action.equals(ConstantUtil.SERVICE_NOTICE)) {
                Boolean isServiceReady = intent.getBooleanExtra(ConstantUtil.KEY_READY, true);
                handleReadyEvent(isServiceReady);
            }

//            } else if (action.equals(ConstantUtil.BROADCAST_ACTION_FRIDGE_INFO)) {
//                String fridgeId = intent.getStringExtra(ConstantUtil.KEY_FRIDGE_ID);
//                String fridgeType = intent.getStringExtra(ConstantUtil.KEY_FRIDGE_TYPE);
//                sendBroadcastToService(ConstantUtil.QUERY_FRIDGE_TEMP_RANGE);
//                sendBroadcastToService(ConstantUtil.QUERY_CHANGE_TEMP_RANGE);
//                sendBroadcastToService(ConstantUtil.QUERY_FREEZE_TEMP_RANGE);
//                sendBroadcastToService(ConstantUtil.QUERY_CONTROL_INFO);
//                sendBroadcastToService(ConstantUtil.QUERY_TEMPER_INFO);
//                statusHandler.post(statusRunnable);
//            } else if (action.equals(ConstantUtil.BROADCAST_ACTION_CONTROL)) {
//                String jsonString = intent.getStringExtra(ConstantUtil.KEY_CONTROL_INFO);
//                updateSettingAndModeUI(jsonString);
//            } else if (action.equals(ConstantUtil.BROADCAST_ACTION_TEMPER)) {
//                String jsonString = intent.getStringExtra(ConstantUtil.KEY_TEMPER);
//                updateTempAndModeUI(jsonString);
//            }

//                Bundle bundle = intent.getBundleExtra("key");
//                ArrayList<FridgeStatusEntry> statusList =
//                        (ArrayList<FridgeStatusEntry>) bundle.getSerializable(ConstantUtil.KEY_TEMPER);
//                if (statusList != null) {
//                    MyLogUtil.v(TAG, "BroadcastReceiver onReceive statusList.size=" + statusList.size());
//                    updateTempAndModeUI(statusList);
//                } else {
//                    MyLogUtil.i(TAG, "BroadcastReceiver onReceive statusList is NULL");
//                }
//            }  else if(action.equals(ConstantUtil.BROADCAST_ACTION_FRIDGE_RANGE)) {
//                mFridgeMinValue = intent.getIntExtra(ConstantUtil.FRIDGE_TEMP_MIN, 0);
//                mFridgeMaxValue = intent.getIntExtra(ConstantUtil.FRIDGE_TEMP_MAX, 0);
//                MyLogUtil.i(TAG,"BroadcastReceiver onReceive Range mFridgeMinValue ="+ mFridgeMinValue + "mFridgeMaxValue="+ mFridgeMaxValue);
//                mColdTempSeekbar.setMax(mFridgeMaxValue - mFridgeMinValue);
//            } else if(action.equals(ConstantUtil.BROADCAST_ACTION_CHANGE_RANGE)) {
//                mChangeMinValue = intent.getIntExtra(ConstantUtil.CHANGE_TEMP_MIN, 0);
//                mChangeMaxValue = intent.getIntExtra(ConstantUtil.CHANGE_TEMP_MAX, 0);
//                mVarialableTempSeekbar.setMax(mChangeMaxValue - mChangeMinValue);
//            } else if(action.equals(ConstantUtil.BROADCAST_ACTION_FREEZE_RANGE)) {
//                mFreezeMinValue = intent.getIntExtra(ConstantUtil.FREEZE_TEMP_MIN, 0);
//                mFreezeMaxValue = intent.getIntExtra(ConstantUtil.FREEZE_TEMP_MAX, 0);
//                mFreezeTempSeekbar.setMax(mFreezeMaxValue - mFreezeMinValue);
//            }
        }
    };

    private void handleReadyEvent(Boolean isReady) {
        if(isReady) {
            if(mQueryReadyTimes > 0) {
                readyHandler.removeCallbacks(readyRunnable);
            }
            mQueryReadyTimes = 0;
            sendBroadcastToService(ConstantUtil.QUERY_FRIDGE_INFO);
        } else {
            mQueryReadyTimes++;
            readyHandler.postDelayed(readyRunnable, 500);
        }
    }

    Runnable readyRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            readyHandler.sendMessage(msg);
        }
    };

    private Handler readyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    if(mQueryReadyTimes > 20) {
                        ToastUtil.showToastCenter(" Connect main board more than 20s failed! over time!");
                        readyHandler.removeCallbacks(readyRunnable);
                        mQueryReadyTimes = 0;
                    } else {
                        sendBroadcastToService(ConstantUtil.QUERY_CONTROL_READY);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    Runnable statusRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            statusHandler.sendMessage(msg);
        }
    };

    private Handler statusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    mTvStatusCode.setText(mMainBoardParameters.getFrameDataString());
                    mTvEnvTemp.setText(Integer.toString(mMainBoardParameters.getMbsValueByName(EnumBaseName.envShowTemp.toString()))+"℃");
                    mTvEnvHum.setText(Integer.toString(mMainBoardParameters.getMbsValueByName(EnumBaseName.envShowHum.toString()))+"%");
                    statusHandler.postDelayed(statusRunnable, 1000);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void updateTempAndModeUI(String jsonString) {
        MyLogUtil.d(TAG, "UpdateTempAndModeUI in");
        if(jsonString == null || jsonString.length()==0) return;
        JSONArray array = JSONArray.parseArray(jsonString);
        for(int i=0; i<array.size();i++) {
            JSONObject object = array.getJSONObject(i);
            String name = (String) object.get("name");
            int temp =(int) object.get("value");
            if(name.equals("fridgeShowTemp") ) {
                mTvPicCold.setText(Integer.toString(temp));
            } else if( name.equals("changeShowTemp") ) {
                mTvPicVariable.setText(Integer.toString(temp));
            }else if( name.equals("freezeShowTemp") ) {
                mTvPicFreeze.setText(Integer.toString(temp));
            }
        }
//        mTvStatusCode.setText(mMainBoardParameters.getFrameDataString());
//        mTvEnvTemp.setText(Integer.toString(mMainBoardParameters.getMbsValueByName(EnumBaseName.envShowTemp.toString()))+"℃");
//        mTvEnvHum.setText(Integer.toString(mMainBoardParameters.getMbsValueByName(EnumBaseName.envShowHum.toString()))+"%");

        MyLogUtil.d(TAG, "UpdateTempAndModeUI out");
    }

    private void updateSettingAndModeUI(String jsonString) {
        MyLogUtil.d(TAG, "updateSettingAndModeUI in");
        if(jsonString == null || jsonString.length()==0) return;

        boolean isOpen = false;
        int coldLevel = -1;
        int changeLevel = -1;
        int freezeLevel = -1;
        JSONArray array = JSONArray.parseArray(jsonString);
        for(int i=0; i<array.size();i++) {
            JSONObject object = array.getJSONObject(i);
            String name = (String) object.get("name");
            int value = (int) object.get("value");
            String disable = (String) object.get("disable");
            if(name.equals("smartMode")) {
                mIsSmart = value == 1 ? true:false;
            } else if(name.equals("holidayMode")) {
                mIsHoliday = value == 1 ? true:false;
            } else if(name.equals("quickColdMode")) {
                mIsCold = value == 1 ? true:false;
            } else if(name.equals("quickFreezeMode")) {
                mIsFreeze = value == 1 ? true:false;
            } else if(name.equals("fridgeTargetTemp")) {
                coldLevel = value;
                mSettingColdWarn = disable;
            } else if(name.equals("freezeTargetTemp")) {
                freezeLevel = value;
                mSettingFreezeWarn = disable;
            } else if(name.equals("changeTargetTemp")) {
                changeLevel = value;
                mSettingVariableWarn = disable;
            } else if(name.equals("fridgeSwitch")) {
                isOpen = value == 1 ? true:false;
                mTbColdSwitchWarn = disable;
            }
        }
        MyLogUtil.d(TAG, "updateSettingAndModeUI mIsSmart=" + mIsSmart + ",mIsHoliday=" +mIsHoliday+",mIsCold="+mIsCold+",mIsFreeze"+mIsFreeze);
        if(mIsSmart) {
            enableButton(mBtnSmart);
            mTvSettingCold.setText("智能运行中");
            mTvSettingFreeze.setText("智能运行中");
            if(!mIsHoliday) {
                disableButton(mBtnHoliday);
            }
            if(!mIsFreeze) {
                disableButton(mBtnQuickFreeze);
            }
        } else {
            disableButton(mBtnSmart);
            if(mIsHoliday) {
                enableButton(mBtnHoliday);
                mTvSettingCold.setText("假日运行中");
            } else {
                disableButton(mBtnHoliday);
                mTvSettingCold.setText(Integer.toString(coldLevel) + "℃");
            }

            if(mIsFreeze) {
                enableButton(mBtnQuickFreeze);
                mTvSettingFreeze.setText("速冻中");
            } else {
                disableButton(mBtnQuickFreeze);
                mTvSettingFreeze.setText(Integer.toString(freezeLevel) + "℃");
            }
        }

        if(mIsCold) {
            enableButton(mBtnQuickCold);
            mTvSettingVariable.setText("速冷中");
        } else {
            disableButton(mBtnQuickCold);
            mTvSettingVariable.setText(Integer.toString(changeLevel) + "℃");
        }

        mTbColdSwitch.setChecked(isOpen);
        MyLogUtil.d(TAG,"modedebug updateTempLevelSettingUI mTbColdSwitchWarn=" + mTbColdSwitchWarn);
        if (isOpen) {
            mTvSettingCold.setTextColor(getResources().getColor(R.color.black2));
            mTvSettingColdContent.setTextColor(getResources().getColor(R.color.black2));
        }
        else {
            mTvSettingCold.setTextColor(getResources().getColor(R.color.grey_food_weight));
            mTvSettingColdContent.setTextColor(getResources().getColor(R.color.grey_food_weight));

        }

        updateSeekBar(mColdTempSeekbar,mSettingColdWarn, coldLevel- mFridgeMinValue);
        updateSeekBar(mVarialableTempSeekbar,mSettingVariableWarn, changeLevel - mChangeMinValue);
        updateSeekBar(mFreezeTempSeekbar,mSettingFreezeWarn, freezeLevel- mFreezeMinValue);

        MyLogUtil.i(TAG,"updateSettingAndModeUI mFridgeMinValue="+mFridgeMinValue+",mChangeMinValue="+mChangeMinValue+",mFreezeMinValue="+mFreezeMinValue);

        MyLogUtil.d(TAG,"modedebug updateTempLevelSettingUI mSettingColdWarn=" + mSettingColdWarn);
    }

   private void updateSeekBar(VerticalSeekBar seekBar,String disableReason, int value) {
       MyLogUtil.i(TAG,"updateSeekBar value="+value);
       seekBar.setProgress(value);
       if(disableReason.equals(ConstantUtil.NO_WARNING)) {
           seekBar.setEnabled(true);
       } else {
           seekBar.setEnabled(false);
       }
   }


    private void enableButton(Button button) {
        button.setBackgroundResource(R.drawable.btn_myfamily_sure);
        button.setTextColor(getResources().getColor(R.color.mine_others_background));
    }

    private void disableButton(Button button) {
        button.setBackgroundResource(R.drawable.bt_mode_control);
        button.setTextColor(getResources().getColor(R.color.black));
    }

    class SettingSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            MyLogUtil.i("SettingSeekBarChangeListener", "onProgressChanged progress=" + progress);
            if (mColdTempSeekbar == seekBar) {
                int showValue = progress + mFridgeMinValue;
                mTvSettingCold.setText(Integer.toString(showValue) + "℃");
            } else if (mVarialableTempSeekbar == seekBar) {
                int showValue = progress + mChangeMinValue;
                mTvSettingVariable.setText(Integer.toString(showValue) + "℃");
            } else if (mFreezeTempSeekbar == seekBar) {
                int showValue = progress + mFreezeMinValue;
                mTvSettingFreeze.setText(Integer.toString(showValue) + "℃");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            MyLogUtil.i("SettingSeekBarChangeListener", "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            MyLogUtil.i("SettingSeekBarChangeListener", "onStopTrackingTouch");
            int progress = seekBar.getProgress();
            MyLogUtil.i("SettingSeekBarChangeListener", "onStopTrackingTouch progress=" + progress);
            if (mColdTempSeekbar == seekBar) {
                int showValue = progress + mFridgeMinValue;
                mTvSettingCold.setText(Integer.toString(showValue) + "℃");
                MyLogUtil.i("SettingSeekBarChangeListener", "onStopTrackingTouch cold showValue=" + showValue);
                mColdTempSeekbar.setProgress(progress);
//                mServiceIntent.putExtra(ConstantUtil.KEY_SET_FRIDGE_LEVEL, showValue);
//                mServiceIntent.setAction(ConstantUtil.TEMPER_SETCOLD);
//                startService(mServiceIntent);
                sendBroadcastToService(ConstantUtil.TEMPER_SETCOLD, ConstantUtil.KEY_SET_FRIDGE_LEVEL,showValue);
            } else if (mVarialableTempSeekbar == seekBar) {
                int showValue = progress + mChangeMinValue;
                mTvSettingVariable.setText(Integer.toString(showValue) + "℃");
                MyLogUtil.i("SettingSeekBarChangeListener", "onStopTrackingTouch variable showValue=" + showValue);
                mVarialableTempSeekbar.setProgress(progress);
//                mServiceIntent.putExtra(ConstantUtil.KEY_SET_COLD_LEVEL, showValue);
//                mServiceIntent.setAction(ConstantUtil.TEMPER_SETCUSTOMAREA);
//                startService(mServiceIntent);
                sendBroadcastToService(ConstantUtil.TEMPER_SETCUSTOMAREA, ConstantUtil.KEY_SET_COLD_LEVEL,showValue);
            } else if (mFreezeTempSeekbar == seekBar) {
                int showValue = progress + mFreezeMinValue;
                mTvSettingFreeze.setText(Integer.toString(showValue) + "℃");
                MyLogUtil.i("SettingSeekBarChangeListener", "onStopTrackingTouch freeze showValue=" + showValue);
                mFreezeTempSeekbar.setProgress(progress);
//                mServiceIntent.putExtra(ConstantUtil.KEY_SET_FREEZE_LEVEL, showValue);
//                mServiceIntent.setAction(ConstantUtil.TEMPER_SETFREEZE);
//                startService(mServiceIntent);
                sendBroadcastToService(ConstantUtil.TEMPER_SETFREEZE, ConstantUtil.KEY_SET_FREEZE_LEVEL, showValue);

            }
        }
    }

    private void sendBroadcastToService(String action, String key, int value) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.COMMAND_TO_SERVICE);
        intent.putExtra(ConstantUtil.KEY_MODE, action);
        intent.putExtra(key,value);
        sendBroadcast(intent);
    }

    class SettingOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean res = false;
            switch (v.getId()) {
                case R.id.cold_layout: {
                    if (!mColdTempSeekbar.isEnabled()) {
                        ToastUtil.showToastLong(mSettingColdWarn);
                        res = true;
                    }
                }
                break;
                case R.id.bianwenshi_layout:
                    if (!mVarialableTempSeekbar.isEnabled()) {
                        ToastUtil.showToastLong(mSettingVariableWarn);
                        res = true;
                    }
                    break;
                case R.id.freezeLayout:
                    if (!mFreezeTempSeekbar.isEnabled()) {
                        ToastUtil.showToastLong(mSettingFreezeWarn);
                        res = true;
                    }
                    break;
                case R.id.toggle_colding: {
                    if (mTbColdSwitch.isChecked()) {
                        if(mTbColdSwitchWarn.equals(ConstantUtil.NO_WARNING)) {
                            MyLogUtil.d(TAG,"toggle_colding modedebug no warning");
                        } else {
                            ToastUtil.showToastLong(mTbColdSwitchWarn);
                            MyLogUtil.d(TAG,"toggle_colding modedebug mTbColdSwitchWarn="+mTbColdSwitchWarn);
                            res = true;
                        }
                    }
                }
                break;
                default:
                    break;
            }
            return res;
        }
    }



}


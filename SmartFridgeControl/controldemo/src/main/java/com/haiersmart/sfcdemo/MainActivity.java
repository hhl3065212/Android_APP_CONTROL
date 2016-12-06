package com.haiersmart.sfcdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haiersmart.sfcdemo.constant.EnumBaseName;
import com.haiersmart.sfcdemo.draw.MyTestButton;
import com.haiersmart.sfcdemo.model.FridgeModel;

import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcdemo.constant.ConstantUtil.BCD251_MODEL;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_ALARM;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_CHANGE_RANGE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_CONTROL;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_ERROR;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_FREEZE_RANGE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_FRIDGE_RANGE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_INFO;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.BROADCAST_ACTION_TEMPER;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.COMMAND_TO_SERVICE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.FRIDGETYPE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_CONTROL_INFO;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_MODE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_SET_COLD_LEVEL;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_SET_FREEZE_LEVEL;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_SET_FRIDGE_LEVEL;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.KEY_TEMPER;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_COLD_OFF;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_COLD_ON;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_FREEZE_OFF;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_FREEZE_ON;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_HOLIDAY_OFF;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_HOLIDAY_ON;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_SMART_OFF;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.MODE_SMART_ON;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.QUERY_CHANGE_TEMP_RANGE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.QUERY_CONTROL_INFO;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.QUERY_FREEZE_TEMP_RANGE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.QUERY_FRIDGE_TEMP_RANGE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.QUERY_TEMPER_INFO;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.REFRIGERATOR_CLOSE;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.REFRIGERATOR_OPEN;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.TEMPER_SETCOLD;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.TEMPER_SETCUSTOMAREA;
import static com.haiersmart.sfcdemo.constant.ConstantUtil.TEMPER_SETFREEZE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private boolean mIsBound = false;
    private static FridgeModel mModel = new FridgeModel();

    private Timer mTimer;

    private LinearLayout lineEnvTemp, lineEnvHum, lineFridgeTemp, lineFreezeTemp, lineChangeTemp,
            lineFridgeTarget, lineFreezeTarget, lineChangeTarger;
    private TextView tvFridgeModel, tvStatusCode, tvEnvTemp, tvEnvHum, tvFridgeTemp, tvFreezeTemp, tvChangeTemp,
            tvFridgeTarget, tvFreezeTarget, tvChangeTarget;
    private Button btnReturn;
    private MyTestButton btnSmart, btnHoliday, btnQuickCold, btnQuickFreeze, btnFridgeSwitch;
    private SeekBar skbFridge, skbFreeze, skbChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.haiersmart.sfcontrol", "com.haiersmart.sfcontrol.service.ControlMainBoardService"));
        startService(intent);
        registerBroadcast();
        sendUserCommond(KEY_MODE, FRIDGETYPE);
        sendUserCommond(KEY_MODE, QUERY_FRIDGE_TEMP_RANGE);
        sendUserCommond(KEY_MODE, QUERY_FREEZE_TEMP_RANGE);
        sendUserCommond(KEY_MODE, QUERY_CHANGE_TEMP_RANGE);
        findView();
        mTimer = new Timer();
        mTimer.schedule(mWaitTask, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiveUpdateUI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiveUpdateUI);
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION_CONTROL);//模式和档位信息广播
        intentFilter.addAction(BROADCAST_ACTION_TEMPER);//温度广播
        intentFilter.addAction(BROADCAST_ACTION_ERROR);//错误或故障信息广播
        intentFilter.addAction(BROADCAST_ACTION_ALARM);
        intentFilter.addAction(BROADCAST_ACTION_FRIDGE_RANGE);
        intentFilter.addAction(BROADCAST_ACTION_CHANGE_RANGE);
        intentFilter.addAction(BROADCAST_ACTION_FREEZE_RANGE);
        registerReceiver(receiveUpdateUI, intentFilter);
    }

    private BroadcastReceiver receiveUpdateUI = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BROADCAST_ACTION_CONTROL)) {
                String jsonString = intent.getStringExtra(KEY_CONTROL_INFO);
                JSONArray jsonArray = JSONArray.parseArray(jsonString);
                updateModeLevel(jsonArray);
            } else if (action.equals(BROADCAST_ACTION_TEMPER)) {
                String jsonString = intent.getStringExtra(KEY_TEMPER);
                JSONArray jsonArray = JSONArray.parseArray(jsonString);
                updateShowTemp(jsonArray);
            } else if (action.equals(BROADCAST_ACTION_ERROR)) {

            } else if (action.equals(BROADCAST_ACTION_ALARM)) {

            } else if (action.equals(BROADCAST_ACTION_FRIDGE_RANGE)) {
                mModel.mFridgeMin = intent.getIntExtra("fridgeMinValue", 0);
                mModel.mFridgeMax = intent.getIntExtra("fridgeMaxValue", 0);
                skbFridge.setMax(mModel.mFridgeMax - mModel.mFridgeMin);
            } else if (action.equals(BROADCAST_ACTION_CHANGE_RANGE)) {
                mModel.mChangeMin = intent.getIntExtra("changeMinValue", 0);
                mModel.mChangeMax = intent.getIntExtra("changeMaxValue", 0);
                skbChange.setMax(mModel.mChangeMax - mModel.mChangeMin);
            } else if (action.equals(BROADCAST_ACTION_FREEZE_RANGE)) {
                mModel.mFreezeMin = intent.getIntExtra("freezeMinValue", 0);
                mModel.mFreezeMax = intent.getIntExtra("freezeMaxValue", 0);
                skbFreeze.setMax(mModel.mFreezeMax - mModel.mFreezeMin);
            } else if (action.equals(FRIDGETYPE)) {
                mModel.mFridgeModel = intent.getStringExtra(BROADCAST_ACTION_INFO);
            }

        }
    };

    private void sendUserCommond(String key, String content) {
        Intent intent = new Intent();
        intent.setAction(COMMAND_TO_SERVICE);
        intent.putExtra(key, content);
        sendBroadcast(intent);
    }

    private void sendUserCommond(String keyOne, String contentOne, String keyTwo, int contentTwo) {
        Intent intent = new Intent();
        intent.setAction(COMMAND_TO_SERVICE);
        intent.putExtra(keyOne, contentOne);
        intent.putExtra(keyTwo, contentTwo);
        sendBroadcast(intent);
    }

    private void sendUserCommond(String key, int value) {
        Intent intent = new Intent();
        intent.setAction(COMMAND_TO_SERVICE);
        intent.putExtra(key, value);
        sendBroadcast(intent);
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

        tvFridgeModel = (TextView) findViewById(R.id.text_demo_fridge_model);
        tvStatusCode = (TextView) findViewById(R.id.text_demo_status_code);
        tvEnvTemp = (TextView) findViewById(R.id.text_demo_env_temp);
        tvEnvHum = (TextView) findViewById(R.id.text_demo_env_hum);
        tvFridgeTemp = (TextView) findViewById(R.id.text_demo_fridge_temp);
        tvFreezeTemp = (TextView) findViewById(R.id.text_demo_freeze_temp);
        tvChangeTemp = (TextView) findViewById(R.id.text_demo_change_temp);
        tvFridgeTarget = (TextView) findViewById(R.id.text_demo_fridge_target);
        tvFreezeTarget = (TextView) findViewById(R.id.text_demo_freeze_target);
        tvChangeTarget = (TextView) findViewById(R.id.text_demo_change_target);

        btnReturn = (Button) findViewById(R.id.btn_demo_return);
        btnReturn.setOnClickListener(this);

        skbFridge = (SeekBar) findViewById(R.id.skb_demo_fridge);
        skbFreeze = (SeekBar) findViewById(R.id.skb_demo_freeze);
        skbChange = (SeekBar) findViewById(R.id.skb_demo_change);
        listenerSeekBar();

    }

    private void setView() {
        //        initSeekBar();
        sendUserCommond(KEY_MODE, QUERY_TEMPER_INFO);
        sendUserCommond(KEY_MODE, QUERY_CONTROL_INFO);
        switch (mModel.mFridgeModel) {
            case BCD251_MODEL:
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
        }
    }

    private void setModel() {
        if (mModel.mFridgeModel == null) {
            mModel.mFridgeModel = BCD251_MODEL;
            tvFridgeModel.setText(mModel.mFridgeModel);
            mWaitTask.cancel();
            setView();
            mTimer.schedule(mTimerTask, 0, 500);
        }
    }

    private TimerTask mWaitTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0x01);
            //            setModel();
        }
    };

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0x02);
            //            refreshUI();
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    setModel();
                    break;
                case 0x02:
                    refreshUI();
                    break;
            }
        }

    };

    private void refreshUI() {
        //        sendUserCommond(KEY_MODE,QUERY_TEMPER_INFO);
        switch (mModel.mFridgeModel) {
            case BCD251_MODEL:
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
                break;
        }

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
                        sendUserCommond(KEY_MODE, MODE_SMART_OFF);
                    } else {
                        sendUserCommond(KEY_MODE, MODE_SMART_ON);
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
                        sendUserCommond(KEY_MODE, MODE_HOLIDAY_OFF);
                    } else {
                        sendUserCommond(KEY_MODE, MODE_HOLIDAY_ON);
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
                        sendUserCommond(KEY_MODE, MODE_COLD_OFF);
                    } else {
                        sendUserCommond(KEY_MODE, MODE_COLD_ON);
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
                        sendUserCommond(KEY_MODE, MODE_FREEZE_OFF);
                    } else {
                        sendUserCommond(KEY_MODE, MODE_FREEZE_ON);
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
                        sendUserCommond(KEY_MODE, REFRIGERATOR_CLOSE);
                    } else {
                        sendUserCommond(KEY_MODE, REFRIGERATOR_OPEN);
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
                sendUserCommond(KEY_MODE, TEMPER_SETCOLD, KEY_SET_FRIDGE_LEVEL, progress + mModel.mFridgeMin);

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
                sendUserCommond(KEY_MODE, TEMPER_SETFREEZE, KEY_SET_FREEZE_LEVEL, progress + mModel.mFreezeMin);
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
                sendUserCommond(KEY_MODE, TEMPER_SETCUSTOMAREA, KEY_SET_COLD_LEVEL, progress + mModel.mChangeMin);
            }
        });
    }

    private void updateShowTemp(JSONArray jsonArray) {
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
    }

    private void updateModeLevel(JSONArray jsonArray) {
        Log.i(TAG, "mode level jsonArray:" + jsonArray);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = (String) jsonObject.get("name");
            int value = (int) jsonObject.get("value");
            String disable = (String) jsonObject.get("disable");
            Log.i(TAG, i + " name:" + name + " value:" + value + " disable:" + disable);
            if (name.equals(EnumBaseName.fridgeTargetTemp.toString())) {
                mModel.mFridgeTarget = value;
                mModel.mDisableFridge = disable;
            } else if (name.equals(EnumBaseName.freezeTargetTemp.toString())) {
                mModel.mFreezeTarget = value;
                mModel.mDisableFreeze = disable;
            } else if (name.equals(EnumBaseName.changeTargetTemp.toString())) {
                mModel.mChangeTarget = value;
                mModel.mDisableChange = disable;
            } else if (name.equals(EnumBaseName.smartMode.toString())) {
                mModel.isSmart = (value == 1) ? true : false;
                mModel.mDisableSmart = disable;
            } else if (name.equals(EnumBaseName.holidayMode.toString())) {
                mModel.isHoliday = (value == 1) ? true : false;
                mModel.mDisableHoliday = disable;
            } else if (name.equals(EnumBaseName.quickColdMode.toString())) {
                mModel.isQuickCold = (value == 1) ? true : false;
                mModel.mDisableQuickCold = disable;
            } else if (name.equals(EnumBaseName.quickFreezeMode.toString())) {
                mModel.isQuickFreeze = (value == 1) ? true : false;
                mModel.mDisableQuickFreeze = disable;
            } else if (name.equals(EnumBaseName.fridgeSwitch.toString())) {
                mModel.isFridgeOpen = (value == 1) ? true : false;
                mModel.mDisableFridgeOpen = disable;
            }
        }
        skbFridge.setProgress(mModel.mFridgeTarget - mModel.mFridgeMin);
        if (mModel.mDisableFridge.equals("none")) {
            skbFridge.setEnabled(true);
            tvFridgeTarget.setText(Integer.toString(mModel.mFridgeTarget) + " ℃");
        } else {
            skbFridge.setEnabled(false);
            tvFridgeTarget.setText(mModel.mDisableFridge);
        }
        skbFreeze.setProgress(mModel.mFreezeTarget - mModel.mFreezeMin);
        if (mModel.mDisableFreeze.equals("none")) {
            skbFreeze.setEnabled(true);
            tvFreezeTarget.setText(Integer.toString(mModel.mFreezeTarget) + " ℃");
        } else {
            skbFreeze.setEnabled(false);
            tvFreezeTarget.setText(mModel.mDisableFreeze);
        }
        skbChange.setProgress(mModel.mChangeTarget - mModel.mChangeMin);
        if (mModel.mDisableChange.equals("none")) {
            skbChange.setEnabled(true);
            tvChangeTarget.setText(Integer.toString(mModel.mChangeTarget) + " ℃");
        } else {
            skbChange.setEnabled(false);
            tvChangeTarget.setText(mModel.mDisableChange);
        }
    }


}


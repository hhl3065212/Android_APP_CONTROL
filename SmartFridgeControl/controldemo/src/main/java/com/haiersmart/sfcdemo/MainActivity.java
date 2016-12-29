package com.haiersmart.sfcdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.haiersmart.sfcdemo.constant.ConstantUtil;
import com.haiersmart.sfcdemo.constant.EnumBaseName;
import com.haiersmart.sfcdemo.constant.QrCodeUtil;
import com.haiersmart.sfcdemo.constant.TypeIdUtil;
import com.haiersmart.sfcdemo.draw.AlarmWindow;
import com.haiersmart.sfcdemo.draw.MyTestButton;
import com.haiersmart.sfcdemo.model.FridgeModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private boolean mIsBound = false;
    private static FridgeModel mModel = new FridgeModel();

    private Timer mTimer;
    private TimerTask mWaitTask, mTimerTask;

    private LinearLayout lineEnvTemp, lineEnvHum, lineFridgeTemp, lineFreezeTemp, lineChangeTemp,
            lineFridgeTarget, lineFreezeTarget, lineChangeTarger;
    private TextView tvFridgeModel, tvStatusCode, tvEnvTemp, tvEnvHum, tvFridgeTemp, tvFreezeTemp, tvChangeTemp,
            tvFridgeTarget, tvFreezeTarget, tvChangeTarget, tvTime, tvTest;
    private Button btnReturn;
    private MyTestButton btnSmart, btnHoliday, btnQuickCold, btnQuickFreeze, btnFridgeSwitch;
    private SeekBar skbFridge, skbFreeze, skbChange;
    private ImageView imvQrCode;

    private int onclickCounts = 0;
    private boolean isReady = false;
    private boolean isServiceReady = false;

    private NetRunnable mNetRunnable;
    private Thread mThread;

    private AlarmWindow alarmWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.haiersmart.sfcontrol", "com.haiersmart.sfcontrol.service.ControlMainBoardService"));
//        startService(intent);
        registerBroadcast();
        findView();
//        startQueryType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReady) {
            sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.QUERY_CONTROL_READY);
            startRefreshUI();
        } else {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.haiersmart.sfcontrol", "com.haiersmart.sfcontrol.service.ControlMainBoardService"));
            startService(intent);
            startQueryType();
        }
        //        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRefreshUI();
        //        unregisterReceiver(receiveUpdateUI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRefreshUI();
        unregisterReceiver(receiveUpdateUI);
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_READY);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_FRIDGE_INFO);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_CONTROL);//模式和档位信息广播
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_TEMPER);//温度广播
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_ERROR);//错误或故障信息广播
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_ALARM);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_FRIDGE_RANGE);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_CHANGE_RANGE);
//        intentFilter.addAction(ConstantUtil.BROADCAST_ACTION_FREEZE_RANGE);
        intentFilter.addAction(ConstantUtil.SERVICE_NOTICE);
        registerReceiver(receiveUpdateUI, intentFilter);
    }

    private BroadcastReceiver receiveUpdateUI = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "BroadcastReceiver receiveUpdateUI action=" + action);
            if(action.equals(ConstantUtil.SERVICE_NOTICE)){
                boolean ready = intent.getBooleanExtra(ConstantUtil.KEY_READY, isServiceReady);
                if(isServiceReady != ready){
                    isServiceReady = ready;
                    if(isServiceReady) {
                        stopQueryType();
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.QUERY_FRIDGE_INFO);
                    }else {
                        startQueryType();
                    }
                }
                if(isReady){
                    String jsonRange = intent.getStringExtra(ConstantUtil.KEY_RANGE);
                    if(jsonRange != null){
                        JSONObject jsonObject = JSONObject.parseObject(jsonRange);
                        Log.i(TAG, "jsonRange is:" + jsonRange);
                        setTempRange(jsonObject);
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.QUERY_TEMPER_INFO);
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.QUERY_CONTROL_INFO);
                        startRefreshUI();
                    }
                    String jsonControl = intent.getStringExtra(ConstantUtil.KEY_CONTROL_INFO);
                    if(jsonControl != null) {
                        JSONArray jsonArray = JSONArray.parseArray(jsonControl);
                        updateModeLevel(jsonArray);
                        refreshUI();
                    }
                    String jsonTemper = intent.getStringExtra(ConstantUtil.KEY_TEMPER);
                    if(jsonTemper != null) {
                        JSONArray jsonArray = JSONArray.parseArray(jsonTemper);
                        updateShowTemp(jsonArray);
                        refreshUI();
                    }
                    String jsonError = intent.getStringExtra(ConstantUtil.KEY_ERROR);
                    if(jsonError != null) {
                        JSONArray jsonArray = JSONArray.parseArray(jsonError);
                        Log.i(TAG, "error jsonArray:" + jsonArray);
                    }
                    String jsonDoor = intent.getStringExtra(ConstantUtil.DOOR_STATUS);
                    if (jsonDoor != null) {
                        JSONObject jsonObject = JSONObject.parseObject(jsonDoor);
                        Log.i(TAG, "door status jsonObject:" + jsonObject);
                                            }
                    String jsonAlarm = intent.getStringExtra(ConstantUtil.DOOR_ALARM_STATUS);
                    if (jsonAlarm != null) {
                        JSONObject jsonObject = JSONObject.parseObject(jsonAlarm);
                        Log.i(TAG, "door alarm status jsonObject:" + jsonObject);
                        handleDoorAlarm(jsonObject);
                    }

                }else {
                    String jsonInfo = intent.getStringExtra(ConstantUtil.KEY_INFO);
                    if(jsonInfo != null){
                        JSONObject jsonObject = JSONObject.parseObject(jsonInfo);
                        Log.i(TAG, "jsonInfo is:" + jsonInfo);
                        String id = (String) jsonObject.get(ConstantUtil.KEY_TYPE_ID);
                        String type = (String) jsonObject.get(ConstantUtil.KEY_FRIDGE_TYPE);
                        mModel.mFridgeModel = type;
                        mModel.mTypeId = id;
                        setModel();
                    }
                }
            }
        }
    };

    private void sendUserCommond(String key, String content) {
        Log.i(TAG, "kill content = " + content);
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.COMMAND_TO_SERVICE);
        intent.putExtra(key, content);
        sendBroadcast(intent);
    }

    private void sendUserCommond(String keyOne, String contentOne, String keyTwo, int contentTwo) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.COMMAND_TO_SERVICE);
        intent.putExtra(keyOne, contentOne);
        intent.putExtra(keyTwo, contentTwo);
        sendBroadcast(intent);
    }

    private void sendUserCommond(String key, int value) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.COMMAND_TO_SERVICE);
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
        tvFridgeModel.setOnClickListener(this);
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

        btnReturn = (Button) findViewById(R.id.btn_demo_return);
        btnReturn.setOnClickListener(this);

        skbFridge = (SeekBar) findViewById(R.id.skb_demo_fridge);
        skbFreeze = (SeekBar) findViewById(R.id.skb_demo_freeze);
        skbChange = (SeekBar) findViewById(R.id.skb_demo_change);
        listenerSeekBar();

        imvQrCode = (ImageView) findViewById(R.id.imv_demo_test);

    }

    private void setView() {
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
        }
        isReady = true;
        tvTest.setText("使用馨小厨APP扫码绑定");
        QrCodeUtil.createQRCode(imvQrCode, TypeIdUtil.getCode(mContext, mModel.mTypeId), 300);
        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.QUERY_TEMP_RANGE);
    }

    private void setModel() {
        //        if(mModel.mFridgeModel != null) {
        mModel.mFridgeModel = ConstantUtil.BCD251_MODEL;
        if (mModel.mFridgeModel.equals(ConstantUtil.BCD251_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel);
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD256_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel + "/" + mModel.mFridgeModel + "(S)");
        } else if (mModel.mFridgeModel.equals(ConstantUtil.BCD401_MODEL)) {
            tvFridgeModel.setText(mModel.mFridgeModel + "/" + mModel.mFridgeModel + "(S)");
        }
        setView();
//        startRefreshUI();
        //        }
    }

    private void startQueryType() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.haiersmart.sfcontrol", "com.haiersmart.sfcontrol.service.ControlMainBoardService"));
        startService(intent);
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
            mTimer.schedule(mWaitTask, 1000, 1000);
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
            mTimer.schedule(mTimerTask, 0, 300);
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
                    if (!isServiceReady) {
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.QUERY_CONTROL_READY);
                        Log.i(TAG, "sendControlCmdResponse main board is ready?");
                        //                    mWaitTask.cancel();
                    }
                    break;
                case 0x02:
                    refreshUI();
                    break;
            }
        }

    };

    private void refreshUI() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String strTime = simpleDateFormat.format(date);
        tvTime.setText(strTime);
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
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_SMART_OFF);
                    } else {
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_SMART_ON);
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
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_HOLIDAY_OFF);
                    } else {
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_HOLIDAY_ON);
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
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_COLD_OFF);
                    } else {
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_COLD_ON);
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
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_FREEZE_OFF);
                    } else {
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.MODE_FREEZE_ON);
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
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.REFRIGERATOR_CLOSE);
                    } else {
                        sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.REFRIGERATOR_OPEN);
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
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return true;
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
                    //                    intent.setClassName("com.haiersmart.sfcontrol","com.haiersmart.sfcontrol.ui.DebugActivity");
                    intent.setComponent(new ComponentName("com.haiersmart.sfcontrol", "com.haiersmart.sfcontrol.ui.FactoryStatusActivity"));
                    intent.setAction("FactoryStatusActivity");
                    intent.putExtra("version", tftVersion);
                    startActivity(intent);
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
                sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.TEMPER_SETCOLD,
                        ConstantUtil.KEY_SET_FRIDGE_LEVEL, progress + mModel.mFridgeMin);
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
                    toast.setGravity(Gravity.CENTER,0,0);
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
                sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.TEMPER_SETFREEZE,
                        ConstantUtil.KEY_SET_FREEZE_LEVEL, progress + mModel.mFreezeMin);
            }
        });
        skbFreeze.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableFreeze.equals("none")) {
                    return false;
                }else {
                    String show;
                    if (mModel.mDisableFreeze.equals("关闭")) {
                        show = "冷冻室已" + mModel.mDisableFreeze + "，如要调节温度请先开启冷冻室";
                    } else {
                        show = mModel.mDisableFreeze + "模式已开启，如要调节温度请先退出" + mModel.mDisableFreeze + "模式";
                    }
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
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
                sendUserCommond(ConstantUtil.KEY_MODE, ConstantUtil.TEMPER_SETCUSTOMAREA,
                        ConstantUtil.KEY_SET_COLD_LEVEL, progress + mModel.mChangeMin);
            }
        });
        skbChange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mModel.mDisableChange.equals("none")) {
                    return false;
                }else {
                    String show;
                    if (mModel.mDisableChange.equals("关闭")) {
                        show = "变温室已" + mModel.mDisableFreeze + "，如要调节温度请先开启变温室";
                    } else {
                        show = mModel.mDisableChange + "模式已开启，如要调节温度请先退出" + mModel.mDisableChange + "模式";
                    }
                    Toast toast = Toast.makeText(mContext, show, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return true;
                }
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
                tvChangeTemp.setText(Integer.toString(value));
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
//                if (mModel.mDisableFridgeOpen.equals("none")) {
                //                    btnFridgeSwitch.setEnabled(true);
                //                } else {
                //                    btnFridgeSwitch.setEnabled(false);
                //                }
            }
        }
        skbFridge.setProgress(mModel.mFridgeTarget - mModel.mFridgeMin);
        if (mModel.mDisableFridge.equals("none")) {
//            skbFridge.setEnabled(true);
            tvFridgeTarget.setText(Integer.toString(mModel.mFridgeTarget) + " ℃");
        } else {
//            skbFridge.setEnabled(false);
            tvFridgeTarget.setText(mModel.mDisableFridge);
        }
        skbFreeze.setProgress(mModel.mFreezeTarget - mModel.mFreezeMin);
        if (mModel.mDisableFreeze.equals("none")) {
//            skbFreeze.setEnabled(true);
            tvFreezeTarget.setText(Integer.toString(mModel.mFreezeTarget) + " ℃");
        } else {
//            skbFreeze.setEnabled(false);
            tvFreezeTarget.setText(mModel.mDisableFreeze);
        }
        skbChange.setProgress(mModel.mChangeTarget - mModel.mChangeMin);
        if (mModel.mDisableChange.equals("none")) {
//            skbChange.setEnabled(true);
            tvChangeTarget.setText(Integer.toString(mModel.mChangeTarget) + " ℃");
        } else {
//            skbChange.setEnabled(false);
            tvChangeTarget.setText(mModel.mDisableChange);
        }
    }
    private void handleDoorAlarm(JSONObject jsonObject){
        Integer value = (Integer) jsonObject.get("fridge");
        if(value == null || value ==0){
            cancelAlarmWindow("冷藏");
        }else {
            popAlarmWindow("冷藏");
        }
        value = (Integer)jsonObject.get("freeze");
        if(value == null || value ==0){
            cancelAlarmWindow("冷冻");
        }else {
            popAlarmWindow("冷冻");
        }
        value = (Integer)jsonObject.get("change");
        if(value == null || value ==0){
            cancelAlarmWindow("变温");
        }else {
            popAlarmWindow("变温");
        }
    }
    private void handleDoorStatus(JSONObject jsonObject){
        if((int)jsonObject.get("fridge")==1){
            popAlarmWindow("冷藏");
        }else {
            cancelAlarmWindow("冷藏");
        }
        if((int)jsonObject.get("freeze")==1){
            popAlarmWindow("冷冻");
        }else {
            cancelAlarmWindow("冷冻");
        }
        if((int)jsonObject.get("change")==1){
            popAlarmWindow("变温");
        }else {
            cancelAlarmWindow("变温");
        }
    }
    private void setTempRange(JSONObject jsonObject){
        mModel.mFridgeMin = (Integer)jsonObject.get("fridgeMinValue");
        mModel.mFridgeMax = (Integer)jsonObject.get("fridgeMaxValue");
        mModel.mFreezeMin = (Integer)jsonObject.get("freezeMinValue");
        mModel.mFreezeMax = (Integer)jsonObject.get("freezeMaxValue");
        mModel.mChangeMin = (Integer)jsonObject.get("changeMinValue");
        mModel.mChangeMax = (Integer)jsonObject.get("changeMaxValue");

        skbFridge.setMax(mModel.mFridgeMax - mModel.mFridgeMin);
        skbChange.setMax(mModel.mChangeMax - mModel.mChangeMin);
        skbFreeze.setMax(mModel.mFreezeMax - mModel.mFreezeMin);
    }

    private void popAlarmWindow(String show){
        if(alarmWindow == null) {
            alarmWindow = new AlarmWindow(this);
        }
        alarmWindow.addShow(show);
        alarmWindow.showDialog();
    }
    private void cancelAlarmWindow(String show){
        if(alarmWindow != null){
            alarmWindow.deleteShow(show);
            if(alarmWindow.isNoAlarm()){
                alarmWindow = null;
            }
        }
    }


}


package com.haiersmart.sfcontrol.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.draw.MyTestButton;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.service.MainBoardParameters;
import com.haiersmart.sfcontrol.service.configtable.TargetTempRange;
import com.haiersmart.sfcontrol.service.powerctl.SerialData;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD251_MODEL;

public class DebugActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "DebugActivity";
    private ControlMainBoardService mControlService;
    private boolean mIsBound = false;
    private SerialData mSerialData;
    private MainBoardParameters mMainBoardParameters;
    private String mModel;
    private TargetTempRange mTargetTempRange;
    private int mFridgeMin,mFridgeMax,mFreezeMin,mFreezeMax,mChangeMin,mChangeMax;

    private Timer mTimer;

    private LinearLayout lineEnvTemp,lineEnvHum,lineFridgeTemp,lineFreezeTemp,lineChangeTemp,
            lineFridgeTarget,lineFreezeTarget,lineChangeTarger;
    private TextView tvFridgeModel,tvStatusCode,tvEnvTemp,tvEnvHum,tvFridgeTemp,tvFreezeTemp,tvChangeTemp,
    tvFridgeTarget,tvFreezeTarget,tvChangeTarget;
    private Button btnReturn;
    private MyTestButton btnSmart,btnHoliday,btnQuickCold,btnQuickFreeze,btnFridgeClose;
    private SeekBar skbFridge,skbFreeze,skbChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        bindService(new Intent(this, ControlMainBoardService.class), conn, Context.BIND_AUTO_CREATE);
        mSerialData = SerialData.getInstance();
        mMainBoardParameters = MainBoardParameters.getInstance();
        findView();
        mTimer = new Timer();
        mTimer.schedule(mWaitTask,0,1000);
    }
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLogUtil.v(TAG, "ControlMainBoardService onServiceConnected");
            mIsBound = true;
            ControlMainBoardService.CmbBinder binder = (ControlMainBoardService.CmbBinder) service;
            mControlService = binder.getService();
//            mServiceIntent = new Intent(this, ControlMainBoardService.class);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
            MyLogUtil.v(TAG, "ControlMainBoardService onServiceDisconnected");
        }
    };
    private void findView(){
        lineEnvTemp=(LinearLayout)findViewById(R.id.linear_debug_env_temp);
        lineEnvHum=(LinearLayout)findViewById(R.id.linear_debug_env_hum);
        lineFridgeTemp=(LinearLayout)findViewById(R.id.linear_debug_fridge_temp);
        lineFreezeTemp=(LinearLayout)findViewById(R.id.linear_debug_freeze_temp);
        lineChangeTemp=(LinearLayout)findViewById(R.id.linear_debug_change_temp);
        lineFridgeTarget=(LinearLayout)findViewById(R.id.linear_debug_skb_fridge);
        lineFreezeTarget=(LinearLayout)findViewById(R.id.linear_debug_skb_freeze);
        lineChangeTarger=(LinearLayout)findViewById(R.id.linear_debug_skb_change);

        tvFridgeModel = (TextView)findViewById(R.id.text_debug_fridge_model);
        tvStatusCode = (TextView)findViewById(R.id.text_debug_status_code);
        tvEnvTemp = (TextView)findViewById(R.id.text_debug_env_temp);
        tvEnvHum = (TextView)findViewById(R.id.text_debug_env_hum);
        tvFridgeTemp = (TextView)findViewById(R.id.text_debug_fridge_temp);
        tvFreezeTemp = (TextView)findViewById(R.id.text_debug_freeze_temp);
        tvChangeTemp = (TextView)findViewById(R.id.text_debug_change_temp);
        tvFridgeTarget = (TextView)findViewById(R.id.text_debug_fridge_target);
        tvFreezeTarget = (TextView)findViewById(R.id.text_debug_freeze_target);
        tvChangeTarget = (TextView)findViewById(R.id.text_debug_change_target);

        btnReturn = (Button)findViewById(R.id.btn_debug_return);
        btnReturn.setOnClickListener(this);

        skbFridge = (SeekBar)findViewById(R.id.skb_debug_fridge);
        skbFreeze = (SeekBar)findViewById(R.id.skb_debug_freeze);
        skbChange = (SeekBar)findViewById(R.id.skb_debug_change);
        listenerSeekBar();

    }
    private void setView(){
        initSeekBar();
        switch (mModel){
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
        }
    }
    private void initSeekBar(){
        mTargetTempRange = mMainBoardParameters.getTargetTempRange();
        mFridgeMin = mTargetTempRange.getFridgeMinValue();
        mFreezeMin = mTargetTempRange.getFreezeMinValue();
        mChangeMin = mTargetTempRange.getChangeMinValue();
        mFridgeMax = mTargetTempRange.getFridgeMaxValue();
        mFreezeMax = mTargetTempRange.getFreezeMaxValue();
        mChangeMax = mTargetTempRange.getChangeMaxValue();
        skbFridge.setMax(mFridgeMax-mFridgeMin);
        skbFreeze.setMax(mFreezeMax-mFreezeMin);
        skbChange.setMax(mChangeMax-mChangeMin);
        skbFridge.setProgress(getControlValueByName(EnumBaseName.fridgeTargetTemp)-mFridgeMin);
        skbFreeze.setProgress(getControlValueByName(EnumBaseName.freezeTargetTemp)-mFreezeMin);
        skbChange.setProgress(getControlValueByName(EnumBaseName.changeTargetTemp)-mChangeMin);
        tvFridgeTarget.setText(Integer.toString(getControlValueByName(EnumBaseName.fridgeTargetTemp))+" ℃");
        tvFreezeTarget.setText(Integer.toString(getControlValueByName(EnumBaseName.freezeTargetTemp))+" ℃");
        tvChangeTarget.setText(Integer.toString(getControlValueByName(EnumBaseName.changeTargetTemp))+" ℃");
    }
    private void setModel(){
        mModel = mSerialData.getCurrentModel();
        if(mModel != null) {
            tvFridgeModel.setText(mModel);
            mWaitTask.cancel();
            setView();
            mTimer.schedule(mTimerTask,0,500);
            MyLogUtil.i(TAG, "fridge model is " + mModel);
        }
    }

    private TimerTask mWaitTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0x01);
        }
    };

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0x02);
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x01:
                    setModel();
                    break;
                case 0x02:
                    refreshUI();
                    break;
            }
        }

    };
    private void refreshUI(){
        tvStatusCode.setText(mMainBoardParameters.getFrameDataString());
        FridgeControlEntry fridgeControlEntry;
        switch (mModel){
            case BCD251_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp)+" ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp)+" ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp)+" ℃");
                tvChangeTemp.setText(getStatusValueByName(EnumBaseName.changeShowTemp)+" ℃");
                fridgeControlEntry = mControlService.getEntryByName(EnumBaseName.smartMode);
                if(fridgeControlEntry.value == 1){
                    btnSmart.setOn();
                }else {
                    btnSmart.setOff();
                }
                fridgeControlEntry = mControlService.getEntryByName(EnumBaseName.holidayMode);
                if(fridgeControlEntry.value == 1){
                    btnHoliday.setOn();
                }else {
                    btnHoliday.setOff();
                }
                fridgeControlEntry = mControlService.getEntryByName(EnumBaseName.quickColdMode);
                if(fridgeControlEntry.value == 1){
                    btnQuickCold.setOn();
                }else {
                    btnQuickCold.setOff();
                }
                fridgeControlEntry = mControlService.getEntryByName(EnumBaseName.quickFreezeMode);
                if(fridgeControlEntry.value == 1){
                    btnQuickFreeze.setOn();
                }else {
                    btnQuickFreeze.setOff();
                }
                fridgeControlEntry = mControlService.getEntryByName(EnumBaseName.fridgeSwitch);
                if(fridgeControlEntry.value == 1){
                    btnFridgeClose.setOn();
                }else {
                    btnFridgeClose.setOff();
                }
                break;
        }

    }
    private String getStatusValueByName(EnumBaseName enumBaseName){
        return Integer.toString(mMainBoardParameters.getMbsValueByName(enumBaseName.toString()));
    }
    /*private String getControlValueByName(EnumBaseName enumBaseName){
        return Integer.toString(mMainBoardParameters.getMbcValueByName(enumBaseName.toString()));
    }*/
    private int getControlValueByName(EnumBaseName enumBaseName){
        return mMainBoardParameters.getMbcValueByName(enumBaseName.toString());
    }
    private void initSmart(final int idButton){
        btnSmart = (MyTestButton)findViewById(idButton);
        btnSmart.setText("智能");
        btnSmart.setEnabled(true);


        btnSmart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == idButton){
                    if(btnSmart.isPress()){
//                        btnSmart.setOff();
                        mControlService.sendUserCommond(EnumBaseName.smartMode,0);
                    }else {
//                        btnSmart.setOn();
                        mControlService.sendUserCommond(EnumBaseName.smartMode,1);
                    }
                }
            }
        });
    }
    private void initHoliday(final int idButton){
        btnHoliday = (MyTestButton)findViewById(idButton);
        btnHoliday.setText("假日");
        btnHoliday.setEnabled(true);

        btnHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == idButton){
                    if(btnHoliday.isPress()){
//                        btnHoliday.setOff();
                        mControlService.sendUserCommond(EnumBaseName.holidayMode,0);
                    }else {
//                        btnHoliday.setOn();
                        mControlService.sendUserCommond(EnumBaseName.holidayMode,1);
                    }
                }
            }
        });
    }
    private void initQuickCold(final int idButton){
        btnQuickCold = (MyTestButton)findViewById(idButton);
        btnQuickCold.setText("速冷");
        btnQuickCold.setEnabled(true);

        btnQuickCold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == idButton){
                    if(btnQuickCold.isPress()){
//                        btnQuickCold.setOff();
                        mControlService.sendUserCommond(EnumBaseName.quickColdMode,0);
                    }else {
//                        btnQuickCold.setOn();
                        mControlService.sendUserCommond(EnumBaseName.quickColdMode,1);
                    }
                }
            }
        });
    }
    private void initQuickFreeze(final int idButton){
        btnQuickFreeze = (MyTestButton)findViewById(idButton);
        btnQuickFreeze.setText("速冻");
        btnQuickFreeze.setEnabled(true);

        btnQuickFreeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == idButton){
                    if(btnQuickFreeze.isPress()){
//                        btnQuickFreeze.setOff();
                        mControlService.sendUserCommond(EnumBaseName.quickFreezeMode,0);
                    }else {
//                        btnQuickFreeze.setOn();
                        mControlService.sendUserCommond(EnumBaseName.quickFreezeMode,1);
                    }
                }
            }
        });
    }
    private void initFridgeClose(final int idButton){
        btnFridgeClose = (MyTestButton)findViewById(idButton);
        btnFridgeClose.setText("冷藏开关");
        btnFridgeClose.setEnabled(true);

        btnFridgeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == idButton){
                    if(btnFridgeClose.isPress()){
//                        btnFridgeClose.setOff();
                        mControlService.sendUserCommond(EnumBaseName.fridgeSwitch,0);
                    }else {
//                        btnFridgeClose.setOn();
                        mControlService.sendUserCommond(EnumBaseName.fridgeSwitch,1);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_debug_return:
                finish();
                break;
        }
    }

    private void listenerSeekBar(){
        skbFridge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFridgeTarget.setText(Integer.toString(progress+mFridgeMin)+" ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(mIsBound)
                {
                    mControlService.sendUserCommond(EnumBaseName.fridgeTargetTemp,progress+mFridgeMin);
                }
            }
        });
        skbFreeze.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFreezeTarget.setText(Integer.toString(progress+mFreezeMin)+" ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(mIsBound) {
                    mControlService.sendUserCommond(EnumBaseName.freezeTargetTemp, progress + mFreezeMin);
                }
            }
        });
        skbChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvChangeTarget.setText(Integer.toString(progress+mChangeMin)+" ℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(mIsBound) {
                    mControlService.sendUserCommond(EnumBaseName.changeTargetTemp, progress + mChangeMin);
                }
            }
        });
    }


}

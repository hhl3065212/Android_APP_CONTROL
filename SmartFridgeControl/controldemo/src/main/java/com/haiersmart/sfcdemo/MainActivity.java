package com.haiersmart.sfcdemo;

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

import com.haiersmart.sfcdemo.draw.MyTestButton;
import com.haiersmart.sfcdemo.model.FridgeModel;
import com.haiersmart.sfcdemo.model.ModelTwoFiveOne;

import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcdemo.constant.ConstantUtil.BCD251_MODEL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private boolean mIsBound = false;
    private FridgeModel mModel;
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
        setContentView(R.layout.activity_main);
        findView();
        mTimer = new Timer();
        mTimer.schedule(mWaitTask,0,1000);
    }

    private void findView(){
        lineEnvTemp=(LinearLayout)findViewById(R.id.linear_demo_env_temp);
        lineEnvHum=(LinearLayout)findViewById(R.id.linear_demo_env_hum);
        lineFridgeTemp=(LinearLayout)findViewById(R.id.linear_demo_fridge_temp);
        lineFreezeTemp=(LinearLayout)findViewById(R.id.linear_demo_freeze_temp);
        lineChangeTemp=(LinearLayout)findViewById(R.id.linear_demo_change_temp);
        lineFridgeTarget=(LinearLayout)findViewById(R.id.linear_demo_skb_fridge);
        lineFreezeTarget=(LinearLayout)findViewById(R.id.linear_demo_skb_freeze);
        lineChangeTarger=(LinearLayout)findViewById(R.id.linear_demo_skb_change);

        tvFridgeModel = (TextView)findViewById(R.id.text_demo_fridge_model);
        tvStatusCode = (TextView)findViewById(R.id.text_demo_status_code);
        tvEnvTemp = (TextView)findViewById(R.id.text_demo_env_temp);
        tvEnvHum = (TextView)findViewById(R.id.text_demo_env_hum);
        tvFridgeTemp = (TextView)findViewById(R.id.text_demo_fridge_temp);
        tvFreezeTemp = (TextView)findViewById(R.id.text_demo_freeze_temp);
        tvChangeTemp = (TextView)findViewById(R.id.text_demo_change_temp);
        tvFridgeTarget = (TextView)findViewById(R.id.text_demo_fridge_target);
        tvFreezeTarget = (TextView)findViewById(R.id.text_demo_freeze_target);
        tvChangeTarget = (TextView)findViewById(R.id.text_demo_change_target);

        btnReturn = (Button)findViewById(R.id.btn_demo_return);
        btnReturn.setOnClickListener(this);

        skbFridge = (SeekBar)findViewById(R.id.skb_demo_fridge);
        skbFreeze = (SeekBar)findViewById(R.id.skb_demo_freeze);
        skbChange = (SeekBar)findViewById(R.id.skb_demo_change);
        listenerSeekBar();

    }
    private void setView(){
        initSeekBar();
        switch (mModel.mFridgeModel){
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
                initFridgeClose(R.id.btn_demo_bottom_left);
                break;
        }
    }
    private void initSeekBar(){
        skbFridge.setMax(mModel.mFridgeMax-mModel.mFridgeMax);
        skbFreeze.setMax(mModel.mFreezeMax-mModel.mFreezeMin);
        skbChange.setMax(mModel.mChangeMax-mModel.mChangeMin);
        skbFridge.setProgress(-mModel.mFridgeMin);
        skbFreeze.setProgress(-mModel.mFreezeMin);
        skbChange.setProgress(-mModel.mChangeMin);
    }
    private void setModel(){
        if(mModel != null) {
            tvFridgeModel.setText(mModel.mFridgeModel);
            mWaitTask.cancel();
            setView();
            mTimer.schedule(mTimerTask,0,500);
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
        switch (mModel.mFridgeModel){
            case BCD251_MODEL:
                tvFridgeTemp.setText(mModel.mFridgeShow+" ℃");
                tvFreezeTemp.setText(mModel.mFreezeShow+" ℃");
                tvChangeTemp.setText(mModel.mChangeShow+" ℃");
                if(mModel.isSmart){
                    btnSmart.setOn();
                }else {
                    btnSmart.setOff();
                }
                if(mModel.isHoliday){
                    btnHoliday.setOn();
                }else {
                    btnHoliday.setOff();
                }
                if(mModel.isQuickCold){
                    btnQuickCold.setOn();
                }else {
                    btnQuickCold.setOff();
                }
                if(mModel.isQuickFreeze){
                    btnQuickFreeze.setOn();
                }else {
                    btnQuickFreeze.setOff();
                }
                if(mModel.isFridgeClose){
                    btnFridgeClose.setOn();
                }else {
                    btnFridgeClose.setOff();
                }
                break;
        }

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
                        mControlService.sendUserCommond(EnumBaseName.smartMode,0);
                    }else {
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
        btnFridgeClose.setText("冷藏关闭");
        btnFridgeClose.setEnabled(true);

        btnFridgeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == idButton){
                    if(btnFridgeClose.isPress()){
                        //                        btnFridgeClose.setOff();
                        mControlService.sendUserCommond(EnumBaseName.fridgeCloseMode,0);
                    }else {
                        //                        btnFridgeClose.setOn();
                        mControlService.sendUserCommond(EnumBaseName.fridgeCloseMode,1);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_demo_return:
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


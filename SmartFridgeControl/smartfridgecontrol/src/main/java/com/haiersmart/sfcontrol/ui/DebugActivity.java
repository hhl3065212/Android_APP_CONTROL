package com.haiersmart.sfcontrol.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.service.MainBoardParameters;
import com.haiersmart.sfcontrol.service.configtable.TargetTempRange;
import com.haiersmart.sfcontrol.service.powerctl.PowerSerialOpt;
import com.haiersmart.sfcontrol.service.powerctl.SerialData;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD251_MODEL;

public class DebugActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "DebugActivity";
    private SerialData mSerialData;
    private MainBoardParameters mMainBoardParameters;
    private PowerSerialOpt mPowerSerialOpt;
    private String mModel;
    private TargetTempRange mTargetTempRange;
    private int mFridgeMin,mFridgeMax,mFreezeMin,mFreezeMax,mChangeMin,mChangeMax;

    private Timer mTimer;

    private LinearLayout lineEnvTemp,lineEnvHum,lineFridgeTemp,lineFreezeTemp,lineChangeTemp,
            lineFridgeTarget,lineFreezeTarget,lineChangeTarger;
    private TextView tvFridgeModel,tvStatusCode,tvEnvTemp,tvEnvHum,tvFridgeTemp,tvFreezeTemp,tvChangeTemp,
    tvFridgeTarget,tvFreezeTarget,tvChangeTarget;
    private Button btnReturn;
    private SeekBar skbFridge,skbFreeze,skbChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        mSerialData = SerialData.getInstance();
        mMainBoardParameters = MainBoardParameters.getInstance();
        try {
            mPowerSerialOpt = PowerSerialOpt.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        findView();
        mTimer = new Timer();
        mTimer.schedule(mWaitTask,0,1000);
    }
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
            mTimer.schedule(mTimerTask,0,1000);
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
        switch (mModel){
            case BCD251_MODEL:
                tvEnvTemp.setText(getStatusValueByName(EnumBaseName.envShowTemp)+" ℃");
                tvFridgeTemp.setText(getStatusValueByName(EnumBaseName.fridgeShowTemp)+" ℃");
                tvFreezeTemp.setText(getStatusValueByName(EnumBaseName.freezeShowTemp)+" ℃");
                tvChangeTemp.setText(getStatusValueByName(EnumBaseName.changeShowTemp)+" ℃");
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
                mPowerSerialOpt.sendCmdByName(EnumBaseName.fridgeTargetTemp.toString(),progress+mFridgeMin);
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
                mPowerSerialOpt.sendCmdByName(EnumBaseName.freezeTargetTemp.toString(),progress+mFreezeMin);
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
                mPowerSerialOpt.sendCmdByName(EnumBaseName.changeTargetTemp.toString(),progress+mChangeMin);
            }
        });
    }


}

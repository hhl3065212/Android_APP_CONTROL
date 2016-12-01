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
import com.haiersmart.sfcontrol.service.powerctl.SerialData;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.haiersmart.sfcontrol.constant.ConstantUtil.BCD251_MODEL;

public class DebugActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "DebugActivity";
    private SerialData mSerialData;
    private MainBoardParameters mMainBoardParameters;
    private String mModel;

    private Timer mTimer;

    private LinearLayout lineEnvTemp,lineEnvHum,lineFridgeTemp,lineFreezeTemp,lineChangeTemp,
            lineFridgeTarget,lineFreezeTarget,lineChangeTarger;
    private TextView tvFridgeModel,tvStatusCode,tvEnvTemp,tvEnvHum,tvFridgeTemp,tvFreezeTemp,tvChangeTemp;
    private Button btnReturn;
    private SeekBar skbFridge,skbFreeze,skbChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        mSerialData = SerialData.getInstance();
        mMainBoardParameters = MainBoardParameters.getInstance();
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

        btnReturn = (Button)findViewById(R.id.btn_debug_return);
        btnReturn.setOnClickListener(this);

        skbFridge = (SeekBar)findViewById(R.id.skb_debug_fridge);
        skbFreeze = (SeekBar)findViewById(R.id.skb_debug_freeze);
        skbChange = (SeekBar)findViewById(R.id.skb_debug_change);
    }
    private void setView(){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_debug_return:
                finish();
                break;
        }
    }


}

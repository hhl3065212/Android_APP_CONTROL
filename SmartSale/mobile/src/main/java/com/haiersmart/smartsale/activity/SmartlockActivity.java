package com.haiersmart.smartsale.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haiersmart.smartsale.R;
import com.haiersmart.smartsale.constant.ConstantUtil;
import com.haiersmart.smartsale.module.Smartlock;

import java.io.OutputStream;

public class SmartlockActivity extends Activity {
    protected final String TAG = "SmartlockActivity";
    private TextView txtDoorState;
    private Button btnUnlock;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    txtDoorState.setText("开门");
                    break;
                case 2:
                    txtDoorState.setText("关门");
                    break;
                default:
                    break;
            }
        }
    };

    private void unlockSmartlock() {
        boolean ret;
        OutputStream outputStream;
        byte[] openCmd = {'1', '\0'};
        Smartlock smartlock = Smartlock.getInstance();
        ret = smartlock.openSmartLock("/dev/smartlock");
        if (!ret)
            return;
        outputStream = smartlock.getOutputStream();
        if (outputStream == null)
            return;
        try {
            outputStream.write(openCmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver mReceiverSmartlockState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "receiver brodacast action = " + action);
            String state = intent.getStringExtra(ConstantUtil.DOOR_STATE);
            if(state.equals("open")) {
                mHandler.sendEmptyMessage(1);
            } else if(state.equals("close")) {
                mHandler.sendEmptyMessage(2);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlock);
        txtDoorState = (TextView) findViewById(R.id.txt_door_state);
        btnUnlock = (Button) findViewById(R.id.btn_smartlock_unlock);

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockSmartlock();
            }
        });

        registerReceiver(mReceiverSmartlockState, new IntentFilter(ConstantUtil.DOOR_STATE_BROADCAST));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.haiersmart.smartsale.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haiersmart.smartsale.R;
import com.haiersmart.smartsale.module.Smartlock;

import java.io.OutputStream;

public class SmartlockActivity extends Activity {
    private TextView txtDoorState;
    private Button btnUnlock;

    private void unlockSmartlock() {
        boolean ret;
        OutputStream outputStream;
        Smartlock smartlock = Smartlock.getInstance();
        ret = smartlock.openSmartLock("/dev/smartlock");
        if (!ret)
            return;
        outputStream = smartlock.getOutputStream();
        if (outputStream == null)
            return;
        try {
            outputStream.write("1".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

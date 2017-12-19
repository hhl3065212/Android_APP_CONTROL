package com.haiersmart.smartsale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.haiersmart.library.SerialPort.SerialPort;
import com.haiersmart.library.Utils.ConvertData;


public class MainActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    SerialPort mSerialPort;
    private Button button_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSerialPort = new SerialPort("/dev/ttyS1",115200);
        String mes= "serial port has open";
//        try {
//            mSerialPort.getOutputStream().write(mes.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mSerialPort.write(mes);
        mHandler.sendEmptyMessage(0x01);
        button_test=(Button)findViewById(R.id.button_test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
    }

    Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x01:
                    byte[] read = new byte[1024];
                    int len =0 ;
                    len = mSerialPort.read(read,30);
                    if(len >0) {
                        Log.i(TAG, "rev:"+ConvertData.bytesToString(read, ConvertData.HEX, len));
                    }
                    mHandler.sendEmptyMessage(0x01);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSerialPort.SerialPortClose();
        mSerialPort = null;
    }


}

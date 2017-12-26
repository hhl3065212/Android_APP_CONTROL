package com.haiersmart.smartsale.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.haiersmart.library.MediaPlayer.PlayFixedVoice;
import com.haiersmart.library.SerialPort.SerialPort;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;
import com.haiersmart.smartsale.R;


public class MainActivity extends Activity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    SerialPort mSerialPort;

    @Bind(id = R.id.btn_open,click = true)
    private Button btnOpen;
    @Bind(id = R.id.btn_select,click = true)
    private Button btnSelect;
    @Bind(id = R.id.btn_final,click = true)
    private Button btnFinal;
    @Bind(id = R.id.btn_pay,click = true)
    private Button btnPay;
    @Bind(id = R.id.button_test,click = true)
    private  Button button_test;
    @Bind(id = R.id.button_rfid, click = true)
    private Button btnRfid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);

//        mSerialPort = new SerialPort("/dev/ttyS1",115200);
//        String mes= "serial port has open";
//        try {
//            mSerialPort.getOutputStream().write(mes.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mSerialPort.write(mes);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open:
                PlayFixedVoice.playVoice(PlayFixedVoice.OPEN);
                break;
            case R.id.btn_select:
                PlayFixedVoice.playVoice(PlayFixedVoice.SELECT);
                break;
            case R.id.btn_final:
                PlayFixedVoice.playVoice(PlayFixedVoice.FINAL);
                break;
            case R.id.btn_pay:
                PlayFixedVoice.playVoice(PlayFixedVoice.PAY);
                break;
            case R.id.button_test:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
            case R.id.button_rfid:
                startActivity(new Intent(MainActivity.this, RfidTestActivity.class));
                break;
            default:
                break;
        }
    }


}

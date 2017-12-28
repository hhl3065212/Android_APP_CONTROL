package com.haiersmart.smartsale.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.haiersmart.library.MediaPlayer.PlayFixedVoice;
import com.haiersmart.library.SerialPort.SerialPort;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;
import com.haiersmart.smartsale.R;
import com.haiersmart.smartsale.constant.ConstantUtil;
import com.haiersmart.smartsale.service.HttpService;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends Activity implements View.OnClickListener, HttpService.UnlockListener {
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
    @Bind(id = R.id.tx_show)
    private TextView txShow;
    @Bind(id = R.id.et_show)
    private EditText etShow;
    @Bind(id = R.id.btn_send,click = true)
    private Button btnSend;
    @Bind(id = R.id.button_rfid, click = true)
    private Button btnRfid;

    Socket socket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    private String content = "";

    private boolean isBind = false;
    private HttpService.HttpBinder httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
        bindService(new Intent(this,HttpService.class),httpConnection,BIND_AUTO_CREATE);
        registerReceiver(ReceiverHttp,new IntentFilter(ConstantUtil.HTTP_BROADCAST));
    }

    BroadcastReceiver ReceiverHttp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG,"receiver brodacast action="+action);
        }
    };

    ServiceConnection httpConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            httpService = (HttpService.HttpBinder)service;
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ReceiverHttp);
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
            case R.id.btn_send:
                if (isBind){
                    httpService.setOnUnlockListener(this);
                }
                break;
            case R.id.button_rfid:
                startActivity(new Intent(MainActivity.this, RfidTestActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onUnlockListener(String userid) {
        Log.i(TAG,"unlock Listener message,userid="+userid);
    }
}

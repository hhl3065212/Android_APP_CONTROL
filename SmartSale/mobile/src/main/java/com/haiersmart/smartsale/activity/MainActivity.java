package com.haiersmart.smartsale.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haiersmart.library.MediaPlayer.PlayFixedVoice;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;
import com.haiersmart.smartsale.R;
import com.haiersmart.smartsale.application.SaleApplication;
import com.haiersmart.smartsale.constant.ConstantUtil;
import com.haiersmart.smartsale.service.HttpService;


public class MainActivity extends Activity implements View.OnClickListener, HttpService.UnlockListener {
    private final String TAG = getClass().getSimpleName();

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
    @Bind(id = R.id.btn_send,click = true)
    private Button btnSend;
    @Bind(id = R.id.button_rfid, click = true)
    private Button btnRfid;
    @Bind(id = R.id.button_smartlock, click = true)
    private Button btnSmartlock;
    @Bind(id = R.id.btn_tcplong,click = true)
    private Button btnTcpLong;

    private boolean isBind = false;
    private HttpService.HttpBinder httpService;
    private UnlockHandler handler = new UnlockHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
//        bindService(new Intent(this,HttpService.class),httpConnection,BIND_AUTO_CREATE);
        registerReceiver(ReceiverHttp,new IntentFilter(ConstantUtil.HTTP_BROADCAST));
        txShow.setText("mac="+ SaleApplication.get().getmMac());
    }

    BroadcastReceiver ReceiverHttp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String logi = "receiver brodacast action="+action;
            Log.i(TAG,logi);
            String userid = intent.getStringExtra(ConstantUtil.HTTP_KEY_USERID);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantUtil.HTTP_KEY_USERID,userid);
            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
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
//        unbindService(httpConnection);
        unregisterReceiver(ReceiverHttp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open:
                PlayFixedVoice.playVoice(PlayFixedVoice.WELCOME);
                break;
            case R.id.btn_select:
                PlayFixedVoice.playVoice(PlayFixedVoice.UNLOCK);
                break;
            case R.id.btn_final:
                PlayFixedVoice.playVoice(PlayFixedVoice.OPEN);
                break;
            case R.id.btn_pay:
                PlayFixedVoice.playVoice(PlayFixedVoice.CLOSE);
                break;
            case R.id.button_test:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
            case R.id.btn_send:
                PlayFixedVoice.playVoice(PlayFixedVoice.PAY);
                break;
            case R.id.button_rfid:
                startActivity(new Intent(MainActivity.this, RfidTestActivity.class));
                break;
            case R.id.button_smartlock:
                startActivity(new Intent(MainActivity.this, SmartlockActivity.class));
                break;
            case R.id.btn_tcplong:
                startActivity(new Intent(MainActivity.this, TcpLongActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onUnlockListener(String userid) {
        Log.i(TAG,"unlock Listener message,userid="+userid);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtil.HTTP_KEY_USERID,userid);
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }


    private int counts = 0;
    private class UnlockHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String userid = bundle.getString(ConstantUtil.HTTP_KEY_USERID);
            StringBuffer buffer = new StringBuffer();
            counts++;
            buffer.append(userid).append("第").append(counts).append("次").append("开锁\n").append("mac="+ SaleApplication.get().getmMac());
            txShow.setText(buffer.toString());
        }
    }
}

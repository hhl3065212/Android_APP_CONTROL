/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2018/1/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.smartsale.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiersmart.library.OKHttp.TcpLong;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;
import com.haiersmart.smartsale.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2018/1/15
 * Author: Holy.Han
 * modification:
 */
public class TcpLongActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private final String HOST = "192.168.100.232";
    private final int PORT = 8080;

    @Bind(id = R.id.txt_message)
    private TextView txtMessage;
    @Bind(id = R.id.edt_send)
    private EditText edtSend;
    @Bind(id = R.id.btn_send, click = true)
    private Button btnSend;
    @Bind(id = R.id.ll_tcplong, click = true)
    private LinearLayout llTcpLong;

    private TcpLong tcpLong;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcplong);
        ViewBinder.bind(this);
        txtMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

        tcpThread.start();

    }

    private Thread tcpThread = new Thread() {
        @Override
        public void run() {
            int len = 0;
            byte[] rev = new byte[1024];
            tcpLong = new TcpLong(HOST, PORT);
            while (!tcpLong.isConnected()) {
                String init = "hello";
                tcpLong.setSender(init.getBytes());
            }
            while (!isInterrupted()) {
                String init = "re hello";
                tcpLong.setSender(init.getBytes());
                while (tcpLong.isConnected()) {
                    len = tcpLong.getReceiver(rev);
                    if (len > 0) {
                        String show = new String(rev, 0, len);
                        sendMessage(show);
                    } else if(len <0){
                        break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpThread.interrupt();
        tcpLong.closeSocket();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String show = bundle.getString("show");
            //            txtMessage.setText(show);
            SimpleDateFormat formatter = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            txtMessage.append(str + show + "\r\n");
        }
    };

    private void sendMessage(String show) {
        Bundle bundle = new Bundle();
        bundle.putString("show", show);
        Message message = new Message();
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                tcpLong.setSender(edtSend.getText().toString().getBytes());
                break;
            case R.id.ll_tcplong:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;
        }
    }
}

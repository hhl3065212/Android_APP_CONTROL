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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;
import com.haiersmart.smartsale.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2018/1/15
 * Author: Holy.Han
 * modification:
 */
public class TcpLongActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private final String HOST = "192.168.200.11";
    private final int PORT = 80;

    @Bind(id = R.id.txt_message)
    private TextView txtMessage;

    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcplong);
        ViewBinder.bind(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int len = 0;
                byte[] rev = new byte[1024];
                try {
                    mSocket = new Socket(HOST,PORT);
                    mInputStream = mSocket.getInputStream();
                    mOutputStream = mSocket.getOutputStream();
                    Log.i(TAG,"Socket create!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (mSocket.isConnected()) {
                    try {
                        len = mInputStream.read(rev);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (len>0) {
                        String show = new String(rev, 0, len);
                        Bundle bundle = new Bundle();
                        bundle.putString("show",show);
                        Message message = new Message();
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG,"Socket disconnected");
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String show = bundle.getString("show");
//            txtMessage.setText(show);
            txtMessage.append(show+"\r\n");
        }
    };
}

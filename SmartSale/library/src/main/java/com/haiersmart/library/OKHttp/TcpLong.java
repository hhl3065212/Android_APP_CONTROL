/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2018/1/17
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.library.OKHttp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2018/1/17
 * Author: Holy.Han
 * modification:
 */
public class TcpLong {
    protected final String TAG = "TcpLong";

    private Socket mSocket;
    private InputStream mInputStream;
    private BufferedReader reader;
    private OutputStream mOutputStream;
    private BufferedWriter writer;
    private String HOST = "127.0.0.1";
    private int PORT = 80;
    private final int timeout = 60000;
    private boolean isConnected = false;

    public TcpLong(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    public void  createSocket() {
        try {
            if (mSocket == null) {
                Log.i(TAG, "Socket ready create!");
                mSocket = new Socket(Proxy.NO_PROXY);
                mSocket.connect(new InetSocketAddress(HOST, PORT), timeout);
                Log.i(TAG, mSocket.toString());
                mInputStream = mSocket.getInputStream();
                mOutputStream = mSocket.getOutputStream();
                mSocket.setKeepAlive(true);
                isConnected = true;
                Log.i(TAG, "Socket create!");
            }
        } catch (IOException e) {
            isConnected = false;
            mSocket = null;
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            if (mSocket != null) {
                isConnected = false;
                mInputStream.close();
                mOutputStream.close();
                mSocket.close();
                mSocket = null;
            }
            Log.i(TAG, "Socket disconnected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getReceiver(byte[] rev) {
        int len = 0;
        createSocket();
        if (mSocket != null) {
            Log.i(TAG, "Ready receive");
                try {
                    len = mInputStream.read(rev, 0, rev.length);
                    Log.i(TAG, "Received data,len = " + Integer.toString(len));
                } catch (IOException e) {
                    len = -1;
                    closeSocket();
                    e.printStackTrace();
                }
                if (len <0){
                    closeSocket();
                }
        }
        return len;
    }


    public void setSender(byte[] send) {
        createSocket();
        if (mSocket != null) {
            try {
                mOutputStream.write(send);
                Log.i(TAG, "Send data,len = " + Integer.toString(send.length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void setSender(String data) {
        createSocket();
        if (mSocket != null) {
            try {
                mOutputStream.write(data.getBytes());
                Log.i(TAG, "Send data,len = " + Integer.toString(data.length()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

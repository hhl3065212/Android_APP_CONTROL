package com.haier.tft.wifimodule.moduletool;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2015/11/16.
 */
public class SocketRevRunable implements Runnable{
    ControlPrefence mControlPrefence;
    DataInputStream inputStream;

    private BytesToInt mBytesToInt = new BytesToInt();

    public SocketRevRunable(DataInputStream inputStream ){
        this.inputStream=inputStream;

    }


    @Override
    public void run() {

        if(mControlPrefence==null){
            mControlPrefence = ControlPrefence.getInstance();
        }
            byte[] buff;
            BytesToInt my = new BytesToInt();
            while (mControlPrefence.getIsSocketLive()) {


                buff = new byte[1024];
                Message msg = new Message();
                Bundle mBundle = new Bundle();
                buff = new byte[1024];

                int len = -1;
                try {
                    Log.i("heart0", "before read");
                    len = inputStream.read(buff);
                    Log.i("heart0", "after read");

                } catch (IOException e) {
                    Log.i("sdk", " SocketRevThread IOException");

                    e.printStackTrace();
                    break;
                }

                if(len==-1){
                    Log.i("sdk", "get data lengh =-1,so break the loop");
                    break;
                }else{
                    Log.i("sdk", "get data lengh ="+len);
                }
                if(len>1)
                {

                    mBundle.putByteArray("receive", buff);
                    mBundle.putInt("lengh", len);
                    msg.setData(mBundle);
                    msg.what=0x01;
                    StaticValueAndConnectUrl.Handler.sendMessage(msg);

                }

        }

    }





}

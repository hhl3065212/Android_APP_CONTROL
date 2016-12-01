package com.haier.tft.wifimodule.moduletool;

import android.util.Log;

import com.haier.tft.wifimodule.alldata.DataToBytesForHeartBeat;
import com.haier.tft.wifimodule.wifimodule.ConnectService;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Administrator on 2015/12/4.
 */
public class BeatHeartThread implements Runnable{
    private String Mac;
    private short strengh;
    private byte[] data;
    private ControlPrefence mControlPrefence;
    private Socket socket;
    private ConnectService connectService;
    private   BytesToInt bytesToInt = new BytesToInt();

    public BeatHeartThread(byte[] bytes,ControlPrefence mControlPrefence,Socket socket,ConnectService connectService){

        this.strengh=strengh;
        this.mControlPrefence=mControlPrefence;
        this.socket=socket;
        this.data=bytes;
        this.connectService=connectService;

    }
    @Override
    public void run() {

            Log.i("sdk", "heartbeat ====before tiaojian===heartbeat");
            while (mControlPrefence.getIsOnline()&&mControlPrefence.getIsSocketLive() && null!=socket && !socket.isClosed() && socket.isConnected() && !socket.isInputShutdown()) {
                Log.i("sdk", "heartbeat =======heartbeat and socketflag = "+mControlPrefence.getIsSocketLive());

                Log.i("ceshi", "send data for heart = " + bytesToInt.bytesToHexString(data));

                try {
                    connectService.sendCmd(data);
//                   Thread.sleep(45000);
                    Thread.sleep(StaticValueAndConnectUrl.HEART_BEAT_TIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    connectService.stop();

                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
    }
}

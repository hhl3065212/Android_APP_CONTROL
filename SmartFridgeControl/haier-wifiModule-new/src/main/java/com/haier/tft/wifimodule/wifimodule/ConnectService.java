package com.haier.tft.wifimodule.wifimodule;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.haier.tft.wifimodule.alldata.DataToBytesForHeartBeat;
import com.haier.tft.wifimodule.alldata.DataToBytesForReportVersionToControlServer;
import com.haier.tft.wifimodule.connectgateserver.GetControlUrlAndPort;
import com.haier.tft.wifimodule.moduletool.BeatHeartThread;
import com.haier.tft.wifimodule.moduletool.BytesToInt;
import com.haier.tft.wifimodule.moduletool.ControlPrefence;
import com.haier.tft.wifimodule.connectgateserver.GetUrlAndPortResult;
import com.haier.tft.wifimodule.connectgateserver.GetUrlAndPortThread;
import com.haier.tft.wifimodule.moduletool.SSLSocketInitRunable;
import com.haier.tft.wifimodule.moduletool.SendCmdResult;
import com.haier.tft.wifimodule.moduletool.SocketInitResult;
import com.haier.tft.wifimodule.moduletool.SocketRevRunable;
import com.haier.tft.wifimodule.moduletool.SocketSendRunable;
import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/12/3.
 */
public class ConnectService {

    private String mUrl = "null";
    private int mPort = 0;
    private static ConnectService Instance = new ConnectService();
    private ExecutorService sendThreadPool = Executors.newFixedThreadPool(4);//初始化线程池，最多五个
    private Thread HeartBeat;
    //	private SSLsocketClient mSSLsocket;
    private ControlPrefence mControlPrefence =ControlPrefence.getInstance();
//    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;


    private ConnectService(){

    }
    public static ConnectService getInstance(){
        return Instance;
    }

    /**
     * 发送命令回调结果
     */
    private SendCmdResult mSendCmdResult = new SendCmdResult() {
        @Override
        public void success() {
            Log.i("sdk", "send cmd success");
        }

        @Override
        public void failed(String e) {
            Log.i("sdk", e);
            stop();

        }
    };

    /**
     * 获取控制地址和端口的回调函数
     */
    private GetUrlAndPortResult getUrlAndPortResult =new GetUrlAndPortResult(){

        @Override
        public void success(String url,int port){


            Log.i("sdk","GetUrlAndPortResult url= "+url+" port="+port);
//                String url = StaticValueAndConnectUrl.mySharedPreferences.getString(StaticValueAndConnectUrl.SharePreferenceName.controlUrl, "");
//                int port = StaticValueAndConnectUrl.mySharedPreferences.getInt(StaticValueAndConnectUrl.SharePreferenceName.controPort, 0);
                mUrl = url;
                mPort = port;

                SSLSocketInitRunable mSSLSocketThread = new SSLSocketInitRunable(mUrl, mPort, mSocketInitResult);
                Thread my = new Thread(mSSLSocketThread);
                my.start();

        }
        @Override
        public void failed(String e){
          //没有进行相应的操作，因为这个时候还没有初始化各参数，因此无需调用  stop();
            Log.i("sdk","GetUrlAndPortResult = "+e);
        }
    };

    /**
     * 初始化Socket回调函数
     */
    private SocketInitResult mSocketInitResult = new SocketInitResult() {
        @Override
        public void getSuccessResult(Socket mSocket, DataInputStream in, DataOutputStream out) {
            mControlPrefence.setIsSocketLive(true);
            mControlPrefence.setIsOnline(true);
            DataInputStream   inputStream = in;
            outputStream = out;

            /**
             * 此处进行接收
             * */
            StartRev(in);
          /**
             * 报告状态给控制服务器
          */
            DataToBytesForReportVersionToControlServer myData = new DataToBytesForReportVersionToControlServer();
            byte[] sendData = myData.getBytesVersion1(StaticValueAndConnectUrl.devtype, StaticValueAndConnectUrl.MAC);
            sendCmd(sendData);

            /**
             * 开始心跳
             */
            DataToBytesForHeartBeat mData = new DataToBytesForHeartBeat();
            byte[] data = mData.getHeartBeatData(StaticValueAndConnectUrl.MAC, (short) 50);
                 //TODO
            BeatHeartThread beatHeartThread =new BeatHeartThread(data,mControlPrefence,mSocket,Instance);
            HeartBeat = new Thread(beatHeartThread);
            HeartBeat.start();

        }

        @Override
        public void getFailed(String e) {
            Log.i("sdk", e);

            stop();

        }
    };



    public void stop() {
        mControlPrefence.setIsSocketLive(false);
//        inputStream=null;待定是否置为空，因为接收startRev一直在运行
        if(HeartBeat!=null) {
            if (!HeartBeat.isInterrupted()) {
                HeartBeat.interrupt();
            }
            try {
                HeartBeat.join();//并没后将startRev线程按此处理，因为read阻塞无用。
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        outputStream=null;
    }



    /**
     * 开启线程
     * @return
     */
    public void start() {


        //第一步是获取控制地址和端口
        GetUrlAndPortThread getUrlAndPortThread = new GetUrlAndPortThread(getUrlAndPortResult);
        Thread mGetUrlAndPortThread = new Thread(getUrlAndPortThread);
        mGetUrlAndPortThread.start();


    }


    public void sendCmd(byte[] data) {
        SocketSendRunable mysend = new SocketSendRunable(data,outputStream,mSendCmdResult);
//        Thread mthread = new Thread(mysend);
//        mthread.start();
        sendThreadPool.execute(mysend);
    }


    public void StartRev(DataInputStream inputStream) {
        SocketRevRunable myReceive = new SocketRevRunable(inputStream);
        Thread myThread = new Thread(myReceive);
        myThread.start();

    }




}

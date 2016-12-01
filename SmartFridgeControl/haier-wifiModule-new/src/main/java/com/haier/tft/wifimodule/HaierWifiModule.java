package com.haier.tft.wifimodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.haier.tft.wifimodule.alldata.BroadCastHashMap;
import com.haier.tft.wifimodule.moduletool.ControlPrefence;
import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;
import com.haier.tft.wifimodule.resolvingXml.DataHandle;
//import com.haier.tft.wifimodule.wifimodule.StartConnectService;
import com.haier.tft.wifimodule.wifimodule.ConnectService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 2015/9/26.
 */
public class HaierWifiModule {

    private TimerTask mTimerTask;
    private Context m_context;
    private ControlPrefence mControlPrefence;
    private volatile boolean hasStart=false;
    private long HeartTime =0;
    //    private  StartConnectService connectService;
    private  ConnectService connectService;
    private boolean endFlag=false;
    Thread checkSocketThread;
    Thread checkTypiedIsGetThread;
    Timer mTimer;

    //    private boolean contineFlag;
    protected HaierWifiModule(Context c)
    {
        m_context = c;
        try {
            if (StaticValueAndConnectUrl.MAC == null || StaticValueAndConnectUrl.MAC.length() == 0)
            {
                WifiManager wifi = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
                StaticValueAndConnectUrl.MAC = wifi.getConnectionInfo().getMacAddress();
                StaticValueAndConnectUrl.MAC = StaticValueAndConnectUrl.MAC.replace(":", "").toUpperCase();
                if( StaticValueAndConnectUrl.MAC!=null)
                    Log.i("sdk","now mac is = "+ StaticValueAndConnectUrl.MAC);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (StaticValueAndConnectUrl.MAC == null)
                StaticValueAndConnectUrl.MAC = "";
        }
        if(StaticValueAndConnectUrl.mySharedPreferences==null) {
            StaticValueAndConnectUrl.mySharedPreferences = c.getApplicationContext().getSharedPreferences("haier", Activity.MODE_PRIVATE);
        }
        mControlPrefence = ControlPrefence.getInstance();

    }

    private static HaierWifiModule _instance;
    public static  HaierWifiModule createInstance(Context c){

        sync(c);
        return _instance;
    }

    private synchronized static void sync(Context c){
        if (_instance == null)
        {
            Log.i("thread","create HaierWifiModule now thread =  "+Thread.currentThread().getName());
            _instance = new HaierWifiModule(c);
        }
    }

    public static HaierWifiModule getInstance(){
        if (_instance == null)
        {
            throw new IllegalArgumentException("create instance before get it...");
        }
        return _instance;
    }



    public void start() {

        Log.i("thread","has been start connectService");
        /**
         * 计时器，检测心跳时间是否超过一分钟，如果超过，则重新连接
         */

        /**
         *避免多次调用start
         */

        if(hasStart){
            Log.i("sdk","has been started so return and wont start again");
            Log.i("thread","has been started so return and wont start again");
            return;
        }else{
            Log.i("sdk","havenot  started so continue");
            Log.i("thread","havenot  started so continue");
            hasStart=true;
        }

        if (mControlPrefence == null) {
            mControlPrefence = ControlPrefence.getInstance();
        }
        mControlPrefence.setContinueFlag(true);


        if (mTimerTask == null) {

            mTimerTask = new TimerTask() {
                @Override
                public void run() {

                    long time = mControlPrefence.getHeartTime();
                    Log.i("heart","time = "+time);
                    if (time != 0) {
                        long mtime = mControlPrefence.getHeartTime();
                        if (System.currentTimeMillis() - mtime > StaticValueAndConnectUrl.HEART_BEAT_TIME_FOR_NOT) {
//70000)
                            if (connectService != null) {
                                mControlPrefence.setHeartTime(0);
                                mControlPrefence.setIsOnline(false);
                                mControlPrefence.setIsSocketLive(false);//ceshi
                                Log.i("sdk", " beatheart   timeout = " + time);
                                Log.i("heart", " beatheart   timeout = " + time);
          /*                       Log.i("heart", " beofore close beatheart timeout and close it ");
                                 mControlPrefence.setIsSocketLive(false);
                                 mControlPrefence.setIsOnline(false);

                                 connectService.close();
                                 Log.i("heart", " beatheart timeout and close it ");
                                 mControlPrefence.setHeartTime(0);*/

                            }

                        } else {
                            Log.i("sdk", " beatheart  not timeout = " + time);
                            Log.i("heart", " beatheart  not timeout = " + time);
                        }
                    }

                }
            };
            mTimer = new Timer();
            mTimer.schedule(mTimerTask, 5000, 5000);


        }
/**
 * 启动监听线程当连接socket为空时，每个半分钟连接一次服务器
 */
//        if(checkThread.getState().)

        checkSocketThread = new Thread() {
            @Override
            public void run() {
                connectService = ConnectService.getInstance();
                while (mControlPrefence.getContinueFlag()) {

                    Log.i("sdk", "now sdk flag is = " + mControlPrefence.getIsSocketLive()+" and thread name is ="+Thread.currentThread().getName());
                    Log.i("thread", "now sdk flag is = " + mControlPrefence.getIsSocketLive()+" and thread name is ="+Thread.currentThread().getName());

                    if (!mControlPrefence.getIsSocketLive()) {
                        connectService.start();
                        endFlag = false;
                    }
                    try {
                        sleep(StaticValueAndConnectUrl.CHECK_SOCKET_TIME);//  sleep(50000特意改为55，read超时设置为50，心跳为45秒一次一般来说，);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Log.e("sdk", "HaierWifiModule InterruptedException");
                        e.printStackTrace();

                    }

                }

            }
        };

        StaticValueAndConnectUrl.Handler = new Handler() {

            public void handleMessage(Message msg) {
                Bundle myBundle = msg.getData();


                //检测心跳，如果一分钟无回复，则重新连接
                if (msg.what == 0x02) {

                    if (mControlPrefence == null) {
                        mControlPrefence = ControlPrefence.getInstance();
                    }
                    Log.i("heart", "handle has get the heart time");
                    mControlPrefence.setHeartTime(System.currentTimeMillis());


                }


                //接收到从服务器发过来的数据
//                     StaticValueAndConnectUrl.GetDATAFROMSERVERFORHANDLE:
                if (msg.what == 0x01) {
                    Log.i("sdk", "getData from server");
                    final byte[] myDataBytes = myBundle.getByteArray("receive");
                    final int myDataBytesLengh = myBundle.getInt("lengh");
                    Log.i("sdk", "myDataBytesLengh= " + myDataBytesLengh);
                    new Thread() {
                        public void run() {

                            DataHandle myDataHandle = new DataHandle(myDataBytes, myDataBytesLengh);
                            myDataHandle.GetDataForSort();

                        }
                    }.start();

                }


                if (msg.what == 0x05) {
                    //直接转发给广播

                    String data = myBundle.getString("BroadCastKey");
                    Intent intent = new Intent();
                    intent.putExtra("sendControl", BroadCastHashMap.getBroadKey(data));
                    intent.setAction("com.haier.tft.control.pcb");
                    m_context.getApplicationContext().sendBroadcast(intent);


                }

                if (msg.what == 0x06) {
                    //用来启动checkThread

                    checkSocketThread.start();


                }

                super.handleMessage(msg);
            }
        };


        checkTypiedIsGetThread = new Thread(){
            @Override
            public void run(){
                StaticValueAndConnectUrl.devtype=mControlPrefence.getTypeid();
                while( mControlPrefence.getContinueFlag()&&StaticValueAndConnectUrl.devtype.length()!=64){
                    try {
                        StaticValueAndConnectUrl.devtype=mControlPrefence.getTypeid();
                        //如果还没有获取到则发送广播主动查询
                        Intent intent = new Intent();
                        intent.putExtra("sendControl","query");
                        intent.setAction("com.haier.tft.control.pcb");
                        m_context.getApplicationContext().sendBroadcast(intent);
                        Log.i("sdk","havenot got typied so broadcast again");
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("sdk","have got the typeid and now start socket typeid is "+ StaticValueAndConnectUrl.devtype);
                StaticValueAndConnectUrl.Handler.sendEmptyMessage(0x06);

            }
        };

        checkTypiedIsGetThread.start();

    }

    /**
     * 启动该线程后大约两分钟内关闭服务线程
     */

    public void stop(){

        hasStart=false;

        if(endFlag==false){
            endFlag =true;

            new Thread(){
                public void run(){

                    mControlPrefence.setContinueFlag(false);
                    mControlPrefence.setIsSocketLive(false);
                    try {
                        sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(mTimer!=null){
                        mTimer.cancel();
                        mTimer.purge();
                        mTimer=null;

                    }
                    if(mTimerTask!=null){
                        mTimerTask.cancel();
                        mTimerTask=null;
                    }

                    if(connectService !=null)
                        connectService.stop();

//
                    try {
                        if(checkTypiedIsGetThread!=null&&checkTypiedIsGetThread.isAlive()){
                            checkTypiedIsGetThread.interrupt();
                            checkTypiedIsGetThread.join();
                        }

                    } catch (InterruptedException e) {
                        Log.i("thread","checkTypiedIsGetThread InterruptedException "+e);
                        e.printStackTrace();
                    }catch(RuntimeException e){
                        Log.i("thread","checkTypiedIsGetThread RuntimeException "+e);
                        e.printStackTrace();
                    }

                    if(mTimer!=null){
                        mTimer.cancel();
                    }

                    try {
                        if(checkSocketThread!=null&&checkSocketThread.isAlive()){
                            checkSocketThread.interrupt();
                            checkSocketThread.join();
                        }

                    } catch (InterruptedException e) {
                        Log.i("thread","InterruptedException "+e);
                        e.printStackTrace();
                    }catch(RuntimeException e){
                        Log.i("thread","RuntimeException "+e);
                        e.printStackTrace();
                    }
                    Log.i("sdk","close thread done");

                }
            }.start();
        }


    }


}

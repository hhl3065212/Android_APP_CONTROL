package com.haiersmart.smartsale.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haiersmart.library.OKHttp.Http;
import com.haiersmart.library.OKHttp.HttpCallback;
import com.haiersmart.smartsale.application.SaleApplication;
import com.haiersmart.smartsale.constant.ConstantUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpService extends Service{
    private final String TAG = getClass().getSimpleName();
    private final String MAC = "112233445566";
    private HttpBinder binder;
    private static boolean isOnBroadcast = true;

    private List<UnlockListener> mUnlockListener = new ArrayList<>();
    public HttpService() {

    }


    public class HttpBinder extends Binder implements HttpServiceBind{

        @Override
        public void setOnUnlockListener(UnlockListener listener) {
            if(!mUnlockListener.contains(listener)) {
                mUnlockListener.add(listener);
            }
        }

        @Override
        public void removeOnUnlockListener(UnlockListener listener) {
            if(mUnlockListener.contains(listener)) {
                mUnlockListener.remove(listener);
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"HttpService onCreate");
        binder = new HttpBinder();
        new Thread(getRunnable).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    private Runnable getRunnable = new Runnable() {
        @Override
        public void run() {
            while (true){
                getIsUnlock();
                long endTime = System.currentTimeMillis()+500L;
                while (System.currentTimeMillis()<endTime);
            }
        }
    };

    public interface UnlockListener {
        void onUnlockListener(String userid);
    }

    private void getIsUnlock(){
        final JSONObject json = new JSONObject();
            json.put(ConstantUtil.HTTP_KEY_MAC, SaleApplication.get().getmMac());
        String url = ConstantUtil.URL+ConstantUtil.URL_GETLOCK;
        Http.post(url, json.toString(), new HttpCallback() {
            @Override
            public void onFailed(IOException e) {
                Log.i(TAG,e.toString());
            }

            @Override
            public void onSuccess(String body, String response) {
                JSONObject json = JSON.parseObject(body);
                String msg = json.getString(ConstantUtil.HTTP_KEY_MSG);
                String mac = json.getString(ConstantUtil.HTTP_KEY_MAC);
                String userid = json.getString(ConstantUtil.HTTP_KEY_USERID);
                String status = json.getString(ConstantUtil.HTTP_KEY_STATUS);
                if (msg.equals(ConstantUtil.HTTP_KEY_OK)){
                    if (mac.equals(SaleApplication.get().getmMac())){
                        if(status.equals("1")) {
                            Log.i(TAG, "unlock by userid="+userid);
                            sendBroadcastUnlock(userid);
                            doOnUnlockListener(userid);
                        }
                    }else {
                        Log.i(TAG,"Mac is not!");
                    }
                }

            }
        });
    }

    private void sendBroadcastUnlock(String userid){
        if (isOnBroadcast) {
            Intent intent = new Intent(ConstantUtil.HTTP_BROADCAST);
            intent.putExtra(ConstantUtil.HTTP_KEY_USERID, userid);
            sendBroadcast(intent);
        }
    }
    private void doOnUnlockListener(String userid){
        for (UnlockListener listener:mUnlockListener){
            listener.onUnlockListener(userid);
        }
    }

    public static void setIsOnBroadcast(boolean isOnBroadcast) {
        HttpService.isOnBroadcast = isOnBroadcast;
    }
}

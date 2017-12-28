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
import com.haiersmart.smartsale.constant.ConstantUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpService extends Service{
    private final String TAG = getClass().getSimpleName();
    private final String MAC = "112233445566";
    private HttpBinder binder;

    private List<UnlockListener> mUnlockListener = new ArrayList<>();
    public HttpService() {

    }


    public class HttpBinder extends Binder implements HttpServiceBind{

        @Override
        public void setOnUnlockListener(UnlockListener listener) {
            mUnlockListener.add(listener);
        }

        @Override
        public void removeOnUnlockListener(UnlockListener listener) {
            mUnlockListener.remove(listener);
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
        String url = "http://192.168.100.232/smartsale/getlock.php";
        final JSONObject json = new JSONObject();
            json.put("mac",MAC);

        Http.post(url, json.toString(), new HttpCallback() {
            @Override
            public void onFailed(IOException e) {

            }

            @Override
            public void onSuccess(String body, String response) {
                JSONObject json = JSON.parseObject(body);
                String msg = json.getString("msg");
                String mac = json.getString("mac");
                String userid = json.getString("userid");
                String status = json.getString("status");
                if (msg.equals("ok")){
                    if (mac.equals(MAC)){
                        if(status.equals("1")) {
                            Log.i(TAG, "unlock by userid="+userid);
                            sendBroadcastUnlock();
                            for (UnlockListener listener:mUnlockListener){
                                listener.onUnlockListener(userid);
                            }
                        }
                    }else {
                        Log.i(TAG,"Mac is not!");
                    }
                }

            }
        });
    }

    private void sendBroadcastUnlock(){
        Intent intent = new Intent(ConstantUtil.HTTP_BROADCAST);
        sendBroadcast(intent);
    }
}

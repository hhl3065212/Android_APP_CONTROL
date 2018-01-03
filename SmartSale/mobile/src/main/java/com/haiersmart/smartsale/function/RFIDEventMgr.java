package com.haiersmart.smartsale.function;

import android.content.BroadcastReceiver;
import android.util.Log;

import com.haiersmart.library.OKHttp.Http;
import com.haiersmart.library.OKHttp.HttpCallback;
import com.haiersmart.smartsale.application.SaleApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.haiersmart.smartsale.constant.ConstantUtil.URL_TEST_SERVER;

/**
 * Created by tingting on 2018/1/3.
 */

public class RFIDEventMgr {
    private static final String TAG = "RFIDEventMgr";
    private SaleApplication mApp;
    private int mUploadTimes;
    private String mJsonString;
    BroadcastReceiver mRfidBr;

    public RFIDEventMgr(SaleApplication app) {
        mApp = app;
    }

    public void upload2Network(String jsonString) throws JSONException {
        Log.d(TAG,"upload2Network");
        mJsonString = jsonString;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mac", mApp.get().getmMac());
        jsonObject.put("userid", "litingting");
        jsonObject.put("rfid", jsonString);
        Http.post( URL_TEST_SERVER, jsonObject.toString(), networkResponse );
    }

    HttpCallback networkResponse = new HttpCallback() {
        @Override
        public void onFailed(IOException e) {
            Log.i(TAG,"onFailed reason: " + e.toString());

            if(mUploadTimes > 0) {
               try {
                    upload2Network(mJsonString);
                    mUploadTimes--;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void onSuccess(String body, String response) {
            Log.i(TAG,"onSuccess !!!");
            mUploadTimes = 3;
        }
    };

}

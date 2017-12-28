/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2017/12/22
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.library.OKHttp;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/12/22
 * Author: Holy.Han
 * modification:
 */
public class Http {
    protected final String TAG = "Http";

    public static final String URL = "http://wine.haieco.com:80/linkcook/tft/rfid/save";


    public static final MediaType CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static final OkHttpClient Client = new OkHttpClient();

    public static void post(String url, String json, HttpCallback cb) {
        RequestBody requestBody = RequestBody.create(CONTENT_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = Client.newCall(request);
        call.enqueue(cb.callback);
    }

    public static void post(String json, HttpCallback cb){
        post(URL,json,cb);
    }

    public static void get(String url, HttpCallback cb) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = Client.newCall(request);
        call.enqueue(cb.callback);
    }

    public static void get(HttpCallback cb){
        get(URL,cb);
    }

    public static void sendTcp(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();
        Client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
        });
    }

}

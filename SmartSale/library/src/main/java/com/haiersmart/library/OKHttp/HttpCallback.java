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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/12/22
 * Author: Holy.Han
 * modification:
 */
public abstract class HttpCallback{
    public final String TAG = "HttpCallback";

    public HttpCallback() {
    }

    public Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            onFailed(e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            byte[] bytes = response.body().bytes();
            String string = new String(bytes);
            onSuccess(string,response.toString());
        }
    };

    public abstract void onFailed(IOException e);

    public abstract void onSuccess(String body, String response);
}

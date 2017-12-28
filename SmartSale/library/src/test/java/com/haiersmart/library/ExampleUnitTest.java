package com.haiersmart.library;

import com.alibaba.fastjson.JSONObject;
import com.haiersmart.library.OKHttp.Http;
import com.haiersmart.library.OKHttp.HttpCallback;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        String url = "https://www.baidu.com/";
        JSONObject[] list = new JSONObject[2];

        JSONObject appjson = new JSONObject();
        appjson.put("swVersion","JGN_66QK1_0022_1234");
        appjson.put("hwVersion","SC-FX-V1x-B");
        JSONObject datajson = new JSONObject();
        datajson.put("deviceModel","JC-66U1");
        datajson.put("deviceId","0123456789ab");
        datajson.put("showTemp",5);
        datajson.put("targetTemp",5);
        datajson.put("rfidlist",list);

        JSONObject json = new JSONObject();
        json.put("app",appjson);
        json.put("data",datajson);

        Http.post(json.toJSONString(),callback1);
        json.clear();
        Http.get(callback2);

        for (int i = 0; i < 100; i++) {
            System.out.println("我是主线程,线程Id为:" + Thread.currentThread().getId());
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    HttpCallback callback1= new HttpCallback() {
        @Override
        public void onFailure(IOException e) {
            System.out.println("我是异步线程1,timeout");
        }

        @Override
        public void onResponse(String body, String response) {
            System.out.println("我是异步线程1,线程Id为:" + Thread.currentThread().getId());
            System.out.println("我是异步线程1,response:" + body.toString());
        }

    };
    HttpCallback callback2= new HttpCallback() {
        @Override
        public void onFailure(IOException e) {
            System.out.println("我是异步线程2,timeout");
        }

        @Override
        public void onResponse(String body, String response) {
            System.out.println("我是异步线程2,线程Id为:" + Thread.currentThread().getId());
            System.out.println("我是异步线程2,response:" + response.toString());
        }

    };
}
package com.haiersmart.userdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haiersmart.library.OKHttp.Http;
import com.haiersmart.library.OKHttp.HttpCallback;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(id = R.id.btn_switch, click = true)
    Button btnSwitch;
    @Bind(id = R.id.txt_show)
    TextView txtShow;
    @Bind(id = R.id.txt_counts)
    TextView txtCounts;
    @Bind(id = R.id.lv_rfid)
    ListView lvRfid;

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private String mac = null;
    private String userid = "12356";
    private GetRfidHandler handler = new GetRfidHandler();
    private int counts = 0;
    private List<String> list;
    private int getCounts = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mac = data.getStringExtra("SCAN_RESULT");
                    //显示扫描到的内容
                    if (mac != null && !mac.isEmpty()) {
                        txtShow.setText("mac=" + mac);
                        getCounts = 10;
                        handler.sendEmptyMessage(0x01);
                    }
                    String url = "http://192.168.200.11/smartsale/unlock.php?mac=" + mac
                            + "&userid=" + userid + "&now=" + System.currentTimeMillis();
                    Http.get(url, new HttpCallback() {
                        @Override
                        public void onFailed(IOException e) {
                            Log.i(TAG, e.toString());
                        }

                        @Override
                        public void onSuccess(String body, String response) {
                            Log.i(TAG, body);
                        }
                    });
                    //显示
                    //                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

    private class GetRfidHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    if (mac != null && !mac.isEmpty()) {
                        final JSONObject json = new JSONObject();
                        json.put("mac", mac);
                        json.put("userid", userid);
                        Http.post("http://192.168.200.11/smartsale/getrfid.php", json.toString(), new HttpCallback() {
                            @Override
                            public void onFailed(IOException e) {

                            }

                            @Override
                            public void onSuccess(String body, String response) {
//                                Log.i(TAG, body);
                                JSONObject json = JSON.parseObject(body);
                                JSONObject rfid = json.getJSONObject("rfid");
                                if (list == null) {
                                    list = new ArrayList<>();
                                }
                                list.clear();
                                counts = 0;
                                if(rfid !=null) {
                                    counts = rfid.getInteger("counts");
                                    JSONArray epcid = rfid.getJSONArray("epcid");
                                    for (int i = 0; i < epcid.size(); i++) {
                                        list.add(epcid.getJSONObject(i).getString("epc"));
                                    }
                                    Collections.sort(list, new Comparator<String>() {
                                        @Override
                                        public int compare(String o1, String o2) {
                                            if (o1.length() < o2.length()) {
                                                return -1;
                                            } else if (o1.length() > o2.length()) {
                                                return 1;
                                            } else {
                                                return o1.compareTo(o2);
                                            }
                                        }
                                    });
                                    Log.i(TAG, list.toString());
                                }
                                handler.sendEmptyMessage(0x02);
                            }
                        });
                    }
                    break;
                case 0x02:
                    txtCounts.setText("RFID数量：" + counts);
                    lvRfid.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.listview_rfid, list));
                    getCounts--;
                    if(getCounts != 0){
                        handler.sendEmptyMessageDelayed(0x01,1000);
                    }
                    break;
            }
        }
    }
}

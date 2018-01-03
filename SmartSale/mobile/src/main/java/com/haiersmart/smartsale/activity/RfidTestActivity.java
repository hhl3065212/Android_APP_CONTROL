package com.haiersmart.smartsale.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.haiersmart.library.OKHttp.Http;
import com.haiersmart.library.OKHttp.HttpCallback;
import com.haiersmart.rfidlibrary.service.RFIDService;
import com.haiersmart.smartsale.R;
import com.haiersmart.smartsale.application.SaleApplication;
import com.haiersmart.smartsale.database.DBOperation;
import com.haiersmart.smartsale.function.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.haiersmart.smartsale.constant.ConstantUtil.URL_TEST_SERVER;


public class RfidTestActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "RfidTestActivity";
    public static final String BROADCAST_ACTION = "com.haiersmart.action.rfid";
    Button mBtnConnect,mBtnDisconnect,mBtnStart,mBtnStop,mBtnSort,mBtnUpload,mBtnBack;
    private TextView mTotalCount;

    private ListView mLVData;
    private MyAdapter mListAdapter;
    private List<Map<String, ?>> mListMs = new ArrayList<Map<String, ?>>();
    private String[] mConame = new String[] { "序号", "EPC ID", "次数", "天线",
            "协议", "RSSI", "频率", "附加数据 " };

    private RFIDService mService = null;
    private boolean mIsBind = false;

    MyBroadcastReceiver mBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private String mRfidInfo;
    private int mUploadTimes;
    private DBOperation mDBHandle;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RFIDService.MyBinder myBinder = (RFIDService.MyBinder) service;
            mService = myBinder.getService();
            mIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBind = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid_test);
        mBtnConnect =(Button) findViewById(R.id.button_connect);
        mBtnConnect.setOnClickListener(this);
        mBtnDisconnect =(Button) findViewById(R.id.button_disconnect);
        mBtnDisconnect.setOnClickListener(this);
        mBtnStart=(Button)findViewById(R.id.button_start);
        mBtnStart.setOnClickListener(this);
        mBtnStop=(Button)findViewById(R.id.button_stop);
        mBtnStop.setOnClickListener(this);
        mBtnSort=(Button)findViewById(R.id.button_sort);
        mBtnSort.setOnClickListener(this);
        mBtnUpload = (Button)findViewById(R.id.button_upload);
        mBtnUpload.setOnClickListener(this);
        mBtnBack=(Button)findViewById(R.id.button_back);
        mBtnBack.setOnClickListener(this);

        mTotalCount = (TextView) findViewById(R.id.textView_readallcnt);

        mLVData = (ListView) findViewById(R.id.listView_epclist);
        mLVData.setOnItemClickListener(this);
        mListAdapter = new MyAdapter(getApplicationContext(),
                mListMs, R.layout.listitemview_inv, mConame,
                new int[] { R.id.textView_readsort,
                        R.id.textView_readepc,
                        R.id.textView_readcnt,
                        R.id.textView_readant,
                        R.id.textView_readpro,
                        R.id.textView_readrssi,
                        R.id.textView_readfre,
                        R.id.textView_reademd
                });
        mLVData.setAdapter(mListAdapter);

        mBroadcastReceiver = new MyBroadcastReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BROADCAST_ACTION);

        mDBHandle = DBOperation.getInstance();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!mIsBind) {
            Intent intent = new Intent(this, RFIDService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mIsBind) {
            unbindService(conn);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_connect:
                try {
                    mService.connectRFID();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_disconnect:
                mService.disconnectRFID();
                break;
            case R.id.button_start:
                mService.startRead();
                break;
            case R.id.button_stop:
                mService.stopRead();
                break;
            case R.id.button_sort:
                sortTags();
                break;
            case R.id.button_upload:
                try {
                    mUploadTimes = 3;
                    upload2Network();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_back:
                finish();
                break;
            default:
                break;

        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setBackgroundColor(Color.YELLOW);
        HashMap<String,String> hm=(HashMap<String,String>)mLVData.getItemAtPosition(position);
        String epc=hm.get("EPC ID");
        for(int i=0;i<mLVData.getCount();i++)
        {
            if(i!=position)
            {
                View v=mLVData.getChildAt(i);
                if(v!=null)
                {ColorDrawable cd=(ColorDrawable) v.getBackground();
                    if(Color.YELLOW==cd.getColor())
                    {
                        int[] colors = {Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
                        v.setBackgroundColor(colors[i % 2]);// 每隔item之间颜色不同
                    }
                }
            }
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String TAG = "MyBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(null) && action.length() > 0) {
                if(action.equals(BROADCAST_ACTION)) {
                    mRfidInfo = intent.getStringExtra("rfidTags");
                    Message msg = new Message();
                    msg.what = 0x01;
                    updateUIHandler.sendMessage(msg);
                }
            }
        }
    }

    Handler updateUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    updateUIHandler.post(updateUIRunnable);
                    break;
                case 0x02:
                    break;
                default:
                    break;
            }
        }
    };


    private Runnable updateUIRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                JSONArray jsonArray = new JSONArray(mRfidInfo);
                int size = jsonArray.length();
                Log.i(TAG,"updateUIRunnable run rfid tags size: " + size);
                mListMs.clear();
                for (int i = 0; i < size; i++) {
                    //order
                    Map<String, String> m = new HashMap<String, String>();
                    m.put(mConame[0], String.valueOf(i));
                    //epcid
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String EPCID = jsonObject.getString("EpcId");
                    m.put(mConame[1], EPCID);
                    //count
                    int ReadCnt = jsonObject.getInt("ReadCnt");
                    String cs = m.get("次数");
                    if (cs == null)
                        cs = "0";
                    int isc = Integer.parseInt(cs) + ReadCnt;
                    m.put(mConame[2], String.valueOf(isc));
                    //antenna id
                    String AntennaID = jsonObject.getString("AntennaID");
                    m.put(mConame[3],AntennaID);
                    // protocol
                    m.put(mConame[4], "GEN2");
                    //RSSI
                    String RSSI = jsonObject.getString("RSSI");
                    m.put(mConame[5],RSSI);
                    String Frequency = jsonObject.getString("Frequency");
                    m.put(mConame[6],Frequency);
                    // other data , reserve
                    m.put(mConame[7], "                 ");
                    mListMs.add(m);
                }
                mListAdapter.notifyDataSetChanged();
                mTotalCount.setText(String.valueOf(size));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };

    private void sortTags() {
        int size  = mListMs.size();
        if(size>1) {
            Comparator comp1 = new Comparator< Map<String, String>>() {

                @Override
                public int compare(Map<String, String> o1, Map<String, String> o2) {
                    String a = o1.get(mConame[1]);
                    String b = o2.get(mConame[1]);
                    int lLength = a.length();
                    int lRight = b.length();
                    if(lLength < lRight) {
                        return -1;
                    } else if(lLength > lRight) {
                        return 1;
                    }
                    return a.compareTo(b);
                }
            };

            Collections.sort(mListMs, comp1);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private void upload2Network() throws JSONException {
        Log.i(TAG,"upload2Network in");

        JSONObject jsonObject= new JSONObject();

        jsonObject.put("mac", SaleApplication.get().getmMac());
        jsonObject.put("userid", "litingting");

        int size = mListMs.size();
        JSONArray epcJa= new JSONArray();
        for(int i=0; i<size; i++) {
            Map<String, String> map = (Map<String, String>) mListMs.get(i);
            String epcstr =  map.get(mConame[1]);
            JSONObject epcJo = new JSONObject();
            epcJo.put("epc", epcstr);
            epcJa.put(epcJo);
        }

        JSONObject rfidJo = new JSONObject();
        rfidJo.put("counts", size);
        rfidJo.put("epcid",epcJa);

        jsonObject.put("rfid",rfidJo);

        String jsonNt = jsonObject.toString();

        Http.post( URL_TEST_SERVER, jsonNt, networkResponse );
        //mDBHandle.getRFIDDbMgr().insert(jsonObject);//TODO
        Log.i(TAG,"upload2Network out");
    }

    HttpCallback networkResponse = new HttpCallback() {
        @Override
        public void onFailed(IOException e) {
            Log.i(TAG,"onFailed reason: " + e.toString());

            if(mUploadTimes > 0) {
                try {
                    upload2Network();
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

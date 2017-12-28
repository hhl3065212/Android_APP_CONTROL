package com.haiersmart.userdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haiersmart.library.OKHttp.Http;
import com.haiersmart.library.OKHttp.HttpCallback;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(id = R.id.btn_switch,click = true)
    Button btnSwitch;
    @Bind(id = R.id.txt_show)
    TextView txtShow;

    private final static int SCANNIN_GREQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                if(resultCode == RESULT_OK){
                    String res = data.getStringExtra("SCAN_RESULT");
                    //显示扫描到的内容
                    txtShow.setVisibility(View.VISIBLE);
                    txtShow.setText(res);
                    Http.get(res+"&userid=12356&now="+System.currentTimeMillis(), new HttpCallback() {
                        @Override
                        public void onFailed(IOException e) {

                        }

                        @Override
                        public void onSuccess(String body, String response) {

                        }
                    });
                    //显示
//                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }
}

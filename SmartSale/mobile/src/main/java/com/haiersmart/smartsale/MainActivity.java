package com.haiersmart.smartsale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.haiersmart.library.MediaPlayer.PlayFixedVoice;
import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;


public class MainActivity extends Activity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    @Bind(id = R.id.btn_open)
    private Button btnOpen;
    @Bind(id = R.id.btn_select)
    private Button btnSelect;
    @Bind(id = R.id.btn_final)
    private Button btnFinal;
    @Bind(id = R.id.btn_pay)
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinder.bind(this);
        setOnClick();

    }

    private void setOnClick(){
        btnOpen.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnFinal.setOnClickListener(this);
        btnPay.setOnClickListener(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open:
                PlayFixedVoice.playVoice(PlayFixedVoice.OPEN);
                break;
            case R.id.btn_select:
                PlayFixedVoice.playVoice(PlayFixedVoice.SELECT);
                break;
            case R.id.btn_final:
                PlayFixedVoice.playVoice(PlayFixedVoice.FINAL);
                break;
            case R.id.btn_pay:
                PlayFixedVoice.playVoice(PlayFixedVoice.PAY);
                break;
        }
    }
}

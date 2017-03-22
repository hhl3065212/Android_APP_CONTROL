package com.haiersmart.sfcontrol.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiersmart.imagerecognition.CaptureImageFromUsbCamera;
import com.haiersmart.sfcontrol.R;

public class CameraUsbActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout tft_camera_usb_layout;

    LinearLayout tft_camera_usb_button_layout;

    Button btn_camera_usb_ret;
    TextView txt_camera_mipi;
    CaptureImageFromUsbCamera tft_camera_usb_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tft_camera_usb);
        setOnclick();
        if (tft_camera_usb_view != null)
            tft_camera_usb_view.startCaptureImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setOnclick() {
        tft_camera_usb_view = (CaptureImageFromUsbCamera)findViewById(R.id.tft_camera_usb_view);
        txt_camera_mipi = (TextView)findViewById(R.id.txt_camera_mipi) ;
        btn_camera_usb_ret = (Button) findViewById(R.id.btn_camera_usb_ret);
        btn_camera_usb_ret.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera_usb_ret: {
                /*if (tft_camera_usb_view != null)
                    tft_camera_usb_view.stopCaptureImage();*/
                finish();
            }
            break;
            default:
                break;
        }
    }
}

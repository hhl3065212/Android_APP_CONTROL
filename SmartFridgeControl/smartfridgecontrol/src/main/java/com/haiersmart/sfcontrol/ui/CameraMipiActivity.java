package com.haiersmart.sfcontrol.ui;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.io.IOException;
import java.util.List;

public class CameraMipiActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    LinearLayout tft_camera_mipi_layout;
    LinearLayout tft_camera_mipi_button_layout;
    SurfaceView tft_camera_mipi_view;
    Button btn_camera_mipi_add;
    Button btn_camera_mipi_sub;
    Button btn_camera_mipi_ret;

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera = null;
    Camera.Parameters mParameters;
    private int mMaxZoom = 0;
    private int mZoom = 0;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tft_camera_mipi);
        findView();
        setOnclick();
        mSurfaceHolder = tft_camera_mipi_view.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setFixedSize(1280,720);
        mSurfaceHolder.addCallback(this);
    }
    private void findView(){
        tft_camera_mipi_layout = (LinearLayout)findViewById(R.id.tft_camera_mipi_layout);
        tft_camera_mipi_button_layout = (LinearLayout)findViewById(R.id.tft_camera_mipi_button_layout);;
        tft_camera_mipi_view = (SurfaceView)findViewById(R.id.tft_camera_mipi_view);;
        btn_camera_mipi_add = (Button)findViewById(R.id.btn_camera_mipi_add);
        btn_camera_mipi_sub = (Button)findViewById(R.id.btn_camera_mipi_sub);
        btn_camera_mipi_ret = (Button)findViewById(R.id.btn_camera_mipi_ret);
    }

    private void setOnclick() {
        tft_camera_mipi_view.setOnClickListener(this);
        btn_camera_mipi_add.setOnClickListener(this);
        btn_camera_mipi_sub.setOnClickListener(this);
        btn_camera_mipi_ret.setOnClickListener(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //获取camera对象
        if(mCamera == null)
        {
            mCamera = Camera.open();
        }
        if(mCamera != null) {
            try {
                //设置预览监听
                mCamera.setPreviewDisplay(holder);
                mParameters = mCamera.getParameters();

                if (this.getResources().getConfiguration().orientation
                        != Configuration.ORIENTATION_LANDSCAPE) {
                    mParameters.set("orientation", "portrait");
                    mCamera.setDisplayOrientation(90);
                    mParameters.setRotation(90);
                } else {
                    mParameters.set("orientation", "landscape");
                    mCamera.setDisplayOrientation(0);
                    mParameters.setRotation(0);
                }
                List<Camera.Size> supportedPreviewSizes = mParameters.getSupportedPreviewSizes();
                for(Camera.Size size:supportedPreviewSizes) {
                    MyLogUtil.i("Camera size:" + size.width+"*"+size.height);
                }
                mParameters.setPreviewSize(1920,1080);
                mMaxZoom = mParameters.getMaxZoom();
                mCamera.setParameters(mParameters);
                //启动摄像头预览
                mCamera.startPreview();
                //            System.out.println("camera.startpreview");

            } catch (IOException e) {
                e.printStackTrace();
                mCamera.release();
                //            System.out.println("camera.release");
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            //摄像头画面显示在Surface上
            mCamera.setPreviewDisplay(holder);
            mParameters = mCamera.getParameters();
            mParameters.setPreviewSize(width, height);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
        } catch (IOException e) {
            if (mCamera != null)
            {
                mCamera.release();
            }
            mCamera = null;
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null; // 记得释放
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera_mipi_sub: {
                mZoom = mParameters.getZoom();
                if(mZoom>0){
                    mZoom--;
                }
                mParameters = mCamera.getParameters();
                mParameters.setZoom(mZoom);
                mCamera.setParameters(mParameters);
            }
            break;
            case R.id.btn_camera_mipi_add: {
                mZoom = mParameters.getZoom();
                if(mZoom<mMaxZoom){
                    mZoom++;
                }
                mParameters = mCamera.getParameters();
                mParameters.setZoom(mZoom);
                mCamera.setParameters(mParameters);
            }
            break;
            case R.id.btn_camera_mipi_ret: {
                finish();
            }
            break;
            default:
                break;
        }
    }

}

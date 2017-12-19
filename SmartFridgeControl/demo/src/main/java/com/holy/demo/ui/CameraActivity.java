package com.holy.demo.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.holy.demo.R;
import com.holy.demo.constant.Bind;
import com.holy.demo.constant.ViewBinder;

import java.io.IOException;
import java.util.List;

import static android.R.attr.width;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    final String TAG = getClass().getSimpleName();
    @Bind(R.id.linear_camera)
    LinearLayout mLinearCamera;
    @Bind(R.id.btn_camera_return)
    Button btnCameraReturn;
    SurfaceView mCameraSurface;
    SurfaceHolder mCameraHolder;
    Camera mCamera;
    Camera.Parameters mParameters;
    int mWidthPreview = 0;
    int mHeightPreview = 0;
    int mWidthSurface = 0;
    int mHeightSurface = 0;
    private int mMaxZoom = 0;
    private int mZoom = 0;
    ViewTreeObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ViewBinder.bind(this);
//        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        btnCameraReturn.setOnClickListener(this);
        initCamera();
        observer = mLinearCamera.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLinearCamera.getViewTreeObserver().removeOnPreDrawListener(this);
                getSize();
                initView();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void getSize() {
        Log.d(TAG, "getSize");
        mWidthSurface = mLinearCamera.getMeasuredWidth();
        mHeightSurface = mLinearCamera.getMeasuredHeight();
        Log.d(TAG, "size = " + mWidthSurface + "*" + mHeightSurface);
        float k = (float) width / mWidthPreview;
        if ((int) (k * mHeightPreview) > mHeightSurface) {
            k = (float) mHeightSurface / mHeightPreview;
            mWidthSurface = (int) (k * mWidthPreview);
        } else {
            mHeightSurface = (int) (k * mHeightPreview);
        }
        if (k == 0) {
            mWidthSurface = mWidthPreview;
            mHeightSurface = mHeightPreview;
        }
        Log.d(TAG, "change size = " + mWidthSurface + "*" + mHeightSurface);
    }

    void initCamera() {
        Log.d(TAG, "initCamera");
        int number = Camera.getNumberOfCameras();
        if(number ==0){
            return;
        }
        if (mCamera == null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            mCamera = Camera.open();
        }
        if (mCamera != null) {

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
            mWidthPreview = supportedPreviewSizes.get(4).width;
            mHeightPreview = supportedPreviewSizes.get(4).height;
//            getMaxPreviewSize();
            mParameters.setPreviewSize(mWidthPreview, mHeightPreview);
            mMaxZoom = mParameters.getMaxZoom();

            mCamera.setParameters(mParameters);
        }
    }
    void getMaxPreviewSize(){
        List<Camera.Size> supportedPreviewSizes = mParameters.getSupportedPreviewSizes();
        long[] max = new long[supportedPreviewSizes.size()];
        for(int i=0;i<supportedPreviewSizes.size();i++){
            long width = supportedPreviewSizes.get(i).width;
            long height = supportedPreviewSizes.get(i).height;
            max[i] = width*height;
        }
        long maxSize = max[0];
        int count =0;
        for(int i=0;i<max.length;i++){
            if(max[i]>maxSize){
                maxSize = max[i];
                count = i;
            }
        }
        mWidthPreview = supportedPreviewSizes.get(count).width;
        mHeightPreview = supportedPreviewSizes.get(count).height;
    }

    void initView() {
        Log.d(TAG, "initView");

        if (mCameraSurface == null) {
            mCameraSurface = new SurfaceView(this);
        }
        mCameraSurface.setLayoutParams(new ViewGroup.LayoutParams(mWidthSurface, mHeightSurface));
        mLinearCamera.removeAllViews();
        mLinearCamera.addView(mCameraSurface);
        mCameraHolder = mCameraSurface.getHolder();
        mCameraHolder.addCallback(this);
        mCameraHolder.setSizeFromLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            if (mCamera != null) {
                mCamera.release();
            }
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera_return:
                finish();
                break;
        }
    }
}

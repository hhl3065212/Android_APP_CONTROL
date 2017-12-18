package com.holy.demo.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/11/22
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class CameraFragment extends Fragment implements View.OnClickListener,SurfaceHolder.Callback{
    final String TAG = getClass().getSimpleName();

@Bind(R.id.btn_camera_show)
    Button btnCameraShow;
//    @Bind(R.id.linear_camera_f)
    LinearLayout mLinearCamera;
    SurfaceView mCameraSurface;
    SurfaceHolder mCameraHolder;
    Camera mCamera;
    Camera.Parameters mParameters;
    int mWidthPreview = 0;
    int mHeightPreview = 0;
    int mWidthSurface = 0;
    int mHeightSurface = 0;
    ViewTreeObserver observer;
    boolean NEW_VIEW = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_camera, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        ViewBinder.bind(this);
        btnCameraShow.setOnClickListener(this);
        if(!NEW_VIEW) {
            initCamera();
            mLinearCamera = (LinearLayout) getView().findViewById(R.id.linear_camera_f);
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera_show:
                startActivity(new Intent(this.getActivity(),CameraActivity.class));
                break;
        }
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
        if (mCamera == null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            mCamera = Camera.open(0);
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
            mWidthPreview = supportedPreviewSizes.get(0).width;
            mHeightPreview = supportedPreviewSizes.get(0).height;
            mParameters.setPreviewSize(mWidthPreview, mHeightPreview);
//            mMaxZoom = mParameters.getMaxZoom();

            mCamera.setParameters(mParameters);
        }
    }

    void initView() {
        Log.d(TAG, "initView");

        mCameraSurface = null;
        mCameraSurface = new SurfaceView(this.getActivity());

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
}

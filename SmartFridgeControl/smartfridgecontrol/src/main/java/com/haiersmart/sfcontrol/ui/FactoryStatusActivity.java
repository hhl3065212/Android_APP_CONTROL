/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 工厂测试界面
 * Author:  Holy.Han
 * Date:  2016/11/30
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.service.MainBoardParameters;

/**
 * <p>function: </p>
 * <p>description:  工厂测试界面</p>
 * history:  1. 2016/11/30
 * Author: Holy.Han
 * modification:
 */
public class FactoryStatusActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "FactoryStatusActivity";

    private MainBoardParameters mMBParam;
    private String mFridgeModel;
    RadioButton rbtVersion,rbtReset,rbtStatus,rbtCamera,rbtTP,rbtAudio,rbtMarket,rbtDebug;
    LinearLayout llVersion,llReset,llStatus,llCamera,llTP,llAudio,llMarket,llDebug;
    TextView tvFridgeModel;
    Button btnReturn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stauts_factory);
        findViews();
        mMBParam = MainBoardParameters.getInstance();
        mFridgeModel = mMBParam.getFridgeType();
        initViews();
    }

    private void findViews() {
        rbtVersion = (RadioButton)findViewById(R.id.rbt_factory_version);
        rbtReset = (RadioButton)findViewById(R.id.rbt_factory_reset);
        rbtStatus = (RadioButton)findViewById(R.id.rbt_factory_status);
        rbtCamera = (RadioButton)findViewById(R.id.rbt_factory_camera);
        rbtTP = (RadioButton)findViewById(R.id.rbt_factory_TP);
        rbtAudio = (RadioButton)findViewById(R.id.rbt_factory_audio);
        rbtMarket = (RadioButton)findViewById(R.id.rbt_factory_market);
        rbtDebug = (RadioButton)findViewById(R.id.rbt_factory_debug);
        rbtVersion.setOnClickListener(this);
        rbtReset.setOnClickListener(this);
        rbtStatus.setOnClickListener(this);
        rbtCamera.setOnClickListener(this);
        rbtTP.setOnClickListener(this);
        rbtAudio.setOnClickListener(this);
        rbtMarket.setOnClickListener(this);
        rbtDebug.setOnClickListener(this);

        llVersion = (LinearLayout) findViewById(R.id.linear_factory_version);
        llReset = (LinearLayout)findViewById(R.id.linear_factory_reset);
        llStatus = (LinearLayout)findViewById(R.id.linear_factory_status);
        llCamera = (LinearLayout)findViewById(R.id.linear_factory_camera);
        llTP = (LinearLayout)findViewById(R.id.linear_factory_TP);
        llAudio = (LinearLayout)findViewById(R.id.linear_factory_audio);
        llMarket = (LinearLayout)findViewById(R.id.linear_factory_market);
        llDebug = (LinearLayout)findViewById(R.id.linear_factory_debug);



        tvFridgeModel = (TextView)findViewById(R.id.text_factory_fridge_model);

        btnReturn = (Button)findViewById(R.id.btn_factory_return);
        btnReturn.setOnClickListener(this);
    }
    private void initViews(){
        llVersion.setVisibility(View.VISIBLE);
        if(mFridgeModel.equals(ConstantUtil.BCD251_MODEL)){
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
        }
        initStatusView();
        initDebugView();
    }

    private void initStatusView(){
        if(mFridgeModel.equals(ConstantUtil.BCD251_MODEL)){
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
        }
    }
    private void initDebugView(){
        if(mFridgeModel.equals(ConstantUtil.BCD251_MODEL)){
            rbtVersion.setVisibility(View.VISIBLE);
            rbtReset.setVisibility(View.VISIBLE);
            rbtStatus.setVisibility(View.VISIBLE);
            rbtCamera.setVisibility(View.GONE);
            rbtTP.setVisibility(View.VISIBLE);
            rbtAudio.setVisibility(View.VISIBLE);
            rbtMarket.setVisibility(View.GONE);
            rbtDebug.setVisibility(View.GONE);
        }
    }

    private void addlisteners() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_factory_return:
                finish();
                break;
            default:
                setLinearContent(v.getId());
                break;
        }
    }
    private void setLinearContent(int num){
        llVersion.setVisibility(View.GONE);
        llReset.setVisibility(View.GONE);
        llStatus.setVisibility(View.GONE);
        llCamera.setVisibility(View.GONE);
        llTP.setVisibility(View.GONE);
        llAudio.setVisibility(View.GONE);
        llMarket.setVisibility(View.GONE);
        llDebug.setVisibility(View.GONE);
        switch (num){
            case R.id.rbt_factory_version:
                llVersion.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_reset:
                llReset.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_status:
                llStatus.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_camera:
                llStatus.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_TP:
                llTP.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_audio:
                llAudio.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_market:
                llMarket.setVisibility(View.VISIBLE);
                break;
            case R.id.rbt_factory_debug:
                llDebug.setVisibility(View.VISIBLE);
                break;
        }
    }
}

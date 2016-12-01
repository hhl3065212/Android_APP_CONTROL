package com.haiersmart.sfcontrol.service.mbmodel;

import com.haiersmart.sfcontrol.database.FridgeInfoDbMgr;
import com.haiersmart.sfcontrol.database.FridgeInfoEntry;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class MainBoardInfo {
    private final String TAG = "MainBoardInfo";
    private String fridgeId;//typeid
    private String fridgeVersion;//版本号
    private String fridgeFactory;//厂家
    private String fridgeSn;//专用号
    private FridgeInfoDbMgr mFridgeInfoDbMgr;

    public MainBoardInfo() {
        init();
    }

    private void init() {
        String defaultFridgeId = "111c12002400081001030061800118420000000000";//typeid default 251
        mFridgeInfoDbMgr = FridgeInfoDbMgr.getInstance();
        FridgeInfoEntry mFridgeInfoEntry = new FridgeInfoEntry();
        mFridgeInfoEntry.name = "fridgeId";
        mFridgeInfoDbMgr.queryByName(mFridgeInfoEntry);
        if (mFridgeInfoEntry.value != null) {
            if(! mFridgeInfoEntry.value.equals("")){
                defaultFridgeId = mFridgeInfoEntry.value;
            }
        }
        MyLogUtil.i(TAG,"defaultFridgeId:"+defaultFridgeId);
        updateBoardInfo(defaultFridgeId);
    }

    public void updateBoardInfo(String fridgeId) {
        setFridgeId(fridgeId);
        setFridgeVersion();
        setFridgeFactory();
        setFridgeSn();
        MyLogUtil.i(TAG,"fridgeId:"+fridgeId);
    }

    public void updateBoardInfo(byte[] dataFrame) {
        setFridgeId(dataFrame);
        setFridgeVersion();
        setFridgeFactory();
        setFridgeSn();
        MyLogUtil.i(TAG,"fridgeId:"+fridgeId);
    }

    public String getFridgeId() {
        return fridgeId;
    }

    private void setFridgeId(String fridgeId) {
        this.fridgeId = fridgeId;
    }

    private void setFridgeId(byte[] dataFrame) {
        StringBuffer stringBuffer = new StringBuffer();//by holy
        stringBuffer.append("");//by holy
        int end = dataFrame[2] + 2;
        for (int counts = 4; counts < end; counts++) {//by holy
            stringBuffer.append(String.format("%02x", dataFrame[counts]));//by holy
        }//by holy
        this.fridgeId = stringBuffer.toString();
    }

    public String getFridgeVersion() {
        return fridgeVersion;
    }

    private void setFridgeVersion(String fridgeVersion) {
        this.fridgeVersion = fridgeVersion;
    }

    private void setFridgeVersion() {
        this.fridgeVersion = this.fridgeId.substring(0, 2);
    }

    public String getFridgeFactory() {
        return fridgeFactory;
    }

    private void setFridgeFactory(String fridgeFactory) {
        this.fridgeFactory = fridgeFactory;
    }

    private void setFridgeFactory() {
        this.fridgeFactory = this.fridgeId.substring(2, 16);
    }

    public String getFridgeSn() {
        return fridgeSn;
    }

    private void setFridgeSn(String fridgeSn) {
        this.fridgeSn = fridgeSn;
    }

    private void setFridgeSn() {
        this.fridgeSn = this.fridgeId.substring(16);
    }
}

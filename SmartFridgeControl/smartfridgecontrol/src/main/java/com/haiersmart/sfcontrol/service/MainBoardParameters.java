/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 单例，主控板所有参数
 * Author:  Holy.Han
 * Date:  2016/11/28
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service;

import com.haiersmart.sfcontrol.service.configtable.TargetTempRange;
import com.haiersmart.sfcontrol.service.mbmodel.MainBoardEntry;
import com.haiersmart.sfcontrol.service.powerctl.SerialData;
import com.haiersmart.sfcontrol.utilslib.PrintUtil;

import java.util.ArrayList;

/**
 * <p>function: </p>
 * <p>description:  单例，主控板所有参数</p>
 * history:  1. 2016/11/28
 * Author: Holy.Han
 * modification: create
 */
public class MainBoardParameters {
    private SerialData mSerialData;
    private static MainBoardParameters instance;

    public static synchronized MainBoardParameters getInstance() {
        if (instance == null) {
            synchronized (MainBoardParameters.class) {
                if (instance == null)
                    instance = new MainBoardParameters();
            }
        }
        return instance;
    }

    private MainBoardParameters() {
        mSerialData = SerialData.getInstance();
    }

    /**
     * 获得主控板控制类
     * @return
     */
    public ArrayList<MainBoardEntry> getMainBoardControl(){
        return mSerialData.getMainBoardControl();
    }

    /**
     * 通过名字获得主控板控制类值
     * @param name
     * @return
     */
    public int getMbcValueByName(String name){
        return mSerialData.getMbcValueByName(name);
    }

    /**
     * 获得主控板状态类
     * @return
     */
    public ArrayList<MainBoardEntry> getMainBoardStatus(){
        return mSerialData.getMainBoardStatus();
    }

    /**
     * 通过名字获得主控板状态类值
     * @param name
     * @return
     */
    public int getMbsValueByName(String name){
        return mSerialData.getMbsValueByName(name);
    }
    /**
     * 获得主控板调试类
     * @return
     */
    public ArrayList<MainBoardEntry> getMainBoardDebug() {
        return mSerialData.getMainBoardDebug();
    }
    /**
     * 通过名字获得主控板调试类值
     * @param name
     * @return
     */
    public int getMbdValueByName(String name){
        return mSerialData.getMbdValueByName(name);
    }

    public byte[] getDataBaseToBytes(){
        return mSerialData.setDataBaseToBytes();
    }
    public String getTypeId(){
        return mSerialData.getMainBoardInfo().getTypeId();
    }

    public String getFridgeId() {
        return mSerialData.getMainBoardInfo().getFridgeId();
    }
    public String getFridgeVersion(){
        return mSerialData.getMainBoardInfo().getFridgeVersion();
    }
    public String getFridgeFactory(){
        return mSerialData.getMainBoardInfo().getFridgeFactory();
    }
    public String getFridgeSn(){
        return mSerialData.getMainBoardInfo().getFridgeSn();
    }
    public String getFridgeType(){
        return mSerialData.getCurrentModel();
    }
    /**
     * 获得档位温度范围
     * @return TargetTempRange
     */
    public TargetTempRange getTargetTempRange(){
        return mSerialData.getTargetTempRange();
    }

    /**
     * 获得数组型主控板状态码
     * @return byte[]
     */
    public byte[] getFrameDataBytes(){
        return mSerialData.getFrameData();
    }

    /**
     * 获得字符串型主控板状态码
     * @return
     */
    public String getFrameDataString(){
        String res = PrintUtil.BytesToString(mSerialData.getFrameData(),16);
        return res;
    }

    /**
     * 获得系统OS的版本号
     * @return
     */
    public String getOSVersion(){
        return "OS_"+mSerialData.getmOSVersion();
    }
    public String getOSType(){
        return mSerialData.getmOSType();
    }
}

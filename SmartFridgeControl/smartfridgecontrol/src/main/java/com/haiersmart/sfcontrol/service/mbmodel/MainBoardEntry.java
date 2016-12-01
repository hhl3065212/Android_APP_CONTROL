package com.haiersmart.sfcontrol.service.mbmodel;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class MainBoardEntry {
    private String Name;//参数名称
    private int Value;//参数状态 可以 打开 关闭 具体数值
    private int StartByte;//
    private int ByteShift;//
    private int DiffValue;

    public MainBoardEntry(String name, int value, int startByte, int byteShift,int diffValue) {
        Name = name;
        Value = value;
        StartByte = startByte;
        ByteShift = byteShift;
        DiffValue = diffValue;
    }

    public MainBoardEntry(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public int getStartByte() {
        return StartByte;
    }

    public int getByteShift() {
        return ByteShift;
    }

    public int getDiffValue() {
        return DiffValue;
    }
}

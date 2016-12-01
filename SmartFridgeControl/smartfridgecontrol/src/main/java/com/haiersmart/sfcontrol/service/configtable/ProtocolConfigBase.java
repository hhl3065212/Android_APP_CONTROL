package com.haiersmart.sfcontrol.service.configtable;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class ProtocolConfigBase {
    private String Name;//参数名称
    private int Direction;//信息方向 0：下发控制  1：上报状态
    private int StartByte;//状态字位置
    private int ByteShift;//状态解析位 0~7:位  8:1字节  16:2字节 9:冷藏关闭特殊处理 10:不更新
    private int MinValue;//最小值
    private int MaxValue;//最大值
    private int DiffValue;//档位与温度对应差值 档位值-温度值=差值

    public ProtocolConfigBase(String name, int direction, int startByte, int byteShift) {
        Name = name;
        Direction = direction;
        StartByte = startByte;
        ByteShift = byteShift;
        MinValue = 0;//最小值
        MaxValue = 0;//最大值
        DiffValue = 0;//档位与温度对应差值 档位值-温度值=差值
    }
    public ProtocolConfigBase(String name, int direction, int startByte, int byteShift,int diffValue) {
        Name = name;
        Direction = direction;
        StartByte = startByte;
        ByteShift = byteShift;
        MinValue = 0;//最小值
        MaxValue = 0;//最大值
        DiffValue = diffValue;//档位与温度对应差值 档位值-温度值=差值
    }
    public ProtocolConfigBase(String name, int direction, int startByte, int byteShift,int minValue,int maxValue,int diffValue) {
        Name = name;
        Direction = direction;
        StartByte = startByte;
        ByteShift = byteShift;
        MinValue = minValue;
        MaxValue = maxValue;
        DiffValue = diffValue;
    }

    public String getName() {
        return Name;
    }

    public int getDirection() {
        return Direction;
    }

    public int getStartByte() {
        return StartByte;
    }

    public int getByteShift() {
        return ByteShift;
    }

    public int getMinValue() {
        return MinValue;
    }

    public int getMaxValue() {
        return MaxValue;
    }

    public int getDiffValue() {
        return DiffValue;
    }
}

package com.haiersmart.sfcontrol.utilslib;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/24
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class BitOpt {
    private final String TAG = "BitOpt";

    public static byte BIT0 = (byte)0x01;
    public static byte BIT1 = (byte)0x02;
    public static byte BIT2 = (byte)0x04;
    public static byte BIT3 = (byte)0x08;
    public static byte BIT4 = (byte)0x10;
    public static byte BIT5 = (byte)0x20;
    public static byte BIT6 = (byte)0x40;
    public static byte BIT7 = (byte)0x80;

    public static void Bit(Byte b,byte pos){
        b = (byte) (b|pos);
    }
}

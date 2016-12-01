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

public class MyByte {
    private byte value;

    private final byte BIT0 = (byte) 0x01;
    private final byte BIT1 = (byte) 0x02;
    private final byte BIT2 = (byte) 0x04;
    private final byte BIT3 = (byte) 0x08;
    private final byte BIT4 = (byte) 0x10;
    private final byte BIT5 = (byte) 0x20;
    private final byte BIT6 = (byte) 0x40;
    private final byte BIT7 = (byte) 0x80;

    public MyByte(byte value) {
        this.value = value;
    }

    public MyByte() {
        this.value = 0;
    }

    public MyByte(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public void setBit0() {
        value = (byte) (value | BIT0);
    }

    public void clrBit0() {
        value = (byte) (value & (~BIT0));
    }

    public void setBit1() {
        value = (byte) (value | BIT1);
    }

    public void clrBit1() {
        value = (byte) (value & (~BIT1));
    }

    public void setBit2() {
        value = (byte) (value | BIT2);
    }

    public void clrBit2() {
        value = (byte) (value & (~BIT2));
    }

    public void setBit3() {
        value = (byte) (value | BIT3);
    }

    public void clrBit3() {
        value = (byte) (value & (~BIT3));
    }

    public void setBit4() {
        value = (byte) (value | BIT4);
    }

    public void clrBit4() {
        value = (byte) (value & (~BIT4));
    }

    public void setBit5() {
        value = (byte) (value | BIT5);
    }

    public void clrBit5() {
        value = (byte) (value & (~BIT5));
    }

    public void setBit6() {
        value = (byte) (value | BIT6);
    }

    public void clrBit6() {
        value = (byte) (value & (~BIT6));
    }

    public void setBit7() {
        value = (byte) (value | BIT7);
    }

    public void clrBit7() {
        value = (byte) (value & (~BIT7));
    }
}

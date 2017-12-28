/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: Type conversions between data int turn [] [] Turn int [] go to print string
 * Author:  Holy.Han
 * Date:  2017/12/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.library.Utils;

/**
 * <p>function: </p>
 * <p>description:  Type conversions between data
 *                  int turn byte[]
 *                  byte[] Turn int
 *                  byte[] go to print string</p>
 * history:  1. 2017/12/14
 * Author: Holy.Han
 * modification:
 */
public class ConvertData {
    protected static final String TAG = "ConvertData";

    public static final int BIN = 2;
    public static final int OCT = 8;
    public static final int DEC = 10;
    public static final int HEX = 16;
    public static final int HEX_0X = 17;

    /**
     * Converts an int value to a byte array of four bytes,
     * which is applied to the order of the (High and low).
     * @param from
     * @return
     */
    public static byte[] intToBytes(int from){
        byte[] to = new byte[4];
        to[0] = (byte) ((from>>24) & 0xff);
        to[1] = (byte) ((from>>16)& 0xff);
        to[2] = (byte) ((from>>8)&0xff);
        to[3] = (byte) (from & 0xff);
        return to;
    }

    /**
     * 字节数组转换为字符串，主要用于字节数组的显示
     * @param bytes 输入字节数组
     * @param form  OCT:八进制 DEC:无符号十进制 HEX:十六进制 HEX_0X:带有0x的十六进制
     * @return
     */
    public static String bytesToString(byte[] bytes, int form) {
        if(bytes == null){
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        for (byte tmp : bytes) {
            if (form == OCT) {
                stringBuffer.append(String.format("%03o ", tmp));
            } else if (form == DEC) {
                stringBuffer.append(String.format("%03d ", tmp & 0xff));
            } else if (form == HEX) {
                stringBuffer.append(String.format("%02x ", tmp));
            } else if (form == HEX_0X) {
                stringBuffer.append(String.format("0x%02x,", tmp));
            }
        }
        if(stringBuffer.length()==0) {
            stringBuffer.append("not connected main board!");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    /**
     * 字节数组转换为字符串，主要用于字节数组的显示
     * @param bytes 输入字节数组
     * @param form OCT:八进制 DEC:无符号十进制 HEX:十六进制 HEX_0X:带有0x的十六进制
     * @param lenth 输入字节数组长度
     * @return
     */
    public static String bytesToString(byte[] bytes, int form, int lenth) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int tmp = 0; tmp < lenth; tmp++) {
            if (form == OCT) {
                stringBuffer.append(String.format("%03o ", bytes[tmp]));
            } else if (form == DEC) {
                stringBuffer.append(String.format("%03d ", bytes[tmp] & 0xff));
            } else if (form == HEX) {
                stringBuffer.append(String.format("%02x ", bytes[tmp]));
            } else if (form == HEX_0X) {
                stringBuffer.append(String.format("0x%02x,", bytes[tmp]));
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }
    /**
     * 字节数组转换为字符串，主要用于字节数组的显示
     * @param bytes 输入字节数组
     * @param form OCT:八进制 DEC:无符号十进制 HEX:十六进制 HEX_0X:带有0x的十六进制
     * @param type 格式间隔
     * @return
     */
    public static String bytesToString(byte[] bytes, int form, String type) {
        if(bytes == null){
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();
        for (byte tmp : bytes) {
            if (form == OCT) {
                stringBuffer.append(String.format("%03o%s", tmp,type));
            } else if (form == DEC) {
                stringBuffer.append(String.format("%03d%s", tmp & 0xff,type));
            } else if (form == HEX) {
                stringBuffer.append(String.format("%02x%s", tmp,type));
            } else if (form == HEX_0X) {
                stringBuffer.append(String.format("0x%02x,", tmp));
            }
        }
        if(stringBuffer.length()==0){
            return "";
        }
        if(type !="") {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

}

package com.haiersmart.sfcontrol.utilslib;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class PrintUtil {
    public static final int BIN = 2;
    public static final int OCT = 8;
    public static final int DEC = 10;
    public static final int HEX = 16;
    public static final int HEX_0X = 17;

    /**
     * 字节数组转换为字符串，主要用于字节数组的显示
     * @param bytes 输入字节数组
     * @param form  OCT:八进制 DEC:无符号十进制 HEX:十六进制 HEX_0X:带有0x的十六进制
     * @return
     */
    public static String BytesToString(byte[] bytes, int form) {
        StringBuffer stringBuffer = new StringBuffer();
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
    public static String BytesToString(byte[] bytes, int form, int lenth) {
        StringBuffer stringBuffer = new StringBuffer();
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
     * 字节数组转换为无符号整数，大端排序，最大长度4字节
     *
     * @param bytes 输入字节数组
     * @return long -1:长度超过4
     */
    public static long BytesToUnInt(byte[] bytes) {
        long result = 0;
        if (bytes.length > 4) {
            return -1;
        }
        if (bytes.length == 1) {
            result = bytes[0] & 0xff;
        } else if (bytes.length == 2) {
            result = (bytes[1] & 0xff) | ((bytes[0] & 0xff) << 8);
        } else if (bytes.length == 3) {
            result = (bytes[2] & 0xff) | ((bytes[1] & 0xff) << 8) | ((bytes[0] & 0xff) << 16);
        } else if (bytes.length == 4) {
            result = (bytes[3] & 0xff) | ((bytes[2] & 0xff) << 8) | ((bytes[1] & 0xff) << 16);
            result += ((long) (bytes[0] & 0xff) << 24);
        }
        return result;
    }

    /**
     * 字节数组转换为整数，大端排序，最大长度4字节
     *
     * @param bytes 输入字节数组
     * @return
     */
    public static int BytesToInt(byte[] bytes) {
        int result = 0;
        if (bytes.length == 1) {
            result = (bytes[0] & 0xff);
        } else if (bytes.length == 2) {
            result = ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
        } else if (bytes.length == 3) {
            result = ((bytes[0] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[2] & 0xff);
        } else if (bytes.length >= 4) {
            result = ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
        }
        return result;
    }

    /**
     * 字节数组转换为无符号整数，大端排序，最大长度2字节
     *
     * @param bytes 输入字节数组
     * @return long -1:长度超过2
     */
    public static int BytesToUnShort(byte[] bytes) {
        int result = 0;
        if (bytes.length > 2) {
            return -1;
        }
        if (bytes.length == 1) {
            result = bytes[0] & 0xff;
        } else if (bytes.length == 2) {
            result = (bytes[1] & 0xff) | ((bytes[0] & 0xff) << 8);
        }
        return result;
    }

    /**
     * 字节数组转换为整数，大端排序，最大长度2字节
     *
     * @param bytes 输入字节数组
     * @return
     */
    public static short BytesToShort(byte[] bytes) {
        short result = 0;
        if (bytes.length == 1) {
            result = (short) (bytes[0] & 0xff);
        } else if (bytes.length == 2) {
            result = (short) (((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
        }
        return result;
    }

    public static String ByteToString(byte b,int from){
        String string = new String();
        if(from == OCT){
            string = String.format("%03o", b);
        }else if(from == DEC){
            string = String.format("%03d", b);
        }        else if(from == HEX){
            string = String.format("%02x", b);
        }        else if(from == HEX_0X){
            string = String.format("0x%02x", b);
        }
        return string;
    }
    public static String ShortToString(short b,int from){
        String string = new String();
        if(from == OCT){
            string = String.format("%06o", b);
        }else if(from == DEC){
            string = String.format("%05d", b);
        }        else if(from == HEX){
            string = String.format("%04x", b);
        }        else if(from == HEX_0X){
            string = String.format("0x%04x", b);
        }
        return string;
    }
}

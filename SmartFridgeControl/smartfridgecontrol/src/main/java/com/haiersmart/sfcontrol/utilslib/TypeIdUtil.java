/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.utilslib;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.service.MainBoardParameters;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/14
 * Author: Holy.Han
 * modification:
 */
public class TypeIdUtil {
    protected final String TAG = "TypeIdUtil";
    public static String getCode(){
        MainBoardParameters mainBoardParameters = MainBoardParameters.getInstance();
        String id = mainBoardParameters.getTypeId();
        String mac = DeviceUtil.getMac(ControlApplication.mContext).replace(":", "");
        String changshang = "0";//	厂商：0代表北京，1代表优悦
        String code = getCRCCode(id+mac,changshang);
        return id+mac+changshang+code;
    }

    //获取CRC code
    private static String getCRCCode(String str, String changshang) {
        if (str.length() == 76) {
            //二维码中各字节的和（校验码除外）
            int sum = 0;
            //把字符串的字节转化为整型并相加
            for (int i = 0; i < 38; i++) {
                String numString = str.substring(i * 2, i * 2 + 2);
                try {
                    int num = Integer.parseInt(numString, 16);
                    sum = sum + num;
                } catch (NumberFormatException e) {
                }
            }
            //再加上厂商的代表值
            int vendorNum = Integer.parseInt(changshang, 16);
            sum = sum + vendorNum;
            //计算出校验码的值
            int checkNum = 0x100 - sum % 256;
            //转16进制
            if (checkNum <= 0xf) {
                return "0" + Integer.toHexString(checkNum);//小于16补0
            } else {
                return Integer.toHexString(checkNum);
            }
        } else {
            return "";
        }
    }
}

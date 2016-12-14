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

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/14
 * Author: Holy.Han
 * modification:
 */
public class StringUtil {
    protected final String TAG = "StringUtil";

    public static boolean isNumber(String str) {
        Boolean isNumber = Boolean.valueOf(str.matches("-?[0-9]+.*[0-9]*"));
        return isNumber.booleanValue();
    }
}

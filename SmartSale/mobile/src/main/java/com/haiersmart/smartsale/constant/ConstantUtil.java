/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2017/12/22
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.smartsale.constant;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/12/22
 * Author: Holy.Han
 * modification:
 */
public class ConstantUtil {
    protected final String TAG = "ConstantUtil";

    //
    public static final String URL = "http://192.168.100.232/smartsale/";
    public static final String URL_GETLOCK = "getlock.php";
    public static final String URL_UNLOCK = "unlock.php";
    public static final String URL_PUTDOOR = "putdoor.php";
    public static final String URL_PUTRFID = "putrfid.php";

    public static final String URL_TEST_SERVER = "http://192.168.100.232/smartsale/putrfid.php";

    public static final String HTTP_BROADCAST = "com.haiersmart.smartsale.httpservice";
    public static final String DOOR_STATE_BROADCAST = "com.haiersmart.smartsale.httpservice";


    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "smartsale.db";
    public static final String DB_TABLE_NAME_CONTROL = "smartsaletable";
    public static final String HTTP_KEY_MSG = "msg";
    public static final String HTTP_KEY_MAC = "mac";
    public static final String HTTP_KEY_USERID = "userid";
    public static final String HTTP_KEY_STATUS = "status";
    public static final String HTTP_KEY_OK = "ok";
    public static final String HTTP_KEY_ERR = "err";
}

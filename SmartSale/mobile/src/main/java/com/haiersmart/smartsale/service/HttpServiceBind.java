/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2017/12/27
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.smartsale.service;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/12/27
 * Author: Holy.Han
 * modification:
 */
public interface HttpServiceBind {
    void setOnUnlockListener(HttpService.UnlockListener listener);
    void removeOnUnlockListener(HttpService.UnlockListener listener);
}

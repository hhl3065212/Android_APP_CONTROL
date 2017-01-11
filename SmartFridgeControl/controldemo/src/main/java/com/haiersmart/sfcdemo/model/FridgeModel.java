/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 冰箱接口模型
 * Author:  Holy.Han
 * Date:  2016/12/5
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcdemo.model;

/**
 * <p>function: </p>
 * <p>description:  冰箱接口模型</p>
 * history:  1. 2016/12/5
 * Author: Holy.Han
 * modification:
 */
public class FridgeModel {
    protected final String TAG = "FridgeModel";
    public String mFridgeModel,mTypeId;
    public int mFridgeMin,mFridgeMax,mFreezeMin,mFreezeMax,mChangeMin,mChangeMax;
    public int mFridgeTarget,mFreezeTarget,mChangeTarget;
    public int mFridgeShow,mFreezeShow,mChangeShow;
    public boolean isSmart,isHoliday,isQuickCold,isQuickFreeze,isFridgeOpen,isTidbit;
    public String mDisableFridge,mDisableFreeze,mDisableChange,mDisableSmart,mDisableHoliday,mDisableQuickCold,
            mDisableQuickFreeze,mDisableFridgeOpen,mDisableTidbit;

}

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 商场演示用
 * Author:  Holy.Han
 * Date:  2017/1/13
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.draw;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.haiersmart.sfcontrol.R;

/**
 * <p>function: </p>
 * <p>description:  商场演示用</p>
 * history:  1. 2017/1/13
 * Author: Holy.Han
 * modification:
 */
public class MyMarketButton extends Button{
    protected final String TAG = "MyMarketButton";
    private boolean isPress;

    public MyMarketButton(Context context) {
        super(context);
        setOff();
    }

    public MyMarketButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOff();
    }

    public MyMarketButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOff();
    }

    public void setOn(){
        isPress = true;
        setBackground(getResources().getDrawable(R.drawable.checkbox_checked));
    }
    public void setOff(){
        isPress = false;
        setBackground(getResources().getDrawable(R.drawable.checkbox_unchecked));
    }

    public boolean isPress() {
        return isPress;
    }
}

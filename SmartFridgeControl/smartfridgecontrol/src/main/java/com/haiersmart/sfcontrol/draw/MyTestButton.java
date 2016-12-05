/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/2
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.draw;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/2
 * Author: Holy.Han
 * modification:
 */
public class MyTestButton extends Button{
    protected final String TAG = "MyTestButton";
    private boolean isPress;

    public MyTestButton(Context context) {
        super(context);
    }

    public MyTestButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTestButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOn(){
        isPress = true;
        setBackgroundColor(Color.rgb(0x88,0xfe,0xfe));
    }
    public void setOff(){
        isPress = false;
        setBackgroundColor(Color.rgb(0xdd,0xdd,0xdd));
    }

    public boolean isPress() {
        return isPress;
    }
}

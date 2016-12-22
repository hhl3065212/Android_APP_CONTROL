/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/2
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcdemo.draw;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.haiersmart.sfcdemo.R;

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
        setOff();
    }

    public MyTestButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOff();
    }

    public MyTestButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOff();
    }

    public void setOn(){
        setBackgroundResource(R.drawable.btn_myfamily_sure);
        isPress = true;
    }
    public void setOff(){
        setBackgroundResource(R.drawable.btn_myfamily_false);
        isPress = false;
    }

    public boolean isPress() {
        return isPress;
    }
}

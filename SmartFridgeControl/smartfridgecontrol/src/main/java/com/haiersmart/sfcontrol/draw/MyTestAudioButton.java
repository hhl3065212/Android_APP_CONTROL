/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.draw;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.haiersmart.sfcontrol.R;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/15
 * Author: Holy.Han
 * modification:
 */
public class MyTestAudioButton extends LinearLayout{
    protected final String TAG = "MyTestAudioButton";
    private boolean isPress = false;

    public MyTestAudioButton(Context context) {
        super(context);
        setOff();
    }

    public MyTestAudioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOff();
    }

    public MyTestAudioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOff();
    }
    public void setOn(){
        isPress = true;
        setBackground(getResources().getDrawable(R.drawable.btn_factory_on));
    }
    public void setOff(){
        isPress = false;
        setBackground(getResources().getDrawable(R.drawable.btn_factory_off));
    }

    public boolean isPress() {
        return isPress;
    }
}

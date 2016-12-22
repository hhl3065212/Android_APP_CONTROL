/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/21
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcdemo.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haiersmart.sfcdemo.R;

import java.util.ArrayList;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/21
 * Author: Holy.Han
 * modification:
 */
public class AlarmWindow extends PopupWindow implements View.OnClickListener{
    protected final String TAG = "AlarmWindow";
    private Activity context;
    private View mMenuView;
    private TextView tvShowText;
    private ArrayList<String> listShow;

    public AlarmWindow(Activity context){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.window_alarm, null);
        tvShowText = (TextView) mMenuView.findViewById(R.id.text_alarm_content);
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);
        mMenuView.setOnClickListener(this);

    }
    public void addShow(String show){
        if(listShow == null){
            listShow = new ArrayList<String>();
        }
        listShow.add(show);
        String tmpShow ="";
        for(int i=0;i<listShow.size();i++){
            if(i==0){
                tmpShow = listShow.get(i);
            }else {
                tmpShow += "\n"+listShow.get(i);
            }
        }
        tvShowText.setText(tmpShow);
    }
    public void deleteShow(String show){
        if(listShow != null) {
            for (int i = 0; i < listShow.size(); i++) {
                if (listShow.get(i).equals(show)) {
                    listShow.remove(i);
                    break;
                }
            }
            if (listShow.size() != 0) {
                String tmpShow = "";
                for (int i = 0; i < listShow.size(); i++) {
                    if (i == 0) {
                        tmpShow = listShow.get(i);
                    } else {
                        tmpShow += "\n" + listShow.get(i);
                    }
                }
                tvShowText.setText(tmpShow);
            } else {
                this.dismiss();
            }
        }
    }
    public void showDialog() {
        // 显示窗口
        if(context==null){
            return;
        }
        showAtLocation(context.getWindow().getDecorView(),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL
                , 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    @Override
    public void onClick(View view) {
        listShow.clear();
        listShow = null;
        this.dismiss();
    }
    public boolean isNoAlarm(){
        boolean res = false;
        if(listShow==null){
            res = true;
        }else if(listShow.isEmpty()){
            res = true;
        }
        return res;
    }
}

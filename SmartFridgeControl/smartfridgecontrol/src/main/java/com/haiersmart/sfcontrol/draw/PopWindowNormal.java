package com.haiersmart.sfcontrol.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haiersmart.sfcontrol.R;


public class PopWindowNormal extends PopupWindow {
    private Button mBtSure/*,mBtCancel,button_yes*/;
    private TextView contentText,titleText;
    private ImageView pop_img_xx ;

//    public final static int OK_CANCEL = 1;
//    public final static int OK_ONLY = 2;
    private View mMenuView;
    private Activity context;
    private PopInputListener popListener;
    private String default_content,default_title;


    public void setPopListener(PopInputListener popListener) {
        this.popListener = popListener;
    }

    @SuppressWarnings("deprecation")
    public PopWindowNormal(Activity activity, String title, String content) {
        super(activity);
        this.context = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_normal, null);

        default_content = context.getResources().getString(R.string.pop_default_content);
        default_title = context.getResources().getString(R.string.pop_default_title);

        if(title!=null){
            default_title = title;
        }
        if(content!=null){
            default_content = content;
        }


        mBtSure = (Button) mMenuView.findViewById(R.id.button_ok);
        contentText = (TextView) mMenuView.findViewById(R.id.pop_update_content);
        titleText = (TextView) mMenuView.findViewById(R.id.pop_title_pni);
        pop_img_xx = (ImageView) mMenuView.findViewById(R.id.pop_img_xx);

        contentText.setText(default_content);
        titleText.setText(default_title);
        pop_img_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputMethod();
                dismiss();
            }
        });

        // 确定按钮
        mBtSure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if(popListener!=null){
                String input = null;
                if(input == null){
                    input = "";
                }
                popListener.onOkClick(input);
            }
            }
        });


        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                hideInputMethod();
            }
        });
//        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, 0);

    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
//        ToastUtil.showToastLong("yincang");
        hideInputMethod();
        super.setOnDismissListener(onDismissListener);
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

    /**
     * 关闭软键盘bufen
     *
     */
    public void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mMenuView, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(mMenuView.getWindowToken(), 0); //强制隐藏键盘
//        imm.hideSoftInputFromWindow(mEdittext.getWindowToken(), 0); //强制隐藏键盘
//        ToastUtil.showToastLong("yincang");

    }
    public void setContentText(String content){
        contentText.setText(content);
    }

}

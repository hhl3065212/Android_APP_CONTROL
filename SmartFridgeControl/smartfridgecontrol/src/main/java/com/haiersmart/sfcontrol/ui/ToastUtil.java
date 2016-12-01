package com.haiersmart.sfcontrol.ui;

import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Toast;

import com.haiersmart.sfcontrol.application.ControlApplication;


/**
 * Toast管理
 */
public class ToastUtil {

    public static Toast toast;
    private static CountDownTimer cdt;

    public static void showToastShort(String toastText) {
        if (toast == null) {
            toast = Toast.makeText(ControlApplication.getInstance(), "", Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(toastText + "");
        toast.show();
    }

    public static void showToastLong(String toastText) {
        if (toast == null) {
            toast = Toast.makeText(ControlApplication.getInstance(), "", Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(toastText + "");
        toast.show();
    }

    public static void showToastCenter(String toastText) {
        if (toast == null) {
            toast = Toast.makeText(ControlApplication.getInstance(), "", Toast.LENGTH_LONG);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(toastText + "");
        toast.show();
    }

}
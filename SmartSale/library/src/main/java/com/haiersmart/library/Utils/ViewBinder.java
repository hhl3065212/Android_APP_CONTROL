package com.haiersmart.library.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/11/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class ViewBinder {


    public static void bind(Object target, View source) {
        Field[] fields = target.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Bind bind = field.getAnnotation(Bind.class);

                if (bind != null) {
                    int viewId = bind.id();
                    boolean clickLis = bind.click();
                    try {
                        field.setAccessible(true);
                        if (field.get(target) != null) {
                            continue;
                        }
                        if (clickLis) {
                            source.findViewById(viewId).setOnClickListener(
                                    (View.OnClickListener) target);
                        }
                        field.set(target, source.findViewById(viewId));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void bind(Activity activity) {
        bind(activity, activity.getWindow().getDecorView());
    }

    public static void bind(View view) {
        Context context = view.getContext();
        if (context instanceof Activity) {
            bind((Activity) context);
        } else {
            Log.d("AnnotateUtil.java", "the view don\'t have root view");
        }
    }

    @TargetApi(11)
    public static void bind(Fragment fragment) {
        bind(fragment, fragment.getActivity().getWindow().getDecorView());
    }


}

package com.holy.demo.constant;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.Fragment;
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

    public static void bind(Activity activity) {
        bind(activity, activity.getWindow().getDecorView());
    }

    public static void bind(Object target, View source) {
        Field[] fields = target.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (field.get(target) != null) {
                        continue;
                    }

                    Bind bind = field.getAnnotation(Bind.class);
                    if (bind != null) {
                        int viewId = bind.value();
                        field.set(target, source.findViewById(viewId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @TargetApi(11)
    public static void bind(Fragment fragment) {
        bind(fragment, fragment.getActivity().getWindow().getDecorView());
    }


}

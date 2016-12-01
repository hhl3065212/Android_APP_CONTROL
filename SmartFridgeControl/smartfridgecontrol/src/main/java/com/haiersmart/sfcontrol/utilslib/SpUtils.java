package com.haiersmart.sfcontrol.utilslib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by tingting on 2016/11/4.
 */

public class SpUtils {

    public static final String NATION = "sfcontrol_sp";
    private SharedPreferences mSp;
    private static SpUtils instance;

    public SpUtils(Context ctx) {
        this(ctx, "nation_sp");
    }

    public SpUtils(Context ctx, String file) {
        this.mSp = ctx.getSharedPreferences(file, 0);
    }

    public static SpUtils getInstance(Context ctx) {
        if(instance == null) {
            instance = new SpUtils(ctx);
        }

        return instance;
    }

    public Object get(String key, Object defValue) {
        return defValue instanceof Integer?Integer.valueOf(this.mSp.getInt(key, ((Integer)defValue).intValue())):(defValue instanceof Boolean?Boolean.valueOf(this.mSp.getBoolean(key, ((Boolean)defValue).booleanValue())):(defValue instanceof Float?Float.valueOf(this.mSp.getFloat(key, ((Float)defValue).floatValue())):(defValue instanceof Long?Long.valueOf(this.mSp.getLong(key, ((Long)defValue).longValue())):(defValue instanceof Set ?this.mSp.getStringSet(key, (Set)defValue):this.mSp.getString(key, (String)defValue)))));
    }

    public void put(String key, Object value) {
        SharedPreferences.Editor editor = this.mSp.edit();
        this.putWithoutApply(editor, key, value);
        this.apply(editor);
    }

    public void put(Object obj) {
        SharedPreferences.Editor editor = this.mSp.edit();

        for(Class klass = obj.getClass(); !Object.class.equals(klass); klass = klass.getSuperclass()) {
            Field[] fields = klass.getDeclaredFields();
            Field[] var5 = fields;
            int var6 = fields.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Field item = var5[var7];

                try {
                    this.putWithoutApply(editor, item.getName(), item.get(obj));
                } catch (IllegalAccessException var10) {
                    var10.printStackTrace();
                }
            }
        }

        this.apply(editor);
    }

    public void putWithoutApply(SharedPreferences.Editor editor, String key, Object value) {
        if(value instanceof Integer) {
            editor.putInt(key, ((Integer)value).intValue());
        } else if(value instanceof Boolean) {
            editor.putBoolean(key, ((Boolean)value).booleanValue());
        } else if(value instanceof Float) {
            editor.putFloat(key, ((Float)value).floatValue());
        } else if(value instanceof Long) {
            editor.putLong(key, ((Long)value).longValue());
        } else if(value instanceof Set) {
            editor.putStringSet(key, (Set)value);
        } else {
            editor.putString(key, (String)value);
        }

    }

    public void clear() {
        this.apply(this.mSp.edit().clear());
    }

    public void remove(String key) {
        this.apply(this.mSp.edit().remove(key));
    }

    public void apply(SharedPreferences.Editor editor) {
        if(Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }

    }

    public void saveCollectionAlbums(String str) {
        this.put("CollectionAlbums", TextUtils.isEmpty(str)?"[]":str);
    }

    public String getCollectionAlbums() {
        return (String)this.get("CollectionAlbums", "[]");
    }
}


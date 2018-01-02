package com.haiersmart.smartsale.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haiersmart.smartsale.application.SaleApplication;
import com.haiersmart.smartsale.constant.ConstantUtil;

/**
 * Created by tingting on 2017/12/28.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper mInstance;
    private static final String TAG = "DBHelper";
    public static DBHelper getInstance() {
        if (mInstance == null) {
            synchronized (DBHelper.class) {
                if (mInstance == null) {
                    mInstance = new DBHelper();
                }
            }
        }
        return mInstance;
    }

    private DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }
    private DBHelper(Context context, String name) {
        super(context, name, null, ConstantUtil.DB_VERSION);
    }
    private DBHelper(Context context) {
        //MyLogUtil.i(TAG,"getInstance");
        super(context, ConstantUtil.DB_NAME, null, ConstantUtil.DB_VERSION);
    }
    private DBHelper(Context context, int version) {
        super(context, ConstantUtil.DB_NAME, null, version);
    }

    private DBHelper() {
        //MyLogUtil.i(TAG,"getInstance");
        super(SaleApplication.mContext, ConstantUtil.DB_NAME, null, ConstantUtil.DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RFIDDbMgr.SQL_CREATE_CONTROL_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

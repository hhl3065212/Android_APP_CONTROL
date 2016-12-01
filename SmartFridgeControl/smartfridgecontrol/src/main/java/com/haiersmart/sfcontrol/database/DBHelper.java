package com.haiersmart.sfcontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;


/**
 * Created by hanho on 2016/8/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper mInstance;
    private static final String TAG = "DBHelper";

    public static DBHelper getInstance() {

        if (mInstance == null) {
            synchronized (DBHelper.class) {
                if (mInstance == null) {
//                    if(ControlApplication.mContext == null ) {
//                        Log.i(TAG,"ControlApplication.mContext is null");
//                    }
                    mInstance = new DBHelper();
                }
            }
        }
        return mInstance;
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }
    public DBHelper(Context context, String name) {
        super(context, name, null, ConstantUtil.DB_VERSION);
    }
    public DBHelper(Context context) {
        //MyLogUtil.i(TAG,"getInstance");
        super(context, ConstantUtil.DB_NAME, null, ConstantUtil.DB_VERSION);
    }
    public DBHelper(Context context, int version) {
        super(context, ConstantUtil.DB_NAME, null, version);
    }

    public DBHelper() {
        //MyLogUtil.i(TAG,"getInstance");
        super(ControlApplication.getInstance().mContext, ConstantUtil.DB_NAME, null, ConstantUtil.DB_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"onCreate");
        db.execSQL(FridgeControlDbMgr.SQL_CREATE_CONTROL_ENTRIES);
        db.execSQL(FridgeInfoDbMgr.SQL_CREATE_INFO_ENTRIES);
        db.execSQL(FridgeStatusDbMgr.SQL_CREATE_STATUS_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table "+ConstantUtil.DB_TABLE_NAME_CONTROL+" if exists");
        onCreate(db);
    }
}

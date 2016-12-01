package com.haiersmart.sfcontrol.database;



import android.content.Context;
import android.util.Log;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.util.ArrayList;

/**
 * Created by hanho on 2016/8/15.
 */
public class DBOperation {
    public static DBOperation mInstance;
    private DBHelper mDbHelper;

    public static DBOperation getInstance() {
        if (mInstance == null) {
            synchronized (DBOperation.class) {
                if (mInstance == null) {
                    mInstance = new DBOperation();
                }
            }
        }
        return mInstance;
    }

    public DBOperation() {
        createDBData();
    }

    //public FridgeDataBaseRam mFridgeDataBaseRam = FridgeDataBaseRam.getInstance();
    public class DBBaseStatus{
        public String ParameterName;//参数名称
        public int Direction;//信息方向
        public int Status;//参数状态 可以 打开 关闭 具体数值

        public DBBaseStatus(String parameterName, int status, int direction) {
            ParameterName = parameterName;
            Status = status;
            Direction = direction;
        }
    }
    public ArrayList<DBBaseStatus> mDBStatus = new ArrayList<DBBaseStatus>();

    public void createDBData() {

       // Log.i("DBHelper","getInstance");
        mDbHelper = DBHelper.getInstance();
        FridgeControlDbMgr.getInstance();
        FridgeControlDbMgr.getInstance().init();
        FridgeInfoDbMgr.getInstance();
        FridgeInfoDbMgr.getInstance().init();
        FridgeStatusDbMgr.getInstance();
        FridgeStatusDbMgr.getInstance().init();
    }

    public DBHelper getDdHelper() {
        return mDbHelper;
    }

    public FridgeControlDbMgr getControlDbMgr() {
        return FridgeControlDbMgr.getInstance();
    }

    public FridgeInfoDbMgr getInfoDbMgr() {
        return FridgeInfoDbMgr.getInstance();
    }

    public FridgeStatusDbMgr getmStatusDbMgr() {
        return FridgeStatusDbMgr.getInstance();
    }

}

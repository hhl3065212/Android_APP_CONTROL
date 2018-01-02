package com.haiersmart.smartsale.database;



/**
 * Created by tingting on 2017/12/28.
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


    private DBOperation() {
        createDBData();
    }

    private void createDBData() {
        mDbHelper = DBHelper.getInstance();
        RFIDDbMgr.getInstance().init();
    }

    public RFIDDbMgr getRFIDDbMgr() {
        return RFIDDbMgr.getInstance();
    }

}

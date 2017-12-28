package com.haiersmart.smartsale.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tingting on 2017/12/28.
 */

public class RFIDDbMgr {
    private static final String TABLE_NAME = "rfiddb";
    private static final String TEXT_TYPE = " VARCHAR";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_CONTROL_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id " + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "mac " + TEXT_TYPE + COMMA_SEP +
                    "userid " + TEXT_TYPE + COMMA_SEP +
                    "counts " + "INTEGER" + COMMA_SEP +
                    "epcid " + TEXT_TYPE +
                    " )";

    private static SQLiteDatabase sdb;
    private static RFIDDbMgr mInstance;
    private static Object mDBLock = Object.class;
    private static final String TAG = "RFIDDBMGR";

    public static RFIDDbMgr getInstance() {
        sdb = DBHelper.getInstance().getWritableDatabase();
        if (mInstance == null) {
            synchronized (mDBLock) {
                if (mInstance == null) {
                    mInstance = new RFIDDbMgr();
                }
            }
        }
        return mInstance;
    }

    private RFIDDbMgr() {
    }

    public void init() {

    }

    private void add(List<RFIDInfoEntry> entries) {

    }

    public long insert(RFIDInfoEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return -1;
            ContentValues values = new ContentValues();
            //values.put("id", entry.id);
            values.put("mac", entry.epcid);
            values.put("userid", entry.userid);
            values.put("counts", entry.counts);
            values.put("epc", entry.epcid);
            long newRowId = sdb.insert(
                    TABLE_NAME,
                    null,
                    values);
            return newRowId;
        }
    }

    public long insert(JSONObject jsonObject) {
        synchronized (mDBLock) {
            if (jsonObject == null) return -1;
            ContentValues values = new ContentValues();
            try {
                values.put("mac", jsonObject.getString("mac"));
                values.put("userid",jsonObject.getString("userid"));
                values.put("counts", jsonObject.getInt("counts"));
                values.put("epc", jsonObject.getString("epc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            long newRowId = sdb.insert(
                    TABLE_NAME,
                    null,
                    values);
            return newRowId;
        }
    }


    public static String getJsonString(JSONObject json, String keyName) {
        try {
            if (json==null) {
                return "";
            }
            String s = json.getString(keyName);
            return (s == null || s.equals("") || s.equals("null")) ? "" : s;
        } catch (JSONException e) {
          //  Log.e(e.getMessage());
            return "";
        }
    }
}

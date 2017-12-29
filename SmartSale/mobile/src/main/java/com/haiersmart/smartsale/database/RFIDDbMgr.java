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
            values.put("epcid", entry.epcid);
            long newRowId = sdb.insert(
                    TABLE_NAME,
                    null,
                    values);
            return newRowId;
        }
    }

    public void deleteEntry(RFIDInfoEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            sdb.delete(TABLE_NAME, "userid = ?", new String[]{entry.userid});
        }
    }

    public void queryByUserId(RFIDInfoEntry entry) {
        synchronized (mDBLock) {
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                //FridgeControlEntry tmpEntry = new FridgeControlEntry();
                if(entry.userid.equals(c.getString(c.getColumnIndex("userid")))) {
                    entry.id = c.getInt(c.getColumnIndex("id"));
                    entry.mac = c.getString(c.getColumnIndex("mac"));
                    entry.counts = c.getInt(c.getColumnIndex("counts"));
                    entry.epcid = c.getString(c.getColumnIndex("epcid"));
                    break;
                }
            }
            c.close();
        }
    }

    public Cursor queryTheCursor() {
        Cursor c = sdb.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return c;
    }


    public long insert(JSONObject jsonObject) {
        synchronized (mDBLock) {
            if (jsonObject == null) return -1;
            ContentValues values = new ContentValues();
            try {
                values.put("mac", jsonObject.getString("mac"));
                values.put("userid",jsonObject.getString("userid"));

                JSONObject rfidJo = jsonObject.getJSONObject("rfid");
                values.put("counts", rfidJo.getInt("counts"));
                values.put("epcid", rfidJo.getString("epcid"));
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



    public void deleteEntry(JSONObject jsonObject) {
        synchronized (mDBLock) {
            if (jsonObject == null) return;
            try {
                sdb.delete(TABLE_NAME, "userid = ?", new String[]{jsonObject.getString("userid")});
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void replaceEntry(JSONObject jsonObject) {
        synchronized (mDBLock) {
            if (jsonObject == null) return;
            sdb.delete(TABLE_NAME, null, null);
            insert(jsonObject);
        }
    }

}

package com.haiersmart.sfcontrol.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tingting on 2016/9/28.
 */
public class FridgeInfoDbMgr {
    private static final String TAG = "FridgeInfoDbMgr";
    private static final String TABLE_NAME = "infodb";
    private static final String TEXT_TYPE = " VARCHAR";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_INFO_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id " + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name " + TEXT_TYPE + COMMA_SEP +
                    "value " + TEXT_TYPE +
            " )";

    private static SQLiteDatabase sdb;
    private static FridgeInfoDbMgr mInstance;
    private static Object mDBLock = Object.class;

    public static FridgeInfoDbMgr getInstance() {
        //sdb = DBOperation.getInstance().getDdHelper().getWritableDatabase();
        sdb = DBHelper.getInstance().getWritableDatabase();
        if (mInstance == null) {
            synchronized (mDBLock) {
                if (mInstance == null) {
                    mInstance = new FridgeInfoDbMgr();
                }
            }
        }

        return mInstance;
    }

    private FridgeInfoDbMgr() {

    }

    public void init() {
        ArrayList<FridgeInfoEntry> entries = new ArrayList<FridgeInfoEntry>();
        entries.add(new FridgeInfoEntry("fridgeId",""));
        entries.add(new FridgeInfoEntry("fridgeVersion",""));
        entries.add(new FridgeInfoEntry("fridgeFactory",""));
        entries.add(new FridgeInfoEntry("fridgeSn",""));
        List<FridgeInfoEntry>  checkList = query();
        int dbSize=checkList.size();
        int entriesSize = entries.size();
        Log.d(TAG,"dbSize = "+dbSize+",entriesSize = "+entriesSize);
        if(dbSize < 1){
            for (FridgeInfoEntry entry : checkList) {
                deleteEntry(entry);
            }
        }
        if (dbSize < entriesSize) {
            for(int i=checkList.size();i<entries.size();i++){
                Log.d(TAG,"insert name:"+entries.get(i).name);
                insert(entries.get(i));
            }
        }
    }

    private void add(List<FridgeInfoEntry> entries) {
        synchronized (mDBLock) {
            sdb.beginTransaction();
            try {
                for(FridgeInfoEntry entry: entries) {
                    sdb.execSQL("INSERT INTO " + TABLE_NAME  + " VALUES(null, ?, ?)", new Object[]{entry.name, entry.value});
                }
            } finally {
                sdb.setTransactionSuccessful();
                sdb.endTransaction();
            }
        }
    }

    public long insert(FridgeInfoEntry entry) {
        synchronized (mDBLock) {
            if(entry == null) return -1;
            ContentValues values = new ContentValues();
            //values.put("id", entry.id);
            values.put("name", entry.name);
            values.put("value", entry.value);
            long newRowId = sdb.insert(
                    TABLE_NAME,
                    null,
                    values);
            return  newRowId;
        }

    }

    public void updateValue(FridgeInfoEntry entry) {
        synchronized (mDBLock) {
            if(entry == null) return;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }

    }


    public void updateEntry(FridgeInfoEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateAll(List<FridgeInfoEntry> entries) {
        synchronized (mDBLock) {
            for(FridgeInfoEntry entry: entries) {
                ContentValues values = new ContentValues();
                values.put("value", entry.value);
                sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
            }
        }
    }

    public void deleteEntry(FridgeInfoEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            sdb.delete(TABLE_NAME, "name = ?", new String[]{entry.name});
        }
    }

    public List<FridgeInfoEntry> query() {
        synchronized (mDBLock) {
            ArrayList<FridgeInfoEntry> entries = new ArrayList<FridgeInfoEntry>();
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                FridgeInfoEntry entry = new FridgeInfoEntry();
                entry.id = c.getInt(c.getColumnIndex("id"));
                entry.name = c.getString(c.getColumnIndex("name"));
                entry.value = c.getString(c.getColumnIndex("value"));
                entries.add(entry);
            }
            c.close();
            return entries;
        }

    }

    public void queryByName(FridgeInfoEntry entry) {
        synchronized (mDBLock) {
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                if(entry.name.equals(c.getString(c.getColumnIndex("name"))) ) {
                    entry.id = c.getInt(c.getColumnIndex("id"));
                    entry.value = c.getString(c.getColumnIndex("value"));
                    break;
                }
            }
            c.close();
        }
    }

    /**
     * query all entrys, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = sdb.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return c;
    }

}

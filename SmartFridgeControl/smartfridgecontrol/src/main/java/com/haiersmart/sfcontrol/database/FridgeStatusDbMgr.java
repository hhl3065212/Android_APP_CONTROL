package com.haiersmart.sfcontrol.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tingting on 2016/9/28.
 */
public class FridgeStatusDbMgr {
    private static final String TABLE_NAME = "statusdb";
    private static final String TEXT_TYPE = " VARCHAR";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_STATUS_ENTRIES =
            "CREATE TABLE  IF NOT EXISTS  " + TABLE_NAME + " (" +
                    "id " + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name " + TEXT_TYPE + COMMA_SEP +
                    "value " + "INTEGER" +
            " )";

    private static SQLiteDatabase sdb;
    private static FridgeStatusDbMgr mInstance;
    private static Object mDBLock = Object.class;

    public static FridgeStatusDbMgr getInstance() {
        //sdb = DBOperation.getInstance().getDdHelper().getWritableDatabase();
        sdb = DBHelper.getInstance().getWritableDatabase();
        if (mInstance == null) {
            synchronized (mDBLock) {
                if (mInstance == null) {
                    mInstance = new FridgeStatusDbMgr();
                }
            }
        }
        return mInstance;
    }

    private FridgeStatusDbMgr() {
    }

    public void init() {
        ArrayList<FridgeStatusEntry> checkList = query();
        if (checkList.size() == 19) {
            return;
        }
        for (FridgeStatusEntry entry : checkList) {
            deleteEntry(entry);
        }
        ArrayList<FridgeStatusEntry> entries = new ArrayList<FridgeStatusEntry>();
        entries.add(new FridgeStatusEntry("fridgeShowTemp", 5));//0 冷藏显示温度
        entries.add(new FridgeStatusEntry("freezeShowTemp", -18));//1 冷冻显示温度
        entries.add(new FridgeStatusEntry("changeShowTemp", 0));//2 变温显示温度
        entries.add(new FridgeStatusEntry("refrigeratorRealTemperature", 50));//3
        entries.add(new FridgeStatusEntry("freezerRealTemperature", -180));//4
        entries.add(new FridgeStatusEntry("variableRealTemperature", 0));//5
        entries.add(new FridgeStatusEntry("envShowTemp", 26));//6 环境显示温度
        entries.add(new FridgeStatusEntry("envRealTemperature", 260));//7
        entries.add(new FridgeStatusEntry("envShowHumidity", 50));//8
        entries.add(new FridgeStatusEntry("fridgeDoorStatus", 0));//9 冷藏门
        entries.add(new FridgeStatusEntry("communicationErr", 0));//10
        entries.add(new FridgeStatusEntry("fridgeDoorErr", 0));//11 冷藏门报警
        entries.add(new FridgeStatusEntry("envSensorErr", 0));//12 环境温度传感器故障
        entries.add(new FridgeStatusEntry("fridgeSensorErr", 0));//13 冷藏温度传感器故障
        entries.add(new FridgeStatusEntry("freezeSensorErr", 0));//14 冷冻温度传感器故障
        entries.add(new FridgeStatusEntry("changeSensorErr", 0));//15 变温温度传感器故障
        entries.add(new FridgeStatusEntry("defrostSensorErr", 0));//16 化霜传感器故障
        entries.add(new FridgeStatusEntry("freezeDefrostErr", 0));//17 冷冻化霜故障
        entries.add(new FridgeStatusEntry("defrostingSensorRealTemperature", 0));//18
        add(entries);
    }

    private void add(List<FridgeStatusEntry> entries) {
        synchronized (mDBLock) {
            sdb.beginTransaction();
            try {
                for(FridgeStatusEntry entry: entries) {
                    sdb.execSQL("INSERT INTO " + TABLE_NAME +"  VALUES(null, ?, ?)", new Object[]{entry.name, entry.value});
                }
            } finally {
                sdb.setTransactionSuccessful();
                sdb.endTransaction();
            }
        }
    }

    public long insert(FridgeStatusEntry entry) {
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

    public void updateValue(FridgeStatusEntry entry) {
        synchronized (mDBLock) {
            if(entry == null) return;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }

    }


    public void updateEntry(FridgeStatusEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateAll(ArrayList<FridgeStatusEntry> entries) {
        synchronized (mDBLock) {
            for (FridgeStatusEntry entry : entries) {
                ContentValues values = new ContentValues();
                values.put("value", entry.value);
                sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
            }
        }
    }

    public void deleteEntry(FridgeStatusEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            sdb.delete(TABLE_NAME, "name = ?", new String[]{entry.name});
        }
    }

    public ArrayList<FridgeStatusEntry> query() {
        synchronized (mDBLock) {
            ArrayList<FridgeStatusEntry> entrys = new ArrayList<FridgeStatusEntry>();
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                FridgeStatusEntry entry = new FridgeStatusEntry();
                entry.id = c.getInt(c.getColumnIndex("id"));
                entry.name = c.getString(c.getColumnIndex("name"));
                entry.value = c.getInt(c.getColumnIndex("value"));
                entrys.add(entry);
            }
            c.close();
            return entrys;
        }

    }

    public void queryByName(FridgeStatusEntry entry) {
        synchronized (mDBLock) {
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                if(entry.name.equals(c.getString(c.getColumnIndex("name")))) {
                    entry.id = c.getInt(c.getColumnIndex("id"));
                    entry.value = c.getInt(c.getColumnIndex("value"));
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

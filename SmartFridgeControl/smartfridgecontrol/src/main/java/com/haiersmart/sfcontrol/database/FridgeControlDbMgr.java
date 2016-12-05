package com.haiersmart.sfcontrol.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tingting on 2016/9/28.
 */
public class FridgeControlDbMgr {
    private static final String TABLE_NAME = "controldb";
    private static final String TEXT_TYPE = " VARCHAR";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_CONTROL_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id " + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name " + TEXT_TYPE + COMMA_SEP +
                    "value " + "INTEGER" + COMMA_SEP +
                    "disable " + TEXT_TYPE +
            " )";

    private static SQLiteDatabase sdb;
    private static FridgeControlDbMgr mInstance;
    private static Object mDBLock = Object.class;
    private static final String TAG = "FridgeControlDbMgr";

    public static FridgeControlDbMgr getInstance() {
        sdb = DBHelper.getInstance().getWritableDatabase();
        if (mInstance == null) {
            synchronized (mDBLock) {
                if (mInstance == null) {
                    mInstance = new FridgeControlDbMgr();
                }
            }
        }
        return mInstance;
    }

    private FridgeControlDbMgr() {
    }

    public void init() {
        List<FridgeControlEntry> checkList = query();
//        if (checkList.size() == 17) {
//            return;
//        }
        for (FridgeControlEntry entry : checkList) {
            deleteEntry(entry);
        }
        ArrayList<FridgeControlEntry> entries = new ArrayList<FridgeControlEntry>();
        entries.add(new FridgeControlEntry("smartMode", 1, ConstantUtil.NO_WARNING));//0 智能模式
        entries.add(new FridgeControlEntry("holidayMode", 0, ConstantUtil.NO_WARNING));//1 假日模式
        entries.add(new FridgeControlEntry("purifyMode", 0, ConstantUtil.NO_WARNING));//2 净化模式
        entries.add(new FridgeControlEntry("quickColdMode", 0, ConstantUtil.NO_WARNING));//3 速冷模式
        entries.add(new FridgeControlEntry("quickFreezeMode", 0, ConstantUtil.NO_WARNING));//4 速冻模式
        entries.add(new FridgeControlEntry("tidbitMode", 0, ConstantUtil.NO_WARNING));//5 珍品模式
        entries.add(new FridgeControlEntry("marketDemo", 0, ConstantUtil.NO_WARNING));//6
        entries.add(new FridgeControlEntry("freshLight", 0, ConstantUtil.NO_WARNING));//7
        entries.add(new FridgeControlEntry("strongPurifyMode", 0, ConstantUtil.NO_WARNING));//8 强效净化模式
        entries.add(new FridgeControlEntry("fridgeTargetTemp", 5, ConstantUtil.SMART_ON_SET_TEMPER_WARNING));//9 冷藏目标温度
        entries.add(new FridgeControlEntry("freezeTargetTemp", -18, ConstantUtil.SMART_ON_SET_TEMPER_WARNING));//10 冷冻目标温度
        entries.add(new FridgeControlEntry("changeTargetTemp", 0, ConstantUtil.NO_WARNING));//11 变温目标温度
        entries.add(new FridgeControlEntry("camera", 0, ConstantUtil.NO_WARNING));//12
        entries.add(new FridgeControlEntry("fridgeSwitch", 1, ConstantUtil.SMART_ON_REFRIGERATOR_CLOSE_WARNING));//13 冷藏开关 默认为1
        entries.add(new FridgeControlEntry("variableOffMode", 0, ConstantUtil.NO_WARNING));//14
        entries.add(new FridgeControlEntry("shutDown", 0, ConstantUtil.NO_WARNING));//15
        entries.add(new FridgeControlEntry("TestMode", 0, ConstantUtil.NO_WARNING));//16
        add(entries);
    }

    private void add(List<FridgeControlEntry> entries) {
        synchronized (mDBLock) {
            sdb.beginTransaction();
            try {
                for(FridgeControlEntry entry: entries) {
                    Log.i(TAG, "entry.name=" + entry.name);
                    ContentValues values = new ContentValues();
                    values.put("name",entry.name);
                    values.put("value",entry.value);
                    values.put("disable",entry.disable);
                    long rowid = sdb.insert(TABLE_NAME,null,values);
                    Log.i(TAG,"rowid=" + rowid);
                    //insert into 表名(字段列表) values(值列表)
                    //sdb.execSQL("INSERT INTO controldb(name,value,disable) VALUES(?, ?, ?)", new Object[]{entry.name, entry.value, entry.disable});
                }
            } finally {
                sdb.setTransactionSuccessful();
                sdb.endTransaction();

            }
        }
    }

    public long insert(FridgeControlEntry entry) {
        synchronized (mDBLock) {
            if(entry == null) return -1;
            ContentValues values = new ContentValues();
            //values.put("id", entry.id);
            values.put("name", entry.name);
            values.put("value", entry.value);
            values.put("disable", entry.disable);
            long newRowId = sdb.insert(
                    TABLE_NAME,
                    null,
                    values);
            return  newRowId;
        }

    }

    public void updateValue(FridgeControlEntry entry) {
        synchronized (mDBLock) {
            if(entry == null) return;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateValueByName(EnumBaseName entryName, int entryValue) {
        synchronized (mDBLock) {
            if(entryName == null) return;
            FridgeControlEntry entry = new FridgeControlEntry(entryName.toString());
            entry.value = entryValue;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateDisableByName(EnumBaseName entryName, String entryDisable) {
        synchronized (mDBLock) {
            if (entryName == null) return;
            FridgeControlEntry entry = new FridgeControlEntry(entryName.toString());
            entry.disable = entryDisable;
            ContentValues values = new ContentValues();
            values.put("disable", entry.disable);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateDisable(FridgeControlEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            ContentValues values = new ContentValues();
            values.put("disable", entry.disable);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateEntry(FridgeControlEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            ContentValues values = new ContentValues();
            values.put("value", entry.value);
            values.put("disable", entry.disable);
            sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
        }
    }

    public void updateAll(List<FridgeControlEntry> entries) {
        synchronized (mDBLock) {
            for (FridgeControlEntry entry : entries) {
                ContentValues values = new ContentValues();
                values.put("value", entry.value);
                values.put("disable", entry.disable);
                sdb.update(TABLE_NAME, values, "name = ?", new String[]{entry.name});
            }
        }
    }

    public void deleteEntry(FridgeControlEntry entry) {
        synchronized (mDBLock) {
            if (entry == null) return;
            sdb.delete(TABLE_NAME, "name = ?", new String[]{entry.name});
        }
    }

    public List<FridgeControlEntry> query() {
        synchronized (mDBLock) {
            ArrayList<FridgeControlEntry> entries = new ArrayList<FridgeControlEntry>();
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                FridgeControlEntry entry = new FridgeControlEntry();
                entry.id = c.getInt(c.getColumnIndex("id"));
                entry.name = c.getString(c.getColumnIndex("name"));
                entry.value = c.getInt(c.getColumnIndex("value"));
                entry.disable = c.getString(c.getColumnIndex("disable"));
                entries.add(entry);
            }
            c.close();
            return entries;
        }

    }

    public void queryByName(FridgeControlEntry entry) {
        synchronized (mDBLock) {
            Cursor c = queryTheCursor();
            while (c.moveToNext()) {
                //FridgeControlEntry tmpEntry = new FridgeControlEntry();
                if(entry.name.equals(c.getString(c.getColumnIndex("name")))) {
                    entry.id = c.getInt(c.getColumnIndex("id"));
                    entry.value = c.getInt(c.getColumnIndex("value"));
                    entry.disable = c.getString(c.getColumnIndex("disable"));
                    break;
                }
            }
            c.close();
        }
    }

    /**
     * query all entries, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = sdb.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return c;
    }

}

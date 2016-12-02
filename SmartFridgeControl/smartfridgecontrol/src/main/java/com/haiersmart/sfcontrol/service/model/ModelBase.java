package com.haiersmart.sfcontrol.service.model;

import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeControlDbMgr;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.database.FridgeStatusDbMgr;
import com.haiersmart.sfcontrol.service.ControlMainBoardInfo;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;

import java.util.List;

/**
 * Created by tingting on 2016/11/2.
 */

public abstract class ModelBase {

    protected static List<FridgeControlEntry> mControlEntries;
    protected ControlMainBoardService mService;
    protected Boolean mIsUIInitDone = false;

    ModelBase(ControlMainBoardService service) {
        mService = service;
    }

    public List<FridgeControlEntry> getControlEntries() {
        return mControlEntries;
    }

    public FridgeControlDbMgr getControlDbMgr() {
        return mService.getControlDbMgr();
    }

    public ControlMainBoardInfo getMainBoardInfo() {
        return mService.getMainBoardInfo();
    }

    public FridgeStatusDbMgr getStatusDbMgr() {
        return mService.getStatusDbMgr();
    }

    public void setUIInitDone(Boolean isDone) {
        mIsUIInitDone = isDone;
    }

    public int getControlValueByName(EnumBaseName entryName) {
        int res = -1;
        String nameString = entryName.toString();
        for (FridgeControlEntry tempEntry: mControlEntries ){
            if(tempEntry.name.equals(nameString)) {
                res = tempEntry.value;
                break;
            }
        }
        return res;
    }

    public FridgeControlEntry getControlEntryByName(EnumBaseName entryName) {
        String nameString = entryName.toString();
        int listSize = mControlEntries.size();
        for (int i=0; i<listSize;i++ ){
            if(mControlEntries.get(i).name.equals(nameString)) {
                return mControlEntries.get(i);
            }
        }
        FridgeControlEntry entry = new FridgeControlEntry(nameString);
        entry.value = -1;
        return entry;
    }

    public void setControlValueByName(EnumBaseName entryName, int entryValue) {
        int listSize = mControlEntries.size();
        String nameString = entryName.toString();
        for (int i=0; i<listSize;i++ ){
            if(mControlEntries.get(i).name.equals(nameString)) {
                mControlEntries.get(i).value = entryValue;
                break;
            }
        }
    }

    public void setControlDisableByName(EnumBaseName entryName, String entryDisable) {
        int listSize = mControlEntries.size();
        String nameString = entryName.toString();
        for (int i=0; i<listSize;i++ ){
            if(mControlEntries.get(i).name.equals(nameString)) {
                mControlEntries.get(i).disable = entryDisable;
                break;
            }
        }
    }

    public void updateControlByEntry(FridgeControlEntry entry) {
        int listSize = mControlEntries.size();
        String nameString = entry.name;
        for (int i=0; i<listSize;i++ ){
            if(mControlEntries.get(i).name.equals(nameString)) {
                mControlEntries.get(i).value = entry.value;
                mControlEntries.get(i).disable = entry.disable;
                break;
            }
        }
    }

    public abstract void init();
    public abstract void smartOn();
    public abstract void smartOff();
    public abstract void freezeOn();
    public abstract void freezeOff();
    public abstract void setCold(int coldTemper);
    public abstract void setFreeze(int freezeTemper);
    public abstract void handleStatusDataResponse();

    public void holidayOn() {
    }

    public void holidayOff() {
    }

    public void coldOn() {
    }

    public void coldOff() {
    }

    public void refrigeratorOpen(){
    }

    public void refrigeratorClose(){
    }

    public void setCustomArea(int customTemper) {
    }
}

package com.haiersmart.sfcontrol.service.model;

import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeControlDbMgr;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.database.FridgeStatusEntry;
import com.haiersmart.sfcontrol.service.ControlMainBoardInfo;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tingting on 2016/11/2.
 */

public abstract class ModelBase {

    static List<FridgeControlEntry> mControlEntries;
    ArrayList<FridgeStatusEntry> mShowTempEntryList;
    ArrayList<FridgeStatusEntry> mErrorEntryList;

    ControlMainBoardService mService;

    ModelBase(ControlMainBoardService service) {
        mService = service;
    }

    public List<FridgeControlEntry> getControlEntries() {
        return mControlEntries;
    }

    public ArrayList<FridgeStatusEntry> getTempEntries() {
        return mShowTempEntryList;
    }

    public ArrayList<FridgeStatusEntry> getErrorEntries() {
        return mErrorEntryList;
    }

    FridgeControlDbMgr getControlDbMgr() {
        return mService.getControlDbMgr();
    }

    ControlMainBoardInfo getMainBoardInfo() {
        return mService.getMainBoardInfo();
    }

    int getControlValueByName(EnumBaseName entryName) {
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

    void setControlValueByName(EnumBaseName entryName, int entryValue) {
        int listSize = mControlEntries.size();
        String nameString = entryName.toString();
        for (int i=0; i<listSize;i++ ){
            if(mControlEntries.get(i).name.equals(nameString)) {
                mControlEntries.get(i).value = entryValue;
                break;
            }
        }
    }

    void setControlDisableByName(EnumBaseName entryName, String entryDisable) {
        int listSize = mControlEntries.size();
        String nameString = entryName.toString();
        for (int i=0; i<listSize;i++ ){
            if(mControlEntries.get(i).name.equals(nameString)) {
                mControlEntries.get(i).disable = entryDisable;
                break;
            }
        }
    }

    void updateControlByEntry(FridgeControlEntry entry) {
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

    /**
     * 公用方法，子类需实现
     */
    public abstract void init();
    public abstract void smartOn();
    public abstract void smartOff();
    public abstract void freezeOn();
    public abstract void freezeOff();
    public abstract void coldOn();
    public abstract void coldOff();
    public abstract void setCold(int coldTemper);
    public abstract void setFreeze(int freezeTemper);
    public abstract void handleStatusDataResponse();

    /**
     * 非公用方法，子类可重写
     */
    public void holidayOn() {}

    public void holidayOff() {}

    public void refrigeratorOpen(){}

    public void refrigeratorClose(){}

    public void setCustomArea(int customTemper) {}

    public void tidbitOn(){}

    public void tidbitOff(){}

    public void purifyOn(){}

    public void purifyOff(){}

    public void SterilizeModeOn(int step){}

    public void SterilizeModeOff(){}

    public void SterilizeSwitchOn(){}
    public void SterilizeSwitchOff(){}
}

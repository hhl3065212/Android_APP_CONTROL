package com.haiersmart.sfcontrol.service.model;

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

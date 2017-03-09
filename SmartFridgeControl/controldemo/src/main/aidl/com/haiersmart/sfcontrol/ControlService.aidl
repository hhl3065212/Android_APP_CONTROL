// ControlService.aidl
package com.haiersmart.sfcontrol;
import com.haiersmart.sfcontrol.ICallback;
// Declare any non-default types here with import statements

interface ControlService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//                 double aDouble, String aString);
    boolean getIsReady();
    String getHardId();
    String getHardType();
    int getTempFridgeMin();
    int getTempFreezeMin();
    int getTempChangeMin();
    int getTempFridgeMax();
    int getTempFreezeMax();
    int getTempChangeMax();
    String getTempRange();
    boolean getSterilizeRunStatus();
    int getSterilizeRemanTime();
    String getHardStatusCoder();
    int getFridgeShowTemp();
    int getFreezeShowTemp();
    int getChangeShowTemp();
    String getShowTemp();
    String getModeInfo();

    String setFridgeTemp(int temp);
    String setFreezeTemp(int temp);
    String setChangeTemp(int temp);
    String setSmartMode(boolean b);
    String setHolidayMode(boolean b);
    String setQuickFreezeMode(boolean b);
    String setQuickColdMode(boolean b);
    String setFridgeSwitch(boolean b);
    String setSterilizeMode(int step);
    String setTidbitMode(boolean b);
    String setPurifyMode(boolean b);
    void setFridgeLight(boolean b);
    void setHandleLight(boolean b);

    int getFridgeTargetTemp();
    int getFreezeTargetTemp();
    int getChangeTargetTemp();
    boolean getSmartMode();
    boolean getHolidayMode();
    boolean getQuickFreezeMode();
    boolean getQuickColdMode();
    boolean getFridgeSwitch();
    int getSterilizeMode();
    boolean getSterilizeSwitch();
    boolean getTidbitMode();
    boolean getPurifyMode();

    void registerCallback(ICallback cb);
    void unregisterCallback(ICallback cb);
}

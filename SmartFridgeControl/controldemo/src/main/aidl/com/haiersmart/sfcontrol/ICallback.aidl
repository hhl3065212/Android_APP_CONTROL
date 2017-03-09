// ICallback.aidl
package com.haiersmart.sfcontrol;

// Declare any non-default types here with import statements

interface ICallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void notifyShowTemp(String showTemp);
    void notifyErrorInfo(String errorInfo);
    void notifyDoorStatus(String doorStatus);
    void notifyDooralarm(String doorAlarm);
    void notifySterilizeRun(String run);
    void notifyMode(String mode);
}

// IHaierService.aidl
package com.smart.haier.haierservice;

// Declare any non-default types here with import statements

interface IHaierService {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
        int update(String path);
        boolean installApk(String path);
        boolean installandStartAPK(String path, String packageName, String activityName);
        int changeTimeFormat(int fmt);
        boolean setDate(int year,int month,int day);
        boolean setTime(int hour,int minute);
        boolean setTimeZone(String timeZone);
        String getSystemVersion();
        String getSystemCustom();
        String getSystemModel();
        String getFridgeModel();
        String getFridgeSn();
        boolean setDateTimeAuto(boolean isEnable);
        boolean deleteInstallandStartApk(String delPackage, String apkPath, String packageName, String activityName);
        boolean deleteInstallappAndRebootsystem(String delPackage, String apkPath);
        boolean deleteInstallBackupappAndRebootsystem(String delPackage, String apkPath, String storageDir, String groupName, String usrName);
}

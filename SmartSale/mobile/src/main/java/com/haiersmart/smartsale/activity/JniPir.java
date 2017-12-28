package com.haiersmart.smartsale.activity;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by tingting on 2017/12/15.
 */

public class JniPir {
    static {
        // The runtime will add "lib" on the front and ".o" on the end of
        // the name supplied to loadLibrary.
        System.loadLibrary("native-pir-jni");
    }

    public native int probe(int gipoNum, int lable);
    public native int openGpioDev();
    public native int closeGpioDev();
    public native int getGpio(int gpioNum);
    public native int releaseGpio(int gpioNum);
    public native int setGpioState(int gpioNum,int state);
}

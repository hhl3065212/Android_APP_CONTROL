package com.haiersmart.smartsale;

/**
 * Created by tingting on 2017/12/15.
 */

public class JniPir {
    static {
        // The runtime will add "lib" on the front and ".o" on the end of
        // the name supplied to loadLibrary.
        System.loadLibrary("native-pir-jni");
    }

    public native int add(int a, int b);
    public native int openGpioDev();
    public native int closeGpioDev();
    public native int getGpio(int num);
    public native int releaseGpio(int num);
    public native int setGpioState(int num,int state);
}

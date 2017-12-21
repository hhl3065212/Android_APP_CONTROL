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

    private FileDescriptor mFd;
    public FileInputStream mFileInputStream;
    public FileOutputStream mFileOutputStream;
    public FileReader mFileReader;


    public JniPir() {
        mFd = openGpioDev();
        Log.i("PIR","mfd="+mFd);
        if (mFd != null){
//            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
//            mFileReader = new FileReader(mFd);
        }
    }

    public int getData(byte[] b){
        mFileInputStream = new FileInputStream(mFd);
        int len = 0;
        try {
            len = mFileInputStream.read(b);
            mFileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return len;
    }

    public native int add(int a, int b);
    public native FileDescriptor openGpioDev();
    public native int closeGpioDev();
    public native int getGpio(int num);
    public native int releaseGpio(int num);
    public native int setGpioState(int num,int state);
}

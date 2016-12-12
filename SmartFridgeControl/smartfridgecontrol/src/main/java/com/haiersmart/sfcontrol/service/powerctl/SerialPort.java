/**
 * Copyright  2015,  Smart  Haier
 * All  rights  reserved.
 * Description:
 * Author:  shilin
 * Date:  15-12-3
 * FileName:  shilin.java
 * History:
 * 1.  Date:15-12-3 上午11:51
 * Author:shilin
 * Modification:
 */

package com.haiersmart.sfcontrol.service.powerctl;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.smart.haier.haierservice.IHaierService;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private boolean isReady = false;
    private int mSerialPortNum = 0;
    private final String[] mSerialPortDevice = {"", "/dev/ttyMFD1", "/dev/ttyMT1", "/dev/ttyS0"};
    private final int mBaudRate = 4800;
    private String strModel;

    private IHaierService mIService;
    private ServiceConnection connHaierService = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = IHaierService.Stub.asInterface(service);
            try {
                openSerialPort();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public SerialPort(){
        Intent serviceIntent = new Intent();
        ComponentName componentName = new ComponentName(
                "com.smart.haier.haierservice",
                "com.smart.haier.haierservice.MyService");
        serviceIntent.setComponent(componentName);
        ControlApplication.getInstance().mContext.bindService(serviceIntent, connHaierService, Context.BIND_AUTO_CREATE);
    }


    private void openSerialPort() throws IOException, RemoteException {
        strModel = mIService.getSystemModel();
//        strModel = "XD";
        if (strModel.indexOf("XD") >= 0) {
            mSerialPortNum = 3;
            mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
        }  else if (strModel.indexOf("595") >=0) {
            mSerialPortNum = 1;
            mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
        } else if (strModel.indexOf("UG") >=0) {
            mSerialPortNum = 2;
            mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
        }  else {
            mSerialPortNum = 1;
            mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
            if (mFd == null) {
                mSerialPortNum = 2;
                mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
                if (mFd == null) {
                    mSerialPortNum = 3;
                    mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
                }
            }
        }
        MyLogUtil.i(TAG,"Serial Port has open,mFd = "+mFd);

        if (mFd == null) {
            isReady = false;
            mSerialPortNum = 0;
//            throw new IOException();
        }else {
            isReady = true;
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
        }
    }

    // Getters and setters

    public boolean isReady() {
        return isReady;
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }


    // JNI
    private native static FileDescriptor SerialOpen(String path, int baudrate);

    public native void SerialClose();

    static {
        System.loadLibrary("native-powerctl-jni");
    }

    public FileDescriptor getmFd() {
        return mFd;
    }

    public int getSerialPortNum() {
        return mSerialPortNum;
    }

    public void SerialPortClose() {
        if(mFd != null) {
            SerialClose();
        }
    }

    public void SerialPortReOpen() {
        mFd = SerialOpen(mSerialPortDevice[mSerialPortNum], mBaudRate);
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }
}

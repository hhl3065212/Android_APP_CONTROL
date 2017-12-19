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

package com.haiersmart.library.SerialPort;


import android.util.Log;

import com.haiersmart.library.Utils.ConvertData;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SerialPort {

    private final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private static boolean isReady = false;
    private String mSerialPortName = null;
    private int mBaudRate = 115200;
    private Timer mTimer;
    private TimerTask mTimerTask;

    /**
     * @param name 串口设备名称，例如：dev/ttyS1 dev/ttyUSB0
     * @param baud 串口波特率，4800 9600 115200
     */
    public SerialPort(String name, int baud) {
        if (name == null || name.equals("")) {
            Log.e(TAG, "Serial port open failed,name is none");
            isReady = false;
        } else {
            mSerialPortName = name;
            mBaudRate = baud;
            mFd = SerialOpen(mSerialPortName, mBaudRate);

            if (mFd == null) {
                isReady = false;
                Log.e(TAG, "Serial port open failed,Fd is none," + mSerialPortName);
            } else {
                Log.i(TAG, "Serial port open success,Fd is " + mFd + "," + mSerialPortName);
                mFileInputStream = new FileInputStream(mFd);
                mFileOutputStream = new FileOutputStream(mFd);
                isReady = true;
            }
        }
    }


    // Getters and setters

    public FileDescriptor getFd() {
        return mFd;
    }

    public boolean isReady() {
        return isReady;
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public String getSerialPortName() {
        return mSerialPortName;
    }

    public int getBaudRate() {
        return mBaudRate;
    }

    // JNI
    private native static FileDescriptor SerialOpen(String path, int baudrate);

    private native void SerialClose();

    static {
        System.loadLibrary("native-serialport-jni");
    }


    // function

    public void SerialPortClose() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
            }
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mFd != null) {
            SerialClose();
            mFd = null;
        }
        isReady = false;
    }

    public void SerialPortReOpen() {
        SerialPortClose();

        mFd = SerialOpen(mSerialPortName, mBaudRate);

        if (mFd == null) {
            isReady = false;
            Log.e(TAG, "Serial port reopen failed,Fd is none," + mSerialPortName);
        } else {
            Log.i(TAG, "Serial port reopen success,Fd is " + mFd + "," + mSerialPortName);
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
            isReady = true;
        }
    }

    public void write(byte[] data) {
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String data) {
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(int data) {
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.write(ConvertData.intToBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int read(byte[] data, int timeout) {
        int len = -1;
        int revLenHis = 0;
        if (data.length>1024){
            return len;
        }

        long stopTime = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < stopTime){
            int revLen = 0;
            try {
                revLen = mFileInputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(revLen == revLenHis){
                continue;
            }else if (revLen > 1024){
                revLenHis = 1024;
                Log.e(TAG,"recived out buff");
                break;
            } else {
                revLenHis = revLen;
                stopTime = System.currentTimeMillis() + timeout;
            }
        }

        if (mFileInputStream != null) {
            try {
                len = mFileInputStream.read(data,0,revLenHis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(len <= 0){
            len = -1;
        }

        return len;
    }

    public int read(byte[] data){
        return read(data,0);
    }


    public int read(String data,int timeout){
        int len = -1;
        byte[] rev = new byte[1024];
        len = read(rev,timeout);
        if(len != -1)        {
            data = new String(rev,0,len);
        }
        return len;
    }

    public int read(String data){
        return read(data,0);
    }

}

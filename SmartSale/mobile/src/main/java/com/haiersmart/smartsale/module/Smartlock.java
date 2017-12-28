package com.haiersmart.smartsale.module;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Gui Yan on 2017/12/22.
 */

public class Smartlock {
    private static final String TAG = "Smartlock";
    static final int O_ACCMODE = 0003;
    static final int O_RDONLY = 00;
    static final int O_WRONLY = 01;
    static final int O_RDWR = 02;
    static final int O_CREAT = 0100;  /* not fcntl */
    static final int O_EXCL = 0200;   /* not fcntl */
    static final int O_NOCTTY = 0400; /* not fcntl */
    static final int O_TRUNC = 01000; /* not fcntl */
    static final int O_APPEND = 02000;
    static final int O_NONBLOCK = 04000;
    static final int O_NDELAY = O_NONBLOCK;
    static final int O_SYNC = 010000;
    static final int O_FSYNC = O_SYNC;
    static final int O_ASYNC = 020000;

    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private boolean mIsOpenDevice;

    public FileDescriptor getFileDescriptor() {
        if (mIsOpenDevice)
            return mFd;
        return null;
    }

    public InputStream getInputStream() {
        if (mIsOpenDevice)
            return mFileInputStream;
        return null;
    }

    public OutputStream getOutputStream() {
        if (mIsOpenDevice)
            return mFileOutputStream;
        return null;
    }

    private static Smartlock instance;

    private Smartlock() {
        mIsOpenDevice = false;
    }

    public static synchronized Smartlock getInstance() {
        if (instance == null) {
            instance = new Smartlock();
        }
        return instance;
    }

    public boolean openSmartLock(String devpath) {
        if (mIsOpenDevice)
            return true;
        mFd = nativeOpen(devpath, O_RDWR);
        if (mFd == null)
            return false;
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        mIsOpenDevice = true;
        return true;
    }

    public void closeSmartLock() {
        nativeClose();
        mFileInputStream = null;
        mFileOutputStream = null;
        mIsOpenDevice = false;
    }

    static {
        System.loadLibrary("native-smartlock-jni");
    }

    public native static FileDescriptor nativeOpen(String filename, int flags);

    public native static void nativeClose();
}

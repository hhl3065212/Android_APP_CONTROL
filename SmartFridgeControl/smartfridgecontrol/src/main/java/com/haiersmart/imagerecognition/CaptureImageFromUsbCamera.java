package com.haiersmart.imagerecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.util.Log.d;
import static android.util.Log.e;


/**
 * Created by LRXx on 2016-10-10.
 */

public class CaptureImageFromUsbCamera extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Context mContext;

    // JNI functions
    public native int prepareCamera(int videoid);

    public native int prepareCameraWithBase(int videoid, int camerabase);

    public native void processCamera();

    public native void stopCamera();

    public native void pixeltobmp(Bitmap bitmap);

    public native int getPixel(byte[] mdata);

    public native int isMJPGCamera();

    public static native boolean checkCameraExist(int videoid);

    public native boolean openCamera(int videoid);

    public native boolean initCamera(int videoid, int width, int height);

    public native void uninitCamera(int videoid);

    public native boolean startCapturing(int videoid);

    public native boolean stopCapturing(int videoid);

    public native boolean closeCamera(int videoid);

    public native int readFrame(int videoid, byte[] mdata);

    static {
        System.loadLibrary("ImageProcNew");
    }

    protected Context context;
    private Bitmap bmp = null;
    private byte[] mdata;

    private SurfaceHolder holder;

    private boolean isPrePare = false;
    private boolean mStop = false;

    public static boolean tag = false;

    // /dev/videox (x=cameraId+cameraBase) is used.
    // In some omap devices, system uses /dev/video[0-3],
    // so users must use /dev/video[4-].
    // In such a case, try cameraId=0 and cameraBase=4
    private int cameraId = 0;
    private int cameraBase = 0;

    // The following variables are used to draw camera images.
    private int winWidth = 0;
    private int winHeight = 0;
    private Rect rect;
    private int dw, dh;
    private float rate;

    // This definition also exists in ImageProc.h.
    // Webcam must support the resolution 640x480 with YUYV format.
    static final int IMG_WIDTH = 1280;
    static final int IMG_HEIGHT = 800;

    File mExternalStorage = Environment.getExternalStorageDirectory();
    public String DIR_PROJECT = "/haier/sfnation/";
    public String DIR_IMAGE = DIR_PROJECT + "image/"; // 拍照的图
    private String filePath = mExternalStorage + DIR_IMAGE;
    public String IMAGE_NAME = "ABC.jpg"; //文件名

    public CaptureImageFromUsbCamera(Context context) {
        super(context);
        init();

    }

    public CaptureImageFromUsbCamera(Context context, AttributeSet attri) {
        super(context, attri);
        init();
    }

    /**
     * 类初始化数据
     */
    private void init() {
        setFocusable(true);
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    /**
     * 去拍照逻辑
     */
    public void mTakePic() {
        if (mdata == null) {
            mdata = new byte[IMG_WIDTH * IMG_HEIGHT * 4];
        }
        if (isPrePare()) {
            SystemClock.sleep(100);
            processCamera();
            if (isMJPGCamera() == 1) {
                getPixel(mdata);
                bmp = BitmapFactory.decodeByteArray(mdata, 0, mdata.length);
            } else {
                pixeltobmp(bmp);
            }
            saveImage(bmp);
            stopCaptureImage();
        }
    }

    /**
     * 判断相机是否存在，and 相机是否准备好
     */
    public boolean isPrePare() {
        if (isPrePare) {
            return true;
        } else {
            isPrePare = prepareCameraWithBase(cameraId, cameraBase) != -1 ? true : false;
            return isPrePare;
        }
    }

    public static boolean mCheckCameraExist() {
        return checkCameraExist(0);
    }


    /**
     * 保存图片方法
     *
     * @param bitmap
     */
    private void saveImage(Bitmap bitmap) {
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        File file1 = new File(filePath + IMAGE_NAME);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(
                    new FileOutputStream(file1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
//			bm.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 以下是surfaceView 的专用模块
     */
    @Override
    public void run() {
        while (isPrePare()) {
            tag = true;
            SystemClock.sleep(100);
            if (winWidth == 0) {
                winWidth = this.getWidth();
                winHeight = this.getHeight();

                rect = new Rect(0, 0, winWidth - 1, winHeight - 1);

//                if (winWidth * 3 / 4 <= winHeight) {
//                    dw = 0;
//                    dh = (winHeight - winWidth * 3 / 4) / 2;
//                    rate = ((float) winWidth) / IMG_WIDTH;
//                    rect = new Rect(dw, dh, dw + winWidth - 1, dh + winWidth * 3 / 4 - 1);
//                } else {
//                    dw = (winWidth - winHeight * 4 / 3) / 2;
//                    dh = 0;
//                    rate = ((float) winHeight) / IMG_HEIGHT;
//                    rect = new Rect(dw, dh, dw + winHeight * 4 / 3 - 1, dh + winHeight - 1);
//                }
            }
            processCamera();
            int mdata_lenth = 0;
            if (isMJPGCamera() == 1) {
                mdata_lenth = getPixel(mdata);
            } else {
                pixeltobmp(bmp);
            }
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                if (isMJPGCamera() == 1) {
                    if (mdata_lenth != 0) {
                        Bitmap bmptodraw = BitmapFactory.decodeByteArray(mdata, 0, mdata_lenth);
                        canvas.drawBitmap(bmptodraw, null, rect, null);
                    }
                } else {
                    canvas.drawBitmap(bmp, null, rect, null);
                }
                getHolder().unlockCanvasAndPost(canvas);
            }

            if (mStop || (!mCheckCameraExist()) || (!tag)) {
                mStop = false;
                break;
            }
        }
    }

    private Thread a;

    public void startCaptureImage() {
        if (a != null && a.isAlive()) {
            return;
        }
        if (bmp == null) {
            bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
        }
        if (mdata == null) {
            mdata = new byte[IMG_WIDTH * IMG_HEIGHT * 4];
        }
//        isMJPGCamera();
        a = new Thread(this);
        a.start();
    }

    public void stopCaptureImage() {
        if (a != null && a.isAlive() && isPrePare) {
            mStop = true;
            while (mStop) {
                SystemClock.sleep(100);
            }
        }
        stopCamera();
        tag = false;
        isPrePare = false;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCaptureImage();
    }
}

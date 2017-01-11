/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.utilslib;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haiersmart.sfcontrol.draw.MyTestAudioButton;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/15
 * Author: Holy.Han
 * modification:
 */
public class FactoryAudioUtil {
    protected static final String TAG = "FactoryAudioUtil";
    private final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private final String AUDIO_RECORDER_FILE = "record_temp.amr";
    private final String AUDIO_PATH = Environment.getExternalStorageDirectory() + "/AudioRecorder";
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private final int MAX_RECORD_TIME = 3000;//ms
    private MyTestAudioButton btnRecord, btnPlayAll, btnPlayLeft, btnPlayRight, btnCurrent;
    private TextView tvRecord, tvPlayAll, tvPlayLeft, tvPlayRight, tvCurrent;
    private ProgressBar prbRecord, prbPlayAll, prbPlayLeft, prbPlayRight, prbCurrent;
    private TimerTask ttAudioRecord, ttAudioPlay;
    private Timer tAudioRecord, tAudioPlay;


    public FactoryAudioUtil(MyTestAudioButton btnRecord, MyTestAudioButton btnPlayAll, MyTestAudioButton btnPlayLeft, MyTestAudioButton btnPlayRight, TextView tvRecord, TextView tvPlayAll, TextView tvPlayLeft, TextView tvPlayRight, ProgressBar prbRecord, ProgressBar prbPlayAll, ProgressBar prbPlayLeft, ProgressBar prbPlayRight) {
        this.btnRecord = btnRecord;
        this.btnPlayAll = btnPlayAll;
        this.btnPlayLeft = btnPlayLeft;
        this.btnPlayRight = btnPlayRight;
        this.tvRecord = tvRecord;
        this.tvPlayAll = tvPlayAll;
        this.tvPlayLeft = tvPlayLeft;
        this.tvPlayRight = tvPlayRight;
        this.prbRecord = prbRecord;
        this.prbPlayAll = prbPlayAll;
        this.prbPlayLeft = prbPlayLeft;
        this.prbPlayRight = prbPlayRight;
        init();
    }

    private void init() {
        btnRecord.setOff();
        btnPlayAll.setOff();
        btnPlayLeft.setOff();
        btnPlayRight.setOff();
        tvRecord.setText("录音");
        tvPlayAll.setText("双声道播放");
        tvPlayLeft.setText("1声道播放");
        tvPlayRight.setText("2声道播放");
        prbRecord.setProgress(0);
        prbPlayAll.setProgress(0);
        prbPlayLeft.setProgress(0);
        prbPlayRight.setProgress(0);
    }

    public void startAudioRecord() {
        deleteAudioDir();
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setMaxDuration(MAX_RECORD_TIME);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(getFilename());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            delay(300);
            mRecorder.prepare();
            mRecorder.start();
        } catch (IllegalStateException e) {
            MyLogUtil.d(TAG, "startAudioRecord IllegalStateException " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        startRecordTimes();
        btnRecord.setOn();
        tvRecord.setText("录音中...");
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                switch (what) {
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        stopAudioRecord();
                        break;
                }
            }
        });

    }


    public void stopAudioRecord() {
        stopRecordTimes();
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        tvRecord.setText("录音");
        btnRecord.setOff();
        prbRecord.setProgress(0);
        prbRecord.setSecondaryProgress(0);
    }

    private void startRecordTimes() {
        if (tAudioRecord == null) {
            tAudioRecord = new Timer();
        }
        if (ttAudioRecord == null) {
            prbRecord.setMax(100);
            ttAudioRecord = new TimerTask() {
                int tmp = 0;

                @Override
                public void run() {
                    tmp += 3;
                    int s = tmp;
                    int e = tmp - 30;
                    if (s > 100) {
                        s = 100;
                    }
                    if (e < 0) {
                        e = 0;
                    }
                    if (e > 100) {
                        tmp = 0;
                    }
                    prbRecord.setProgress(s);
                    prbRecord.setSecondaryProgress(e);
                }
            };
        }
        tAudioRecord.schedule(ttAudioRecord, 30, 30);
    }

    private void stopRecordTimes() {
        if (ttAudioRecord != null) {
            ttAudioRecord.cancel();
            ttAudioRecord = null;
        }
        if (tAudioRecord != null) {
            tAudioRecord.cancel();
            tAudioRecord = null;
        }
    }

    private void delay(int ms) {
        try {
            Thread.currentThread();
            sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getFilename() {
        File file = new File(AUDIO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File audioFile = new File(file, AUDIO_RECORDER_FILE);
        try {
            if (!audioFile.exists()) {
                audioFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (AUDIO_PATH + "/" + AUDIO_RECORDER_FILE);
    }

    private boolean isExistsAudioFile() {
        boolean res = false;
        File file = new File(AUDIO_PATH);
        if (file.exists()) {
            File audioFile = new File(file, AUDIO_RECORDER_FILE);
            if (audioFile.exists()) {
                res = true;
            }
        }
        return res;
    }

    public void deleteAudioDir() {
        boolean deleteFlag = true;
        File deletingDir = new File(AUDIO_PATH);
        if (deletingDir == null || !deletingDir.exists() || !deletingDir.isDirectory()) {
            deleteFlag = false;
        }
        if (deleteFlag) {
            for (File file : deletingDir.listFiles()) {
                if (file.isFile()) {
                    deleteFlag = file.delete();
                    MyLogUtil.d(TAG, "deleteAudioDir file.delete() = " + deleteFlag);
                }
            }
            deletingDir.delete();
            MyLogUtil.d(TAG, "deleteAudioDir deletingDir.delete = " + deleteFlag);
        }
    }


    public void startAudioPlay(final boolean left, final boolean right) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        if (isExistsAudioFile()) {
            try {
                mPlayer.setDataSource(getFilename());
                mPlayer.prepare();
                mPlayer.setVolume(left ? 1 : 0, right ? 1 : 0);
                MyLogUtil.i(TAG, "audio lenth is" + mPlayer.getDuration());
                mPlayer.start();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setStartPlayView(left, right);
            startPlayTimes(left, right);

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopAudioPlay(left, right);
                }
            });
        }
    }

    private void setStartPlayView(boolean left, boolean right) {
        if (left && right) {
            btnPlayAll.setOn();
            tvPlayAll.setText("双声道播放中...");
        } else if (left) {
            btnPlayLeft.setOn();
            tvPlayLeft.setText("1声道播放中...");
        } else if (right) {
            btnPlayRight.setOn();
            tvPlayRight.setText("2声道播放中...");
        }
    }

    private void setStopPlayView(boolean left, boolean right) {
        if (left && right) {
            btnPlayAll.setOff();
            tvPlayAll.setText("双声道播放");
            prbPlayAll.setProgress(0);
        } else if (left) {
            btnPlayLeft.setOff();
            tvPlayLeft.setText("1声道播放");
            prbPlayLeft.setProgress(0);
        } else if (right) {
            btnPlayRight.setOff();
            tvPlayRight.setText("2声道播放");
            prbPlayRight.setProgress(0);
        }
    }


    private int getPlayerDuration() {
        int res = -1;
        if (mPlayer != null) {
            res = mPlayer.getDuration();
        }
        return res;
    }

    private int getPlayerCurrentPosition() {
        int res = -1;
        if (mPlayer != null) {
            res = mPlayer.getCurrentPosition();
        }
        return res;
    }

    public void stopAudioPlay(boolean left, boolean right) {
        stopPlayTimes();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        setStopPlayView(left, right);
    }

    private void startPlayTimes(boolean left, boolean right) {
        if (tAudioPlay == null) {
            tAudioPlay = new Timer();
        }
        if (ttAudioPlay == null) {
            int lenth = getPlayerDuration();
            if (left && right) {
                prbCurrent = prbPlayAll;
            } else if (left) {
                prbCurrent = prbPlayLeft;
            } else if (right) {
                prbCurrent = prbPlayRight;
            }
            prbCurrent.setMax(lenth);

            ttAudioPlay = new TimerTask() {
                @Override
                public void run() {
                    int tmp = getPlayerCurrentPosition();
                    prbCurrent.setProgress(tmp);
                }
            };
        }
        tAudioPlay.schedule(ttAudioPlay, 30, 30);
    }

    private void stopPlayTimes() {
        if (ttAudioPlay != null) {
            ttAudioPlay.cancel();
            ttAudioPlay = null;
        }
        if (tAudioPlay != null) {
            tAudioPlay.cancel();
            tAudioPlay = null;
        }
    }
}

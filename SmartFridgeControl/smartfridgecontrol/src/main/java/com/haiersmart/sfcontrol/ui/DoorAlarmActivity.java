package com.haiersmart.sfcontrol.ui;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;


import com.haiersmart.sfcontrol.R;
import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class DoorAlarmActivity extends Activity {

    private static final long delay = 1000 * 60 * 10;
    private Timer a;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private int oldVol;
    private static final String TAG = "DoorAlarmActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_alarm);
        MyLogUtil.i(TAG, "onCreate");

        systemVolumeControl();
        playVoice();
        delayTimer();
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ControlApplication.getInstance().addDoorAlarmActivity(this);
    }

    /**
     * 记录并设置告警音量
     */
    private void systemVolumeControl() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        oldVol = getMediaCurrentVol();
        setMediaVolume((int) (getMediaMaxVol() * 0.8));
    }


    /**
     * 播放告警声音
     */
    private void playVoice() {
        mediaPlayer = MediaPlayer.create(DoorAlarmActivity.this, R.raw.doorwarning);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行定时退出计时器
     */
    private void delayTimer() {
        a = new Timer();
        a.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, delay);
    }

    /**
     * 设置系统声音
     *
     * @param progress
     */
    private void setMediaVolume(int progress) {
        if (audioManager != null)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    /**
     * 获取当前声音值
     *
     * @return
     */
    private int getMediaCurrentVol() {
        if (audioManager != null) {
            return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    /**
     * 获取最大声音值
     *
     * @return
     */
    private int getMediaMaxVol() {
        if (audioManager != null) {
            return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    /**
     * 释放资源
     */
    private void release() {
        if (a != null) a.cancel();
        if (mediaPlayer != null) mediaPlayer.stop();
        if (!(getMediaCurrentVol() == oldVol)) setMediaVolume(oldVol);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLogUtil.i(TAG, "onDestroy");
        if  (wakeLock !=  null  && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null ;
        }
        handler.removeCallbacks(lightUpRun);
        release();
    }

    private long delay_time = 15000;
    Runnable lightUpRun = new Runnable() {
        @Override
        public void run() {
            screenOn();
            handler.sendEmptyMessage(0);
        }
    };
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            handler.postDelayed(lightUpRun,delay_time);
            return false;
        }
    });
    private void screenOn(){
        //TODO 保持亮屏  防止断网
        pm = (PowerManager)DoorAlarmActivity.this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.ON_AFTER_RELEASE , "OtaWakeLock");
        wakeLock.acquire();
    }
}

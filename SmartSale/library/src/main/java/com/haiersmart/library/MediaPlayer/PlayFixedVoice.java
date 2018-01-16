package com.haiersmart.library.MediaPlayer;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/12/18
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class PlayFixedVoice {

    private static final String TAG = "PlayFixedVoice";

    /**
     * 固定语音的参数，对应不同语音
     */
    public static final int WELCOME = 1;
    public static final int UNLOCK = 2;
    public static final int OPEN = 3;
    public static final int CLOSE = 4;
    public static final int PAY = 5;

    /**
     * 不同不放设备
     * PROMPT assets内音频文件
     * YSM3A1T YS-M3A1T模块内音频文件
     */
    public static final int PROMPT = 0;
    public static final int YSM3A1T = 1;

    private static boolean mIsPlayWelcome = false;
    private static boolean mIsPlayUnlock = false;
    private static boolean mIsPlayOpen = false;
    private static boolean mIsPlayClose = false;
    private static boolean mIsPlayPay = false;
    private static final int mDelayWelcome = 15000;
    private static final int mDelayUnlock = 0;
    private static final int mDelayOpen = 0;
    private static final int mDelayClose = 0;
    private static final int mDelayPay = 0;

    private static Timer timer;
    private static TimerTask task;


    /**
     * 基本播放，可指定设备，指定声音播放
     *
     * @param context
     * @param device  设备指定 PROMPT YSM3A1T
     * @param voice   音频文件 OPEN SELECT FINAL PAY
     */
    public static void playVoice(Context context, int device, int voice) {
        if (voice == WELCOME && !mIsPlayWelcome) {
            mIsPlayWelcome = true;
            timerDelay();
            if (device == PROMPT) {
                PlayPromptVoice.playVoice(context, voice);
            } else if (device == YSM3A1T) {
                PlayYSM3A1TVoice.playVoiceOnce(voice);
            }
        } else if (voice != WELCOME) {
            mIsPlayWelcome = true;
            timerDelay();
            if (device == PROMPT) {
                PlayPromptVoice.playVoice(context, voice);
            } else if (device == YSM3A1T) {
                PlayYSM3A1TVoice.playVoiceOnce(voice);
            }
        }
    }

    /**
     * 播放assets内文件
     *
     * @param context
     * @param voice   音频文件 OPEN SELECT FINAL PAY
     */
    public static void playVoice(Context context, int voice) {
        if (voice == WELCOME && !mIsPlayWelcome) {
            mIsPlayWelcome = true;
            timerDelay();
            PlayPromptVoice.playVoice(context, voice);
        } else if (voice != WELCOME) {
            mIsPlayWelcome = true;
            timerDelay();
            PlayPromptVoice.playVoice(context, voice);
        }
    }

    /**
     * 播放assets以外的设备音频
     *
     * @param device 设备指定 YSM3A1T
     * @param voice  音频文件 OPEN SELECT FINAL PAY
     */
    public static void playVoice(int device, int voice) {
        if (voice == WELCOME && !mIsPlayWelcome) {
            mIsPlayWelcome = true;
            timerDelay();
            if (device == YSM3A1T) {
                PlayYSM3A1TVoice.playVoiceOnce(voice);
            }
        } else if (voice != WELCOME) {
            mIsPlayWelcome = true;
            timerDelay();
            if (device == YSM3A1T) {
                PlayYSM3A1TVoice.playVoiceOnce(voice);
            }
        }
    }

    /**
     * 播放默认设备YSM3A1T的音频
     *
     * @param voice 音频文件 OPEN SELECT FINAL PAY
     */
    public static void playVoice(int voice) {
        if (voice == WELCOME && !mIsPlayWelcome) {
            mIsPlayWelcome = true;
            timerDelay();
            PlayYSM3A1TVoice.playVoiceOnce(voice);
        } else if (voice != WELCOME) {
            mIsPlayWelcome = true;
            timerDelay();
            PlayYSM3A1TVoice.playVoiceOnce(voice);
        }
    }

    private static void timerDelay() {
        if(timer != null){
//            Log.i(TAG,"timer cancel");
            timer.cancel();
            timer= null;
        }
        if (task != null){
//            Log.i(TAG,"task cancel");
            task.cancel();
            task = null;
        }
        if (timer == null) {
            timer = new Timer("play voice delay");
//            Log.i(TAG,"timer create");
        }
        if(task == null){
//            Log.i(TAG,"task create");
            task = new TimerTask() {
                @Override
                public void run() {
//                    Log.i(TAG,"task end");
                    mIsPlayWelcome = false;
                    timer.cancel();
                    timer.purge();
                    task.cancel();
                }
            };
        }

        if (timer !=null && task != null) {
            timer.schedule(task, mDelayWelcome);
        }
//        Log.i(TAG,"timer schedule");
    }


}

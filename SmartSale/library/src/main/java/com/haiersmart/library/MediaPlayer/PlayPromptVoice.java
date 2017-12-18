/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2017/12/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.library.MediaPlayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/12/15
 * Author: Holy.Han
 * modification:
 */
public class PlayPromptVoice {
    protected static final String TAG = "PlayPromptVoice";
    /**
     * 语音存储路径
     */
    private static final String VOICE_DIR = "prompt_voice";
    /**
     * 语音名称
     */
    private static final String VOICE_OPEN_DOOR = "open.mp3";
    private static final String VOICE_SELECT = "select.mp3";
    private static final String VOICE_FINAL_ESTIMATE = "final.mp3";
    private static final String VOICE_PAY = "pay.mp3";

    private static final int OPEN = 1;
    private static final int SELECT = 2;
    private static final int FINAL = 3;
    private static final int PAY = 4;

    private static MediaPlayer player = null;

    /**
     *
     * @param context
     * @param voiceName VOICE_OPEN_DOOR VOICE_SELECT VOICE_FINAL_ESTIMATE VOICE_PAY
     * @return
     */
    private static boolean voiceIsExist(Context context, String voiceName) {
        boolean res = false;
        try {
            String[] voiceList = context.getAssets().list(VOICE_DIR);
            for (int i = 0; i < voiceList.length; i++) {
                Log.e(TAG, voiceList[i]);
                if (voiceList[i].equals(voiceName.trim())) {

                    res = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, voiceName + "不存在");
        }
        if (res) {
            Log.i(TAG, voiceName + "存在");
        } else {
            Log.i(TAG, voiceName + "不存在");
        }
        return res;
    }

    /**
     *
     * @param context
     * @param voiceName VOICE_OPEN_DOOR VOICE_SELECT VOICE_FINAL_ESTIMATE VOICE_PAY
     */
    public static void playVoice(Context context, String voiceName) {
        if (voiceIsExist(context, voiceName)) {
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            final int mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前音乐音量
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取最大声音
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0); // 设置为最大声音，可通过SeekBar更改音量大小
            player = new MediaPlayer();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    player.stop();
                    player.release();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,mVolume,0);
                }
            });
            try {
                AssetFileDescriptor FD = context.getAssets().openFd(VOICE_DIR+"/"+voiceName);
                player.setDataSource(FD.getFileDescriptor(), FD.getStartOffset(), FD.getLength());
                player.prepare();
                player.setVolume(1.0f,1.0f);
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
                player.release();
            }
        }
    }

    public static void playVoice(Context context,int voice){
        switch (voice){
            case OPEN:
                playVoice(context,VOICE_OPEN_DOOR);
                break;
            case SELECT:
                playVoice(context,VOICE_SELECT);
                break;
            case FINAL:
                playVoice(context,VOICE_FINAL_ESTIMATE);
                break;
            case PAY:
                playVoice(context,VOICE_PAY);
                break;
        }
    }


}

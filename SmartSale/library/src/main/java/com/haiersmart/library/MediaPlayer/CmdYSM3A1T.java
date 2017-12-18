/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: YS-M3A1T Voice Playback Module command word
 * Author:  Holy.Han
 * Date:  2017/12/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.library.MediaPlayer;

/**
 * <p>function: </p>
 * <p>description:  YS-M3A1T Voice Playback Module command word</p>
 * history:  1. 2017/12/15
 * Author: Holy.Han
 * modification:
 */
public class CmdYSM3A1T {
    protected final String TAG = "CmdYSM3A1T";

    private static final byte header = (byte)0xfd;
    private static final byte ender = (byte)0xdf;
    /* 控制命令 */
        public static final byte play = (byte)0x01;//播放
        public static final byte pause = (byte)0x02;//暂停
        public static final byte next = (byte)0x03;//下一曲
        public static final byte previous = (byte)0x04;//上一曲
        public static final byte vloumUp = (byte)0x05;//音量加
        public static final byte vloumDown = (byte)0x06;//音量减
        public static final byte standby = (byte)0x07;//待机
        public static final byte word = (byte)0x09;//正常工作
        public static final byte fastForward = (byte)0x0a;//快进
        public static final byte fastBackward = (byte)0x0b;//快退
        public static final byte PP = (byte)0x0c;//
        public static final byte stop = (byte)0x0e;//停止

    /* 查询命令 */
    public static final byte state = (byte)0x10;//查询状态
    public static final byte state_stop = (byte)0;//停止状态
    public static final byte state_play = (byte)1;//播放状态
    public static final byte state_paus = (byte)2;//暂停状态
    public static final byte state_ff = (byte)3;//快进状态
    public static final byte state_fr = (byte)4;//快退状态

    public static final byte vloum = (byte)0x11;//查询音量大小 0-30
    public static final byte EQ = (byte)0x12;//查询音质
    public static final byte EQ_no = (byte)0;//无音质
    public static final byte EQ_pop = (byte)1;//流行音质
    public static final byte EQ_rock = (byte)2;//摇滚音质
    public static final byte EQ_jazz = (byte)3;//金属音质
    public static final byte EQ_classic = (byte)4;//经典音质
    public static final byte EQ_bass = (byte)5;//贝斯音质

    public static final byte mode = (byte)0x13;//查询播放模式
    public static final byte mode_all = (byte)0;//播放所有
    public static final byte mode_folder = (byte)1;//播放专辑
    public static final byte mode_one = (byte)2;//单曲循环
    public static final byte mode_random = (byte)3;//随机播放
    public static final byte mode_onestop = (byte)4;//只播一曲

    public static final byte sdFileCounts = (byte)0x15;//查询SD卡总文件数 1-65535
    public static final byte devices = (byte)0x18;//查询播放设备
    public static final byte devices_usb = (byte)0;//USB
    public static final byte devices_sd = (byte)1;//SD
    public static final byte devices_spi = (byte)2;//SPI

    public static final byte currentPlay = (byte)0x19;//查询当前曲目 1-65535
    public static final byte currentPlayTime = (byte)0x1c;//查询当前曲目时间 s
    public static final byte currentTotalTime = (byte)0x1d;//查询当前曲目总时间 s
    public static final byte currentFileCounts = (byte)0x1f;//查询当前文件夹内总曲目数0-65535
    /* 设置系统参数 */
    public static final byte setVloum = (byte)0x31;//设置音量 0-30
    public static final byte setEQ = (byte)0x32;//设置音质 参考EQ_xxx
    public static final byte setMode = (byte)0x33;//设置播放模式 参考mode_xxx
    public static final byte setDir = (byte)0x34;//文件夹切换
    public static final byte dir_previous = (byte)0;//上一个文件夹
    public static final byte dir_next = (byte)1;//下一个文件夹
    /* 文件选择 */
    public static final byte selectVoice = (byte)0x41;//1-max 16位歌曲名
    public static final byte selectDirVoice = (byte)0x42;//高8位文件夹号，低8位歌曲名
    public static final byte insertVoice = (byte)0x43;//1-max 16位歌曲名
    public static final byte insertDirVoice = (byte)0x44;//高8位文件夹号，低8位歌曲名


    public static byte[] packCmd(byte cmd){
        byte[] res = new byte[4];
        res[0] = header;
        res[1] = (byte)0x02;
        res[2] = cmd;
        res[3] = ender;
        return res;
    }

    public static byte[] packCmd(byte cmd,byte data){
        byte[] res = new byte[5];
        res[0] = header;
        res[1] = (byte)0x03;
        res[2] = cmd;
        res[3] = data;
        res[4] = ender;
        return res;
    }
    public static byte[] packCmd(byte cmd,byte data1,byte data2){
        byte[] res = new byte[6];
        res[0] = header;
        res[1] = (byte)0x04;
        res[2] = cmd;
        res[3] = data1;
        res[4] = data2;
        res[5] = ender;
        return res;
    }
}

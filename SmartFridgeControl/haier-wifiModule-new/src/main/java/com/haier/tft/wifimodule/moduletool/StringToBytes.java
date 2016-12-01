package com.haier.tft.wifimodule.moduletool;

import android.util.Log;

import common.StringConv;


/**
 * Created by Administrator on 2015/9/12.
 */
public class StringToBytes {

    public byte[] getBinary(){



        return null;

    }

    public byte[] get_CMD_WIFI_TO_COMMSERVER_4_BYTE(int n){
        byte[] num = new byte[4];

        num[0]=0x00;
        num[1]=0x00;
        num[2]=0x27;
        num[3]=0x23;
        return num;
    }

    public byte[] get_ERRPRNUM_4BYTE(int n){
        byte[] num = new byte[4];

        num[0]=0x00;
        num[1]=0x00;
        num[2]=0x00;
        num[3]=0x00;
        return num;
    }


    public byte[] getVersionMyself8Byte(String version_myself) {


        byte[] myversion = new byte[8];
//        version_myself.;
        myversion=version_myself.getBytes();

        return myversion;
    }


    public byte[] getVersionDevFile8Byte(String version_dev_file) {


        byte[] version = new byte[8];
//        version_myself.;
        version=version_dev_file.getBytes();

        return version;
    }

    public byte[] getVersionProtocol8Byte(String Protocol) {


        byte[] version = new byte[8];
//        version_myself.;
        version=Protocol.getBytes();

        return version;
    }

    public byte[] getHardVersion8Byte(String Protocol) {


        byte[] version = new byte[8];
//        version_myself.;
        version=Protocol.getBytes();

        return version;
    }

    public byte[] getPlatform32Byte(String Protocol) {


        byte[] version = new byte[32];
//        version_myself.;
        version=Protocol.getBytes();

        return version;
    }

    public byte[] get_BaseBoard_SW_VER_8Byte(String Protocol) {


        byte[] version = new byte[8];
//        version_myself.;
        version=Protocol.getBytes();

        return version;
    }


    public byte[] get_HARDWARE_VER_8Byte(String Protocol) {


        byte[] version = new byte[8];
//        version_myself.;
        version=Protocol.getBytes();

        return version;
    }
    public byte[] get_BaseBoard_HW_VER_8Byte(String Protocol) {


        byte[] version = new byte[8];
//        version_myself.;
        version=Protocol.getBytes();

        return version;
    }

    public byte get_BaseBoard_update_feature_1Byte(int i) {


        byte version =0;
//        version_myself.;

        version = (byte)(i&0xFF);

        return version;
    }

    public byte[] getMACTo32Bytes(String mac) {
        byte[] macbyte = new byte[32];
        int j=0;
        int len= mac.getBytes().length;
        Log.i("ceshi", "len= "+len);

        for(int i=0;i<len;i++){
            macbyte[j]= mac.getBytes()[i];
            j++;
        }
     
/*        for(int i =0;i<mac.length();i=i+2){
        	
          int  tempInt  = StringUtils.parseInt(mac.substring(i, i + 2), 16);
          

            macbyte[j]= (byte)tempInt;
            j++;
        }
*/
        return macbyte;
    }

    public byte[] getDeviceTypeTo32Bytes(String dev){
        byte[] devicetype = new byte[32];
        int j =0;
        int tempInt;
        for(int i =0;i<dev.length();i=i+2)
        {

//        Log.i("binary","dev .lengh = "+dev.length()+"  i = "+i);
            tempInt  = StringConv.parseInt(dev.substring(i, i + 2), 16);
            devicetype[j]= (byte) (tempInt&0xFF);
            j=j+1;
        }

        for(int k=0;k<32;k++){

//            Log.i("binary", "devicetype[j] " +j +"  "+ Integer.toHexString(devicetype[k]));
        }

        return devicetype;

    }

    public byte[] getShortTo2Bytes(short n){
        byte[]  mybyte = new byte[2];

        mybyte[1]=(byte) (n&0xFF);
        mybyte[0]=(byte) (n>>8&0xFF);

        return mybyte;
    }


    public byte[] getIntTo4Bytes(int n){
        byte[]  mybyte = new byte[4];

        mybyte[3]=(byte) (n&0xFF);
        mybyte[2]=(byte) (n>>8&0xFF);
        mybyte[1]=(byte) (n>>16&0xFF);
        mybyte[0]=(byte) (n>>24&0xFF);


        return mybyte;
    }

    public byte[] getIntTo2Bytes(int n){
        byte[]  mybyte = new byte[2];

        mybyte[1]=(byte) (n&0xFF);
        mybyte[0]=(byte) (n>>8&0xFF);

        return mybyte;
    }


}

package com.haiersmart.sfcontrol.service.configtable;

import com.haiersmart.sfcontrol.constant.EnumBaseName;

/**
 * Created by Holy.Han on 2016/9/23 15:12
 * email hanholy1210@163.com
 */
public class ProtocolCommand {
    private final String TAG="BaseCodeTable";

    public static byte[] PackCmdFrame(EnumBaseName cmd, byte... value){
        byte[] data;
        switch (cmd){
            case getAllProperty:
                data = new byte[]{(byte)0xaa,(byte)0x55,(byte)0x04,(byte)0x01,(byte)0x4d,(byte)0x01,(byte) 0x00};
                break;
            case smartMode:   //人工智慧 value 1:打开  0:关闭
                data = new byte[]{(byte) 0xAA, (byte) 0x55, (byte) 0x04, (byte) 0x01, (byte) 0x4d, (byte) 0x04, (byte) 0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x04);
                }else {
                    data[5] = (byte)(0x05);
                }
                break;
            case holidayMode:   //进假日 value 1:打开  0:关闭
                data = new byte[]{(byte) 0xAA, (byte) 0x55, (byte) 0x04, (byte) 0x01, (byte) 0x4d, (byte) 0x06, (byte) 0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x06);
                }else {
                    data[5] = (byte)(0x07);
                }
                break;
            case quickFreezeMode:  //进入速冻
                data = new byte[]{(byte) 0xAA, (byte) 0x55, (byte) 0x04, (byte) 0x01, (byte) 0x4d, (byte) 0x08, (byte) 0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x08);
                }else {
                    data[5] = (byte)(0x09);
                }
                break;
            case quickColdMode:    //进入速冷
                data = new byte[]{(byte) 0xAA, (byte) 0x55, (byte) 0x04, (byte) 0x01, (byte) 0x4d, (byte) 0x0a, (byte) 0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x0a);
                }else {
                    data[5] = (byte)(0x0b);
                }
                break;
            case fridgeTargetTemp:  //冷藏温度设置
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x06,(byte)0x01,(byte)0x5d,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x00};
                data[7] = value[0];
                break;
            case freezeTargetTemp: //冷冻温度设置
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x06,(byte)0x01,(byte)0x5d,(byte)0x03,(byte)0x00,(byte)0x02,(byte)0x00};
                data[7] = value[0];
                break;
            case changeTargetTemp: //变温温度设置
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x06,(byte)0x01,(byte)0x5d,(byte)0x04,(byte)0x00,(byte)0x02,(byte)0x00};
                data[7] = value[0];
                break;
            case tidbitMode://珍品 value 1:打开  0:关闭
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x04,(byte)0x01,(byte)0x4d,(byte)0x46,(byte)0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x46);
                }else {
                    data[5] = (byte)(0x47);
                }
                break;
            case getDeviceId://
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x02,(byte)0x70,(byte)0x00};
                break;
            case purifyMode://
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x04,(byte)0x01,(byte)0x4d,(byte)0x0c,(byte)0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x0c);
                }else {
                    data[5] = (byte)(0x0d);
                }
                break;
            case SterilizeMode://SterilizeMode
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x04,(byte)0x01,(byte)0x4d,(byte)0x30,(byte)0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x30);
                }else {
                    data[5] = (byte)(0x31);
                }
                break;
            case marketDemo://marketDemo
                data = new byte[]{(byte)0xAA,(byte)0x55,(byte)0x04,(byte)0x01,(byte)0x4d,(byte)0x29,(byte)0x00};
                if(value[0] == 1){
                    data[5] = (byte)(0x29);
                }else {
                    data[5] = (byte)(0x2a);
                }
                break;
            case getDebug:
                data = new byte[]{(byte)0xaa,(byte)0x55,(byte)0x04,(byte)0xff,(byte)0x8d,(byte)0x01,(byte) 0x00};
                break;
            case testMode:
                data = new byte[]{(byte)0xaa,(byte)0x55,(byte)0x04,(byte)0x8a,(byte)0x5d,(byte)0x01,(byte) 0x00,(byte) 0x00};
                data[6] = value[0];
                break;
            default:
                data = new byte[]{(byte)0xaa,(byte)0x55,(byte)0x04,(byte)0x01,(byte)0x4d,(byte)0x01,(byte) 0x00};
                break;
        }

        data[data.length-1] = checkSum(data,data.length-1);
        return data;
    }
    private static byte checkSum(byte[] data, int len)
    {
        byte mSum = 0;
        for(int i = 2; i < len; i++)
        {
            mSum += data[i];
        }
        return mSum;
    }
}

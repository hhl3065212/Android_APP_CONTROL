package com.haier.tft.wifimodule.DataForEplus;

import android.util.Log;

import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;

/**
 * 将从ＰＣＢ收到的数据转化为Ｅ＋＋网络数据
 *
 * Created by Administrator on 2015/10/8.
 */
public class DataToBytesForChangeToEPlusFor225 extends DataToByteForChangeToEPlusForCommon{

    public DataToBytesForChangeToEPlusFor225(){
        super();
    }

        /*
            + 冷藏室显示温度(1byte)
            + 冷冻室显示温度(1byte)
            + 变温室显示温度(1byte)
            + 环境显示温度(1byte)
            + 冷藏档位设置(1byte)
            + 冷冻档位设置(1byte)
            + 变温档位设置(1byte)
            + 预留（1byte）
            + wordA(1word)
            + wordB(1word)
            + wordC(1word)*/
        @Override
        public byte[] getBytesForReportState(byte[] sendData, byte[] GoodFoodData,byte[] UVData,
                                             boolean isAnswer) {
            int length = 27;
            ePlusData = new byte[length];
            ePlusData[0] = (byte) 0xFF;
            ePlusData[1] = (byte) 0xFF;
            ePlusData[2] = (byte) 0x18;// 帧长 等于范围 - 3         --//451 0X1a位
            ePlusData[3] = (byte) 0x00;
            ePlusData[4] = (byte) 0x00;
            ePlusData[5] = (byte) 0x00;
            ePlusData[6] = (byte) 0x00;
            ePlusData[7] = (byte) 0x00;
            ePlusData[8] = (byte) 0x00;

            if (isAnswer == true) {
                ePlusData[9] = 0x02;// ePlusData[8]=0x02;应答为02，上报为06

                Log.i("answer", "ans so it is 02");
            } else {
                ePlusData[9] = 0x06;// ePlusData[8]=0x02;应答为02，上报为06
                Log.i("answer", "report so it is 06");
            }

            ePlusData[10] = 0x6D;
            ePlusData[11] = 0x01;
            // ////////////数据///////////////////

            ePlusData[12] = sendData[6];// 冷藏室显示温度
            ePlusData[13] = sendData[7];// 冷冻室显示温度
            ePlusData[14] = sendData[8];// 变温室显示温度
            ePlusData[15] =0x00;//预留

             /*

            因冷藏室网络协议文档与TFT通信协议文档相差0x01，因此进行判断（除冷藏室关闭功能）
             */
            if(sendData[12]==0x00){
                ePlusData[16] =sendData[12];
            }else{
                ePlusData[16] =(byte)(sendData[12]-0x01);//冷藏档位设置温度(1byte)
            }

            ePlusData[17] =sendData[13];//冷冻档位设置温度(1byte)
            ePlusData[18] =sendData[14];//变温档位设置温度(1byte)
            ePlusData[19] = 0x00;// 256预留位

////////////////////////////////////////////////////////
            // ///////////////////以下是三个word用于控制模式等/其中安防模式未进行识别/////////////

            ePlusData[20] = sendData[18];
            ePlusData[21] = sendData[19];

            ePlusData[22] = sendData[20];
            ePlusData[23] = sendData[21];

            ePlusData[24] = sendData[22];
            ePlusData[25] = sendData[23];

            // 校验 加起来然后去一个byte//从真张算起到校验和前相加
            byte flag = 0x00;
            for (int i = 2; i < length-1; i++) {// 校验位，从帧长相加一直到校验位之前
                flag = (byte) (flag + ePlusData[i]);

            }

            ePlusData[length-1] = flag;// 校验帧

            return ePlusData;
        }
    }



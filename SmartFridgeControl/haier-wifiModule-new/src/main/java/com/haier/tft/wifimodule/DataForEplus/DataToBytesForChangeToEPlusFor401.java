package com.haier.tft.wifimodule.DataForEplus;

import android.util.Log;

/**
 * Created by Administrator on 2015/12/3.
 */
public class DataToBytesForChangeToEPlusFor401 extends DataToByteForChangeToEPlusForCommon{



    @Override
    public  byte[] getBytesForReportState(byte[] sendData, byte[] GoodFoodData,byte[] UVData,boolean isAnswer){
        ePlusData = new byte[25];
        ePlusData[0]=(byte) 0xFF;
        ePlusData[1]=(byte) 0xFF;
        ePlusData[2]=(byte) 0x16;//帧长 等于范围 - 3
        ePlusData[3]=(byte) 0x00;
        ePlusData[4]=(byte) 0x00;
        ePlusData[5]=(byte) 0x00;
        ePlusData[6]=(byte) 0x00;
        ePlusData[7]=(byte) 0x00;
        ePlusData[8]=(byte) 0x00;

        if(isAnswer==true){
            ePlusData[9]=0x02;// ePlusData[8]=0x02;应答为02，上报为06

            Log.i("answer", "ans so it is 02");
        }else{
            ePlusData[9]=0x06;// ePlusData[8]=0x02;应答为02，上报为06
            Log.i("answer", "report so it is 06");
        }

        ePlusData[10]=0x6D;
        ePlusData[11]=0x01;
        //////////////数据///////////////////
        ePlusData[12]=sendData[6];//冷藏室显示温度
        ePlusData[13]=sendData[7];//冷冻室显示温度
        ePlusData[14]=0x00;//401预留位
        ePlusData[15]=sendData[8];//冷藏档位设置
        ePlusData[16]=sendData[9];//冷冻档位设置
        ePlusData[17]=0x00;//401预留位
       /* recv[105] =  aa 55 1b 2 6d 1 3e 3c 4 8 2 6f 2 5e 2 66 0 0 0
        0 1 0 21 0 0 0 0 0 42 ae*/
        /////////////////////以下是三个word用于控制模式等//////////////
        //word A 速冷，速冻，智能与 强制开启关闭

        byte[] word6Byte= changeModelByteFromPCBBytesToNetBytes(sendData[20],sendData[22]);

        ePlusData[18]=word6Byte[0];
        ePlusData[19]=word6Byte[1];

        ePlusData[20]=word6Byte[2];
        ePlusData[21]=word6Byte[3];

        ePlusData[22]=word6Byte[4];
        ePlusData[23]=word6Byte[5];
///////////////////////////////////////////以下是一个word,两个byte,401无美食模式，因此屏蔽////////////////////////


//        if(GoodFoodData==null){
//
//            GoodFoodData[0]= (byte) 0xFE;
//            GoodFoodData[1]= (byte) 0xFE;
//        }
//
//        ePlusData[24]=GoodFoodData[0];
//        ePlusData[25]=GoodFoodData[1];

//        ePlusData[25]=StaticValueAndConnectUrl.GoodFoodModel[1];
        ///////////校验 加起来然后去一个byte//从真张算起到校验和前相加///////////////////
        byte flag=0x00;
        for(int i=2;i<24;i++){//校验位，从帧长相加一直到校验位之前
            flag=(byte) (flag+ePlusData[i]);


        }

        ePlusData[24]=flag;//校验帧


        return ePlusData;
    }

    /**
     * 将wordA 和B进行重新组合
     * @param
     */
public byte[] changeModelByteFromPCBBytesToNetBytes(byte PCBDataWordA0_A7,byte PCBDataWordB0_B7){
    byte NetByteA0_A7=0x00;
    byte NetByteB0_B7 =0x00;
    byte NetByteFreezeModle=0x00;
    byte NetByteColdModle=0x00;
    byte NetByteSmartColdModle=0x00;
    byte NetByteCleanModel=0x00;
    byte NetByteDoorOpen=0x00;
    byte[] WordBytes = new byte[6];
    //初始化

    for(int i=0;i<6;i++){
        WordBytes[i]=0x00;
    }


    /**
     * 以下为PCB 板,在word A上
     速冻开启：0000 0000 0000 0001       1
     速冻关闭：0000 0000 0000 0000       0

     速冷开启：0000 0000 0000 0010       2
     速冷关闭：0000 0000 0000 0000       0

     智能开启：0000 0000 0000 0100       4
     智能关闭：0000 0000 0000 0000       0

     净化开启：0000 0000 0000 1000       8
     净化关闭：0000 0000 0000 0000       0
     //////////////////////以下为网络协议，在wordA WordB  上///////////////////////////////////
    智能模式开启：0000 0000 0000 0010   00 02
    速冻模式开启：0000 0000 0000 1000   00 08
    速冷模式开启：0000 0000 0001 0000   00 10
    强制删除开启：1000 0000 0000 0000   80 00//此处可以不用

    0A:智能，速冻开启
    12：智能，速冷开启
    18：速冻，速冷开启
    1A：智能，速冷，速冻开启


    冷藏室门开启：0000 0000 0000 0001	00 01
    净化功能开启：0000 0000 0001 0000	00 10


     */

    NetByteFreezeModle= (byte) (PCBDataWordA0_A7&0x01);//获取速冻
    NetByteFreezeModle=(byte)(NetByteFreezeModle<<3);

    NetByteColdModle= (byte) (PCBDataWordA0_A7&0x02);//获取速冷
    NetByteColdModle=(byte)(NetByteColdModle<<3);

    NetByteSmartColdModle= (byte)(PCBDataWordA0_A7&0x04);//获取智能
    NetByteSmartColdModle=(byte)(NetByteSmartColdModle>>1);

    NetByteA0_A7 =(byte)(NetByteFreezeModle|NetByteColdModle|NetByteSmartColdModle);


    ////////////////////////以下为word B ////////////////////
    NetByteCleanModel= (byte)(PCBDataWordA0_A7&0x08);//获取净化模式
    NetByteCleanModel=(byte)(NetByteCleanModel<<2);

    NetByteDoorOpen=(byte)(PCBDataWordB0_B7&0x04);
    NetByteDoorOpen=(byte)(NetByteDoorOpen>>2);

    NetByteB0_B7=(byte)(NetByteCleanModel|NetByteDoorOpen);

    WordBytes[1]=NetByteA0_A7;
    WordBytes[3]=NetByteB0_B7;

      return WordBytes;
}


}

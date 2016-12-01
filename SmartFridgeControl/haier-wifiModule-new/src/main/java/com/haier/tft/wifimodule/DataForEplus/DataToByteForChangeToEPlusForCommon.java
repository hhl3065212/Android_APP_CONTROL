package com.haier.tft.wifimodule.DataForEplus;

import android.util.Log;

/**
 * Created by Administrator on 2015/12/3.
 */
public class DataToByteForChangeToEPlusForCommon {
    public  byte[] ePlusData ;
    public byte[] ePlusDataForError;
    public DataToByteForChangeToEPlusForCommon(){

    }
   ;

    public  byte[] getBytesForReportState(byte[] sendData, byte[] GoodFoodData,byte[] UVModel,boolean isAnswer){
        ePlusData = new byte[27];
        ePlusData[0]=(byte) 0xFF;
        ePlusData[1]=(byte) 0xFF;

        ePlusData[2]=(byte) 0x18;//帧长等于范围 - 3


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
        ePlusData[12]=sendData[6];
        ePlusData[13]=sendData[7];
        ePlusData[14]=sendData[8];
        ePlusData[15]=sendData[12];
        ePlusData[16]=sendData[13];
        ePlusData[17]=sendData[14];

        /////////////////////以下是三个word//////////////
        ePlusData[18]=sendData[18];
        ePlusData[19]=sendData[19];
        ePlusData[20]=sendData[20];
        ePlusData[21]=sendData[21];
        ePlusData[22]=sendData[22];
        ePlusData[23]=sendData[23];
///////////////////////////////////////////以下是一个word,两个byte////////////////////////


        if(GoodFoodData==null){

            GoodFoodData[0]= (byte) 0xFE;
            GoodFoodData[1]= (byte) 0xFE;
        }

        ePlusData[24]=GoodFoodData[0];
        ePlusData[25]=GoodFoodData[1];

//        ePlusData[25]=StaticValueAndConnectUrl.GoodFoodModel[1];
        ///////////校验 加起来然后去一个byte//从真张算起到校验和前相加///////////////////
        byte flag=0x00;
        for(int i=2;i<26;i++){
            flag=(byte) (flag+ePlusData[i]);


        }

        ePlusData[26]=flag;//校验帧


        return ePlusData;
    }

    public byte[] getBytesForReportError(byte[] sendData,boolean isAnswer){
        //TODO
        ePlusDataForError = new byte[5];
        return ePlusDataForError;
    }

}

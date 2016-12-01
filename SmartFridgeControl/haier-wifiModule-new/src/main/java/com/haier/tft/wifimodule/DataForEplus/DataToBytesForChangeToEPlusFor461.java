package com.haier.tft.wifimodule.DataForEplus;

import android.util.Log;

import com.haier.tft.wifimodule.moduletool.BytesToInt;

/**
 * Created by Administrator on 2016/4/6.
 */
public class DataToBytesForChangeToEPlusFor461 extends
        DataToByteForChangeToEPlusForCommon {


    @Override
    public byte[] getBytesForReportState(byte[] sendData, byte[] GoodFoodData,byte[] UVData,
                                         boolean isAnswer) {
        int length = 29;
        ePlusData = new byte[length];
        ePlusData[0] = (byte) 0xFF;
        ePlusData[1] = (byte) 0xFF;
        ePlusData[2] = (byte) 0x1a;// 帧长 等于范围 - 3         --//451 0X1a位
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
        ePlusData[14] = 0x00;// 451预留位
        ePlusData[15] =sendData[12];// 冷藏档位设置
        if(sendData[13]<2){
            ePlusData[16]=sendData[13];
        }else {
            ePlusData[16] = (byte)(sendData[13]-0x02) ;// 冷冻档位设置，正好相差2度，所以需要减2,判断是避免出现负数
        }

        ePlusData[17] = 0x00;// 451预留位

        // ///////////////////以下是三个word用于控制模式等//////////////
        // word A 速冷，速冻，智能与 强制开启关闭

        byte[] word6Byte = changeModelByteFromPCBBytesToNetBytes(sendData[19],sendData[21]);

        ePlusData[18] = word6Byte[0];
        ePlusData[19] = word6Byte[1];

        ePlusData[20] = word6Byte[2];
        ePlusData[21] = word6Byte[3];

        ePlusData[22] = word6Byte[4];
        ePlusData[23] = word6Byte[5];

        //TODO 暂时无酸奶机模式数据
        ePlusData[24] = 0x00;
        ePlusData[25] = 0x00;
        //TODO 451增加UV杀菌模式

        if(UVData == null){
            UVData = new byte[1];
            UVData[0] = (byte) 0x00;
        }

        ePlusData[26] = UVData[0];

        //杀菌模式结束------------
        // 校验 加起来然后去一个byte//从真张算起到校验和前相加
        byte flag = 0x00;
        for (int i = 2; i < length-1; i++) {// 校验位，从帧长相加一直到校验位之前
            flag = (byte) (flag + ePlusData[i]);

        }

        ePlusData[length-1] = flag;// 校验帧

        return ePlusData;
    }

    /**
     * 将wordA 和B进行重新组合
     *
     * @param
     *
    // * A0：不使用，强制固定为“0”。
    A1: 表示字A的第1位:
    为“1”时，表示“人工智慧”
    为“0”时，表示非“人工智慧”
    A2：表示字A的第2位:
    为“1”时，表示“假日”
    为“0”时，表示非“假日”
    A3: 表示字A的第3位:
    为“1”时，表示“速冻”
    为“0”时，表示非“速冻”
    A4：表示字A的第4位:
    为“1”时，表示“速冷”；
    为“0”时，表示非“速冷”。
    A5~A15：不使用，强制固定为“0”。
    -----------------------------------
    B0：表示字B的第0位:
    为“1”时，表示“冷藏室门开”；
    为“0”时，表示“冷藏室门关”。
    B1：表示字B的第1位:
    为“1”时，表示“冷冻室门开”；
    为“0”时，表示“冷冻室门关”。
    B2~B15：不使用，强制固定为“0”。
    ------------------------------------
    C10表示字C的第10位：
    为1时，表示‘安防开启’
    为0时，表示‘安防关闭’
    C11表示字C的第11位：
    为1时，表示‘酸奶机开启’
    为0时，表示‘酸奶机关闭’
    其他位强制为0
     */
    private byte[] changeModelByteFromPCBBytesToNetBytes(byte PCBDataWordA0_A7,byte PCBDataWordB0_B7) {
        //TODO 0x00 低8位
        byte NetByteA0_A7 = 0x00;
        byte NetByteB0_B7 = 0x00;

        //人工智能
        byte NetByteSmartColdModle = 0x00;
        //假日模式
        byte NetByteHolidayModel = 0x00;
        //速冻模式
        byte NetByteFreezeModle = 0x00;
        //速冷模式
        byte NetByteColdModle = 0x00;
        //冷冻室门开启
        byte NetByteFreezeDoorOpen = 0x00;
        //冷藏室门开启
        byte NetByteColdDoorOpen = 0x00;

        byte[] WordBytes = new byte[6];
        // 初始化
        for (int i = 0; i < 6; i++) {
            WordBytes[i] = 0x00;
        }

        /**
         * A1: 表示字A的第1位:
         为“1”时，表示“人工智慧”
         为“0”时，表示非“人工智慧”
         */
        NetByteSmartColdModle = (byte) (PCBDataWordA0_A7 & 0x02);// 获取人工智慧
        /**
         * A2：表示字A的第2位:
         为“1”时，表示“假日”
         为“0”时，表示非“假日”
         */
        NetByteHolidayModel = (byte) (PCBDataWordA0_A7 & 0x04);// 获取假日
        /**
         * A3: 表示字A的第3位:
         为“1”时，表示“速冻”
         为“0”时，表示非“速冻”
         */
        NetByteFreezeModle = (byte) (PCBDataWordA0_A7 & 0x08);// 获取速冻
        /**
         * A4：表示字A的第4位:
         为“1”时，表示“速冷”；
         为“0”时，表示非“速冷”。
         */
        NetByteColdModle = (byte) (PCBDataWordA0_A7 & 0x10);// 获取速冷


        NetByteA0_A7 = (byte) (NetByteSmartColdModle | NetByteHolidayModel | NetByteFreezeModle | NetByteColdModle);

        // //////////////////////以下为word B ////////////////////
        /**
         * B0：表示字B的第0位:
         为“1”时，表示“冷藏室门开”；
         为“0”时，表示“冷藏室门关”。
         */
        NetByteFreezeDoorOpen = (byte) (PCBDataWordB0_B7 & 0x01);// 获取净化模式

        /***
         * B1：表示字B的第1位:
         为“1”时，表示“冷冻室门开”；
         为“0”时，表示“冷冻室门关”。
         */
        NetByteColdDoorOpen = (byte) (PCBDataWordB0_B7 & 0x02);

        NetByteB0_B7 = (byte) (NetByteFreezeDoorOpen | NetByteColdDoorOpen);

        //A字段低8位
        WordBytes[1] = NetByteA0_A7;
        //B字段低8位
        WordBytes[3] = NetByteB0_B7;

        //test
        BytesToInt byteToInt = new BytesToInt();

        Log.d("data", "转换后的word为："+"--"+byteToInt.bytesToHexString(WordBytes));


        return WordBytes;
    }

}


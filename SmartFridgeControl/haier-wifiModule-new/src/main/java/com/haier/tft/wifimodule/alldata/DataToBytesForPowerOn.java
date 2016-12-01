package com.haier.tft.wifimodule.alldata;

import android.util.Log;

import com.haier.tft.wifimodule.moduletool.StringToBytes;

public class DataToBytesForPowerOn {

    public byte[] getSendVersionToGenServer165Byte(String devicetypid ,String mMac){

        StringToBytes mStingToBinary = new StringToBytes();

        byte[] data =new byte[165];
        /////10019
        data[0]=0x00;
        data[1]=0x00;
        data[2]=0x27;
        data[3]=0x23;

        ////ERROR_NUM
        for(int i =4;i<8;i++){
            data[i]=(byte) 0x00;
        }

        byte[] devicetype = mStingToBinary.getDeviceTypeTo32Bytes(devicetypid);
        int j=8;
        for(int i =0;i<devicetype.length;i++){
            data[j]= devicetype[i];
            j++;
        }
        byte[] MAC =mStingToBinary.getMACTo32Bytes(mMac);

        j=40;
        for(int i =0;i<MAC.length;i++){
            data[j]= MAC[i];
            j++;
        }

        ////SN
        data[72]=0x00;
        data[73]=0x00;
        data[74]=0x00;
        data[75]=0x01;

        //   out.write(LEN);

        data[76]=0x00;
        data[77]=0x00;
        data[78]=0x00;
        data[79]=0x55;

        // out.write(CMD_WIFI_BASE_INFO);
        data[80]=0x00;
        data[81]=0x00;
        data[82]=0x4E;
        data[83]=0x2C;

        //  out.write(mStingToBinary.getVersionMyself8Byte("1.0.0"));
        j=84;
        for(int i=0;i<mStingToBinary.getVersionMyself8Byte("e_2.0.00").length;i++){
            data[j]=mStingToBinary.getVersionMyself8Byte("e_2.0.00")[i];
            j++;
        }
        //out.write(mStingToBinary.getVersionDevFile8Byte("1.0.0"));
        j=92;
        for(int i=0;i<mStingToBinary.getVersionDevFile8Byte("0.0.0").length;i++){
            data[j]=mStingToBinary.getVersionDevFile8Byte("0.0.0")[i];
            j++;
        }
//       out.write(mStingToBinary.getVersionProtocol8Byte("2.00"));
        j=100;
        for(int i=0;i<mStingToBinary.getVersionProtocol8Byte("2.00").length;i++){
            data[j]=mStingToBinary.getVersionProtocol8Byte("2.00")[i];
            j++;
        }
        //    out.write(mStingToBinary.get_HARDWARE_VER_8Byte("1_0.0.00"));
        j=108;
        for(int i=0;i<mStingToBinary.get_HARDWARE_VER_8Byte("g_0.0.00").length;i++){
            data[j]=mStingToBinary.get_HARDWARE_VER_8Byte("g_0.0.00")[i];
            j++;
        }

        //
        // out.write(mStingToBinary.getPlatform32Byte("UDISCOVERY_UWT"));
        j=116;
        for(int i=0;i<mStingToBinary.getPlatform32Byte("UHOME_TFT_FRIDGE").length;i++){
            data[j]=mStingToBinary.getPlatform32Byte("UHOME_TFT_FRIDGE")[i];
            j++;
        }
        //out.write(mStingToBinary.get_BaseBoard_SW_VER_8Byte("1.0.0"));
        j=148;
        for(int i=0;i<mStingToBinary.get_BaseBoard_SW_VER_8Byte("20150502").length;i++){
            data[j]=mStingToBinary.get_BaseBoard_SW_VER_8Byte("20150502")[i];
            j++;
        }
        //     out.write(mStingToBinary.get_BaseBoard_HW_VER_8Byte("1.0.0"));
        j=156;
        for(int i=0;i<mStingToBinary.get_BaseBoard_HW_VER_8Byte("20150502").length;i++){
            data[j]=mStingToBinary.get_BaseBoard_HW_VER_8Byte("20150502")[i];
            j++;
        }

        //   out.write(mStingToBinary.get_BaseBoard_update_feature_1Byte(0));
        j=164;
        data[j]=0x00;

        return data;
    }






}

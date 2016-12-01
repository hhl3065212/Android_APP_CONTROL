package com.haier.tft.wifimodule.alldata;

import com.haier.tft.wifimodule.moduletool.StringToBytes;


public class DataToBytesForHeartBeat {


    /**
     * 参考4.2.5，
     * @param WifiStrengh
     * @param mMac
     * @return
     */
    public byte[] getHeartBeatData( String mMac,short WifiStrengh){

        StringToBytes mStingToBinary = new StringToBytes();

        byte[] data =new byte[88];
        /////10007
        data[0]=0x00;
        data[1]=0x00;
        data[2]=0x27;
        data[3]=0x1F;

        ////ERROR_NUM
        for(int i =4;i<8;i++){
            data[i]=(byte) 0x00;
        }

        //Session 32bytes
//         byte[] devicetype = mStingToBinary.getDeviceTypeTo32Bytes(devicetypid);
        int j=8;
        for(int i =0;i<32;i++){
            data[j]=0x00;
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

        //   out.write(LEN); 68(44) or 88(58)

        data[76]=0x00;
        data[77]=0x00;
        data[78]=0x00;
        data[79]=0x08;

        // out.write(DURATION);若上次未收到则写0xFFFFFFFF
        data[80]=(byte) 0xFF;
        data[81]=(byte) 0xFF;
        data[82]=(byte) 0xFF;
        data[83]=(byte) 0xFF;


        //STRENGH
        byte[] strengh =new byte[2];
        strengh=  mStingToBinary.getShortTo2Bytes(WifiStrengh);
        j=84;
        for(int i=0;i<2;i++){

            data[j]=  strengh[i];
            j++;
        }
        //NETWORK_TYPE
        byte[] NETWORK_TYPE=  mStingToBinary.getShortTo2Bytes((short) 1);
        for(int i=0;i<2;i++){

            data[j]=  NETWORK_TYPE[i];
            j++;
        }




        return data;
    }







}

package com.haier.tft.wifimodule.alldata;

import com.haier.tft.wifimodule.moduletool.StringToBytes;



public class DataToBytesForReportVersionToControlServer {


    /**
     * 参考4.5.1，如果要用通用传输数据，则需要将version myself至为u首写字母
     * @param devicetypid
     * @param mMac
     * @return
     */
    public byte[] getBytesVersion1(String devicetypid ,String mMac){

        StringToBytes mStingToBinary = new StringToBytes();

        byte[] data =new byte[168];
        /////10007
        data[0]=0x00;
        data[1]=0x00;
        data[2]=0x27;
        data[3]=0x17;

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

        //   out.write(LEN); 68(44) or 88(58)

        data[76]=0x00;
        data[77]=0x00;
        data[78]=0x00;
        data[79]=0x58;

        // out.write(CMD_Report_Version);20001
        data[80]=0x00;
        data[81]=0x00;
        data[82]=0x4E;
        data[83]=0x21;

        //  out.write(mStingToBinary.getVersionMyself8Byte("1.0.0"));
        j=84;
        for(int i=0;i<mStingToBinary.getVersionMyself8Byte("e_2.0.00").length;i++){
            data[j]=mStingToBinary.getVersionMyself8Byte("e_2.0.00")[i];
//        	 data[j]=mStingToBinary.getVersionMyself8Byte("e_2.1.10")[i];
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
        for(int i=0;i<mStingToBinary.get_HARDWARE_VER_8Byte("g_2.0.00").length;i++){
            data[j]=mStingToBinary.get_HARDWARE_VER_8Byte("g_2.0.00")[i];
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

        //OFFLINE_CAUSE？？？

        char OFFLINE_CAUSE ='1';
        data[165]=(byte) OFFLINE_CAUSE;


        //STRENGH 可以变动 ？？？
//       short STRENGH ='1';
        data[166]=(byte) 0X00;
        data[167]=(byte) 0X50;

        return data;
    }







}

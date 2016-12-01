package com.haier.tft.wifimodule.alldata;

import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;
import com.haier.tft.wifimodule.moduletool.StringToBytes;

public class DataToByteForSendToServer {



/**
 * 参考 4.3.2
 * @param 
 * @param mMac
 * @return
 */
	public byte[] getBytesToSendToServer (byte[] Session ,String mMac,byte[] sn,byte[] Databytes){
		
		StringToBytes mStingToBinary = new StringToBytes();
		
		if(Session==null||Session.length<10){
			Session =new byte[32];
			for(int i=0;i<32;i++){
				Session[i]=0;
			}
		}
		int len;
		if(Databytes==null){
			len=0;
		}
		 len=Databytes.length;

		
		int size=80+len;
		
		
		byte[] data =new byte[size];
         /////10005
         data[0]=0x00;
         data[1]=0x00;
         data[2]=0x27;
         data[3]=0x15;
         
         ////ERROR_NUM
         for(int i =4;i<8;i++){
        	 data[i]=(byte) 0x00;
         }
         
         //Session
         
         int j=8;
         for(int i =0;i<Session.length;i++){
        	 data[j]= Session[i];
        	 j++;
         }
         //MAC

         byte[] MAC =mStingToBinary.getMACTo32Bytes(mMac);
         
          j=40;
         for(int i =0;i<MAC.length;i++){
        	 data[j]= MAC[i];
        	 j++;
         }
         
         
        ////SN
         if(sn==null){
         
         data[72]=0x00;
         data[73]=0x00;
         data[74]=0x00;
         data[75]=0x01;
         }else{
        	 
         data[72]=sn[0];
         data[73]=sn[1];
         data[74]=sn[2];
         data[75]=sn[3];

         }
         
      //   out.write(LEN); 68(44) or 88(58)
       byte[] mbyte = new byte[4];
       
       mbyte=    mStingToBinary.getIntTo4Bytes(len);
       j=76;
       for(int i=0;i<4;i++){
    	   data[j]=mbyte[i];
    	   j++;
       }

///////////////////////////ITOPHOME_CMD_DATA///////////////////////////////////////////
            j=80;
       for(int i=0;i<Databytes.length;i++){
    	   
//    	   Log.i("ceshi2", "Databytes i ="+i+"  j = "+j);
    	   data[j]=Databytes[i];
    	   j++;
       }

  
		return data;
	}
	
	
	
	
	
	


}

package com.haier.tft.wifimodule.resolvingXml;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.haier.tft.wifimodule.moduletool.BytesToInt;
import com.haier.tft.wifimodule.moduletool.ControlPrefence;
import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;

public class DataHandle {

	private byte[] getData;
	private int len;
	private int kind;
	private byte[] SN = new byte[4];
	private ControlPrefence mControlPrefence;

	
	private BytesToInt myByteToInt =new BytesToInt();
	public DataHandle(byte[] data,int len){
		
		this.getData=data;
		this.len=len;
	}
	
	
	public void GetDataForSort(){
		
		BytesToInt mByteToInt = new BytesToInt();
		mControlPrefence =ControlPrefence.getInstance();
		
		if(len==79){


			kind=mByteToInt.getInt(0, 4, getData);
			
			
			
			
		}
		
		else if(len==80){
			kind=mByteToInt.getInt(0, 4, getData);
		
		}
		
		else {
			
			kind=mByteToInt.getInt(0, 4, getData);
			
		}
		
		
		
		/**
		 * 心跳
		 */

		if(kind==10016){
			

			Log.i("sdk","yes get heartdata from server");
			Message mess = new Message();
			mess.what=0x02;
           StaticValueAndConnectUrl.Handler.sendMessage(mess);


			
		}
		
		/**
		 * 控制服务器到设备4.3.1
		 */
		
		if(kind==10004){
			StaticValueAndConnectUrl.Session = new byte[32];
			int j=0;
			for(int i=8;i<40;i++){
				
				StaticValueAndConnectUrl.Session[j]=getData[i];
				
				j++;
			}
			
			j=0;
			for(int i =72;i<76;i++){
				
				SN[j]=getData[i];
				j++;
				
			}
			mControlPrefence.addMessageForSN(SN);
			
			Log.i("answer", "receive session = "+mByteToInt.bytesToHexString(StaticValueAndConnectUrl.Session));
			Log.i("answer", "receive sn = "+mByteToInt.bytesToHexString(SN));

			mControlPrefence.setIsAnswer(true);
			/**
			 * 此处代表长度
			 */
		    int mylen =mByteToInt.getInt(76, 80, getData);
			
		   byte[] CmdFromServer=new byte[mylen];
		    
		   j=80;//从第80字节开始，参考4.3.1
		   for(int i =0;i<mylen;i++){
			   
			   CmdFromServer[i] =getData[j];

			   j++;
		   }

//			mControlPrefence.setCmdFromServer(CmdFromServer);
		
		   
		    BytesToInt m = new BytesToInt();
		    Log.i("data", "from server ="+m.bytesToHexString( CmdFromServer));
		    
		    
		    Bundle mBundle = new Bundle();
		    String key =GetBroadCastKey(CmdFromServer);
		    mBundle.putString("BroadCastKey", key);	
            if(key.equalsIgnoreCase("4d01")){//当服务器查询的时候，回复的指令无需02，需要发送06，因此此处为06
				mControlPrefence.setIsAnswer(true);
            }
		    Message msg =new Message();
		    msg.setData(mBundle);
		    msg.what=0x05;//接收到服务器指令直接转发
		    StaticValueAndConnectUrl.Handler.sendMessage(msg);

		}
		
		
	}
	

/*: from server =ff ff 0a 00 00 00 00 00 00 01 4d 065e
: from server =ff ff0c000000000000015d03000471
: from server =ff ff 0c 00 00 00 00 00 00 01 5d 03000673*/
	
	private String GetBroadCastKey(byte[] data){
		 int len =data.length;
		 
		 if(len<11){
			 return "0"; 
		 }
		 
       if(data[10]==0x4d){
    	   
    	   byte[] mydata =new byte[2];
    	   mydata[0]=data[10];
    	   mydata[1]=data[11];
    	 return   myByteToInt.bytesToHexString(mydata);
       }
       
       if(data[10]==0x5d){
    	   
    	   byte[] mydata =new byte[4];
    	   mydata[0]=data[10];
    	   mydata[1]=data[11];
    	   mydata[2]=data[12];
    	   mydata[3]=data[13];
    	   return   myByteToInt.bytesToHexString(mydata);
       }
       
      
           return "0";
		 
	}
	
	
	
}

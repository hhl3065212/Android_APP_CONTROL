package com.haier.tft.wifimodule.alldata;

import java.util.HashMap;

import android.util.Log;

import com.haier.tft.wifimodule.alldata.BroadCastHashMapData.SendBroadDataMapFactory;
import com.haier.tft.wifimodule.moduletool.ControlPrefence;
import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;

public class BroadCastHashMap {

	private static HashMap<String, String> mydata =new HashMap<String, String>();
    private static String getBroadFlagForGoodFoodNum(String key){


	   if(key.length()>7){

      String firstFlag=   key.substring(0,4);
	  if(firstFlag.equalsIgnoreCase("5d0f")){
         String endFlag =key.substring(4,8);

		  return "setGoodFood:"+endFlag;

	  }

	   }
             return null;
   }



	public static String getBroadKey(String key){

		mydata=SendBroadDataMapFactory.getInstance(StaticValueAndConnectUrl.devtype).getBroadCastHashMap();

		String myBroadCast = getBroadFlagForGoodFoodNum(key);
		if(myBroadCast==null){
			myBroadCast=mydata.get(key);
		}

		Log.i("sdk", "key = "+key +" that is for find the broadcast and the trans key is " +myBroadCast);

		return myBroadCast;
	}

}

package com.haier.tft.wifimodule.alldata.BroadCastHashMapData;

import android.util.Log;

import com.haier.tft.wifimodule.DataForEplus.DataToByteForChangeToEPlusForCommon;
import com.haier.tft.wifimodule.DataForEplus.DataToBytesForChangeToEPlusFor251;
import com.haier.tft.wifimodule.DataForEplus.DataToBytesForChangeToEPlusFor401;
import com.haier.tft.wifimodule.DataForEplus.DataToBytesForChangeToEPlusFor451;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SendBroadDataMapFactory {

    private static SendBroadDataMapFactory intance;
    private static String oldTypeid="1";
    private static SendBroadCommonDataMap dataInstance;


    private SendBroadDataMapFactory(){


    }


    /**
     * 因为担心typeid会改变，因此每次调用时都对比下是否一致
     * @param typied
     * @return
     */
    public static SendBroadDataMapFactory getInstance(String typied){


        Log.i("sdk","SendBroadDataMapFactory typied ="+typied);
        if(intance==null||(!oldTypeid.equalsIgnoreCase(typied))){
            Log.i("sdk","SendBroadDataMapFactory==null||(!oldTypeid.equalsIgnoreCase(typied) "+typied);
            intance=new SendBroadDataMapFactory();
            //451适配
             if(typied.equalsIgnoreCase("111c120024000810010200618002834500000000000000000000000000000000")){
                dataInstance = new SendBroadDataMapFor451();
                Log.i("sdk", "create 451 control Data");
            }
             //适配461
             else if(typied.equalsIgnoreCase("111c120024000810010100618003474600000000000000000000000000000000")){
                 dataInstance = new SendBroadDataMapFor461();
                 Log.i("sdk", "create 461 control Data");
             }
             else if(typied.equalsIgnoreCase("111c120024000810010300618004770000000000000000000000000000000000")){
                 dataInstance = new SendBroadDataMapFor225();
                 Log.i("sdk", "create 225 control Data");
             }
            else {
                dataInstance =new SendBroadCommonDataMap();
            }

            oldTypeid=typied;

        }


        return intance;
    }

    public HashMap<String, String> getBroadCastHashMap(){

        return dataInstance.getBroadHasMap();

    }

}

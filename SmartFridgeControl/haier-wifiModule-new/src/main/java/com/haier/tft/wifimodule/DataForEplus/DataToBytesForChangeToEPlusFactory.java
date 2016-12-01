package com.haier.tft.wifimodule.DataForEplus;

import android.util.Log;

/**
 * Created by Administrator on 2015/12/3.
 */
public class DataToBytesForChangeToEPlusFactory {

    private static DataToByteForChangeToEPlusForCommon  dataInstance;
    private static DataToBytesForChangeToEPlusFactory intance;
    private static String oldTypeid="0";


    protected   DataToBytesForChangeToEPlusFactory(){

     }

    /**
     * 因为担心typeid会改变，因此每次调用时都对比下是否一致
     * @param typied
     * @return
     */
    public static DataToBytesForChangeToEPlusFactory getInstance(String typied){
           if(intance==null||(!oldTypeid.equalsIgnoreCase(typied))){
               intance=new DataToBytesForChangeToEPlusFactory();

               if(typied.equalsIgnoreCase("111c120024000810010200618001184200000000000000000000000000000000")){
                   Log.i("sdk","create 251 control Data");
                   dataInstance = new DataToBytesForChangeToEPlusFor251();
               }
               else if(typied.equalsIgnoreCase("111c120024000810010100618004470000000000000000000000000000000000")){
                   dataInstance = new DataToBytesForChangeToEPlusFor401();
                   Log.i("sdk", "create 401 control Data");
               } 
               //451适配
               else if(typied.equalsIgnoreCase("111c120024000810010200618002834500000000000000000000000000000000")){
                   dataInstance = new DataToBytesForChangeToEPlusFor451();
                   Log.i("sdk", "create 451 control Data");
               }
               //461适配
//               111c120024000810010100618003474600000000000000000000000000000000
               else if(typied.equalsIgnoreCase("111c120024000810010100618003474600000000000000000000000000000000")){
                   dataInstance = new DataToBytesForChangeToEPlusFor461();
                   Log.i("sdk", "create 461 control Data");
               }
               //256适配
               else if(typied.equalsIgnoreCase("111C120024000810010300618004584100000000000000000000000000000000")){
                   dataInstance = new DataToBytesForChangeToEPlusFor256();
                   Log.i("sdk", "create 256 control Data");
               }

               //225适配

               else if(typied.equalsIgnoreCase("111c120024000810010300618004770000000000000000000000000000000000")){
                   dataInstance = new DataToBytesForChangeToEPlusFor225();
                   Log.i("sdk", "create 225 control Data");
               }

               else {
                   Log.e("sdk","create DataToBytesForChangeToEPlusFactory failed the typeid is = "+typied);
                   intance=null;
               }
               oldTypeid=typied;

           }


          return intance;
    }
    
    /**
     * 根据汇报数据生成对应的网络数据字节数组
     * @date 2016-2-27 下午4:12:43
     * @description TODO
     * @param sendData
     * @param GoodFoodData 美食模式
     * @param UVData UV杀菌
     * @param isAnswer
     * @return
     */
    public byte[] getBytesForReportState(byte[] sendData, byte[] GoodFoodData,byte[] UVData,boolean isAnswer){
       return dataInstance.getBytesForReportState(sendData,GoodFoodData,UVData,isAnswer);
   }

    public byte[] getBytesForReportError(byte[] sendData,boolean isAnswer){
        return dataInstance.getBytesForReportError( sendData, isAnswer);
    }

}

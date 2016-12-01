package com.haier.tft.wifimodule.alldata.BroadCastHashMapData;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SendBroadDataMapFor451 extends SendBroadCommonDataMap{

   public SendBroadDataMapFor451(){
      super();
   }

    @Override
    public HashMap<String, String> getBroadHasMap(){
        super.getBroadHasMap();

/////////////////////////////////冷冻室温度控制///////////////////////////////////
        mydata.put("5d030000", "setFreeze:-24");
        mydata.put("5d030001", "setFreeze:-23");
        mydata.put("5d030002", "setFreeze:-22");
        mydata.put("5d030003", "setFreeze:-21");
        mydata.put("5d030004", "setFreeze:-20");
        mydata.put("5d030005", "setFreeze:-19");
        mydata.put("5d030006", "setFreeze:-18");
        mydata.put("5d030007", "setFreeze:-17");
        mydata.put("5d030008", "setFreeze:-16");
        mydata.put("5d030009", "setFreeze:-15");
        mydata.put("5d03000a", "setFreeze:-14");
        /////////////////////////////////////以下为杀菌模式////////////////////////////

        mydata.put("5d0c0000","setUVModel:0");
        mydata.put("5d0c0001","setUVModel:1");
        mydata.put("5d0c0002","setUVModel:2");
        mydata.put("5d0c0003","setUVModel:3");
        mydata.put("5d0c0004","setUVModel:4");
        mydata.put("5d0c0005","setUVModel:5");
        mydata.put("5d0c0006","setUVModel:6");
        mydata.put("5d0c0007","setUVModel:7");
        mydata.put("5d0c0008","setUVModel:8");
        mydata.put("5d0c0009","setUVModel:9");

        return mydata;
    }
}

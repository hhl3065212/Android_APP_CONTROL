package com.haier.tft.wifimodule.alldata.BroadCastHashMapData;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SendBroadDataMapFor225 extends SendBroadCommonDataMap{

    public SendBroadDataMapFor225(){
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

/////////////////////////////////冷藏室温度控制///////////////////////////////////
        mydata.put("5d020000", "setCold:OFF");

        mydata.put("5d020001", "setCold:2");
        mydata.put("5d020002", "setCold:3");
        mydata.put("5d020003", "setCold:4");
        mydata.put("5d020004", "setCold:5");
        mydata.put("5d020005", "setCold:6");
        mydata.put("5d020006", "setCold:7");
        mydata.put("5d020007", "setCold:8");
        mydata.put("5d020008", "setCold:9");
        mydata.put("5d020009", "setCold:10");

///////////////////////////变温室温度控制//////////////////////////////////
        mydata.put("5d040000", "setCustomArea:-7");
        mydata.put("5d040001", "setCustomArea:-6");
        mydata.put("5d040002", "setCustomArea:-5");
        mydata.put("5d040003", "setCustomArea:-4");
        mydata.put("5d040004", "setCustomArea:-3");
        mydata.put("5d040005", "setCustomArea:-2");
        mydata.put("5d040006", "setCustomArea:-1");
        mydata.put("5d040007", "setCustomArea:0");
        mydata.put("5d040008", "setCustomArea:1");
        mydata.put("5d040009", "setCustomArea:2");
        mydata.put("5d04000a", "setCustomArea:3");
        mydata.put("5d04000b", "setCustomArea:4");
        mydata.put("5d04000c", "setCustomArea:5");
        mydata.put("5d04000d", "setCustomArea:6");
        mydata.put("5d04000e", "setCustomArea:7");
        mydata.put("5d04000f", "setCustomArea:8");
        mydata.put("5d040010", "setCustomArea:9");
        mydata.put("5d040011", "setCustomArea:10");
        mydata.put("5d040012", "setCustomArea:OFF");

        return mydata;
    }
}

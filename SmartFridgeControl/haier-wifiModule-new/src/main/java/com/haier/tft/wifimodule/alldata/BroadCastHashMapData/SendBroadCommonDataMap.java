package com.haier.tft.wifimodule.alldata.BroadCastHashMapData;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/17.
 */
public class SendBroadCommonDataMap {

    public static HashMap<String, String> mydata =new HashMap<String, String>();

    public HashMap<String, String> getBroadHasMap(){
/*		序号	指令含义	控制命令说明	返回命令说明	6d01字节位置
		（如果没有对应状态，此列为空）
		1	查询命令	4d01 	6d01 + all status
		2	进人工智慧	4d04 	6d01 + all status	WordA 第1位
			退人工智慧	4d05 	6d01 + all status	WordA 第1位
		3	进假日	4d06 	6d01 + all status	WordA 第2位
			退假日	4d07 	6d01 + all status	WordA 第3位
		4	进速冻	4d08 	6d01 + all status	WordA 第3位
			退速冻	4d09 	6d01 + all status	WordA 第3位
		5	进速冷	4d0a	6d01 + all status	WordA 第4位
			退速冷	4d0b	6d01 + all status	WordA 第4位
		6	冷藏室档位设置	5d02 + clevel( 1word )	6d01 + all status	6d01后第4字节
		7	冷冻室档位设置	5d03 + dlevel( 1 word )	6d01 + all status	6d01后第5字节
		8	变温室档位设置	5d04 + wlevel( 1word )	6d01 + all status	6d01后第6字节
						*/


        mydata.put("4d01", "query");
        ////////////////////////四种模式//////////////
        mydata.put("4d04", "model:openSmartModel");
        mydata.put("4d05", "model:closeSmartModel");

        mydata.put("4d06", "model:openHolidayModel");
        mydata.put("4d07", "model:closeHolidayModel");

        mydata.put("4d08", "model:openFreezeModel");
        mydata.put("4d09", "model:closeFreezeModel");

        mydata.put("4d0a", "model:openColdModel");
        mydata.put("4d0b", "model:closeColdModel");

        /**
         * 进入净化模式与退出净化模式
         */
        mydata.put("4d2d", "model:openCleanModel");
        mydata.put("4d2e", "model:closeCleanModel");

            /**
             * 珍品模式的打开关闭
              */
        mydata.put("4d46", "model:openZhenPinModel");
        mydata.put("4d47", "model:closeZhenPinModel");

/////////////////////////////////冷冻室温度控制///////////////////////////////////
        mydata.put("5d030000", "setFreeze:-26");
        mydata.put("5d030001", "setFreeze:-25");
        mydata.put("5d030002", "setFreeze:-24");
        mydata.put("5d030003", "setFreeze:-23");
        mydata.put("5d030004", "setFreeze:-22");
        mydata.put("5d030005", "setFreeze:-21");
        mydata.put("5d030006", "setFreeze:-20");
        mydata.put("5d030007", "setFreeze:-19");
        mydata.put("5d030008", "setFreeze:-18");
        mydata.put("5d030009", "setFreeze:-17");
        mydata.put("5d03000a", "setFreeze:-16");
        /**
         * 以下针对401添加的
         */
        mydata.put("5d03000b", "setFreeze:-15");
        /////////////////////////////////////////////////////
        mydata.put("5d03000c", "setFreeze:-14");
        mydata.put("5d03000d", "setFreeze:-13");


/////////////////////////////////冷藏室温度控制///////////////////////////////////
        mydata.put("5d020000", "setCold:OFF");
        /**
         * 401的有1度
         */
        mydata.put("5d020001", "setCold:1");
        mydata.put("5d020002", "setCold:2");
        mydata.put("5d020003", "setCold:3");
        mydata.put("5d020004", "setCold:4");
        mydata.put("5d020005", "setCold:5");
        mydata.put("5d020006", "setCold:6");
        mydata.put("5d020007", "setCold:7");
        mydata.put("5d020008", "setCold:8");
        mydata.put("5d020009", "setCold:9");
        mydata.put("5d02000a", "setCold:10");


/////////////////////////////////变温室温度控制///////////////4 19////////////////////
        mydata.put("5d040003", "setCustomArea:-18");
        mydata.put("5d040004", "setCustomArea:-17");
        mydata.put("5d040005", "setCustomArea:-16");
        mydata.put("5d040006", "setCustomArea:-15");
        mydata.put("5d040007", "setCustomArea:-14");
        mydata.put("5d040008", "setCustomArea:-13");
        mydata.put("5d040009", "setCustomArea:-12");
        mydata.put("5d04000a", "setCustomArea:-11");
        mydata.put("5d04000b", "setCustomArea:-10");
        mydata.put("5d04000c", "setCustomArea:-9");
        mydata.put("5d04000d", "setCustomArea:-8");
        mydata.put("5d04000e", "setCustomArea:-7");
        mydata.put("5d04000f", "setCustomArea:-6");
        mydata.put("5d040010", "setCustomArea:-5");
        mydata.put("5d040011", "setCustomArea:-4");
        mydata.put("5d040012", "setCustomArea:-3");
        mydata.put("5d040013", "setCustomArea:-2");
        mydata.put("5d040014", "setCustomArea:-1");
        mydata.put("5d040015", "setCustomArea:0");
        mydata.put("5d040016", "setCustomArea:1");
        mydata.put("5d040017", "setCustomArea:2");
        mydata.put("5d040018", "setCustomArea:3");
        mydata.put("5d040019", "setCustomArea:4");
        mydata.put("5d04001a", "setCustomArea:5");

        return mydata;
    }

}

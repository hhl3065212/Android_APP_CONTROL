package com.haiersmart.sfcontrol.service.configtable;

/**
 * Created by Holy.Han on 2016/9/23 14:41
 * email hanholy1210@163.com
 */
public class ProtocolConfig {
    private final String TAG = "ProtocolConfig";

    public ProtocolConfig() {
    }

    public final ProtocolConfigBase[] configBCD325 = new ProtocolConfigBase[]{
            new ProtocolConfigBase("111c12002400081001030061800446410000000000", 0, 0, 0),
            new ProtocolConfigBase("refrigeratorShowTemperature", 1, 0, 255),
            new ProtocolConfigBase("freezerShowTemperature", 1, 1, 0xff),
            new ProtocolConfigBase("variableShowTemperature", 1, 2, 0xff),
            new ProtocolConfigBase("envShowTemperature", 1, 4, 0xff),
            new ProtocolConfigBase("refrigeratorTargetTemperature", 0, 6, 0xff),
            new ProtocolConfigBase("freezerTargetTemperature", 0, 7, 0xff),
            new ProtocolConfigBase("variableTargetTemperature", 0, 8, 0xff),

            new ProtocolConfigBase("intelligenceMode", 0, 12, 1),
            new ProtocolConfigBase("holidayMode", 0, 12, 2),
            new ProtocolConfigBase("quickFreezingMode", 2, 12, 3),
            new ProtocolConfigBase("quickRefrigerationMode", 2, 12, 4),

            new ProtocolConfigBase("refrigeratorDoorStatus", 1, 14, 8),

            new ProtocolConfigBase("envTemperatureSensorErr", 1, 22, 0),
            new ProtocolConfigBase("refrigeratorTemperatureErr", 1, 22, 1),
            new ProtocolConfigBase("freezerTemperatureErr", 1, 22, 3),
            new ProtocolConfigBase("variableTemperatureErr", 1, 22, 4),
            new ProtocolConfigBase("defrostingSensorErr", 1, 22, 8),
            new ProtocolConfigBase("communicationErr", 1, 22, 10),
            new ProtocolConfigBase("freezerDefrostingSensorErr", 1, 22, 14),
            new ProtocolConfigBase("refrigeratorDoorErr", 1, 20, 14),
    };
    public final ProtocolConfigBase[] configBCD251 = new ProtocolConfigBase[]{
            new ProtocolConfigBase("111c12002400081001030061800118420000000000", 0, 0, 0),//type id
            new ProtocolConfigBase("fridgeShowTemp", 1, 7, 0xff),//冷藏显示温度
            new ProtocolConfigBase("freezeShowTemp", 1, 8, 0xff),//冷冻显示温度
            new ProtocolConfigBase("changeShowTemp", 1, 9, 0xff),//变温显示温度
            new ProtocolConfigBase("envShowTemp", 1, 11, 0xff),//环境显示温度
            new ProtocolConfigBase("fridgeTargetTemp", 0, 13, 0xff,2,10,0),//冷藏目标温度
            new ProtocolConfigBase("freezeTargetTemp", 0, 14, 0xff,-24,-16,-26),//冷冻目标温度
            new ProtocolConfigBase("changeTargetTemp", 0, 15, 0xff,-18,5,-21),//变温目标温度

            new ProtocolConfigBase("smartMode", 0, 20, 1),//智能模式
            new ProtocolConfigBase("holidayMode", 0, 20, 2),//假日模式
            new ProtocolConfigBase("quickFreezeMode", 0, 20, 3),//速冻模式
            new ProtocolConfigBase("quickColdMode", 0, 20, 4),//速冷模式

            new ProtocolConfigBase("fridgeDoorStatus", 1, 22, 0),//冷藏门
            new ProtocolConfigBase("marketDemo", 1, 21, 0),//商场演示

            new ProtocolConfigBase("envSensorErr", 1, 30, 0),//环境温度传感器故障
            new ProtocolConfigBase("fridgeSensorErr", 1, 30, 1),//冷藏温度传感器故障
            new ProtocolConfigBase("freezeSensorErr", 1, 30, 3),//冷冻温度传感器故障
            new ProtocolConfigBase("changeSensorErr", 1, 30, 4),//变温温度传感器故障
            new ProtocolConfigBase("defrostSensorErr", 1, 29, 0),//化霜传感器故障
//            new ProtocolConfigBase("communicationErr", 1, 22, 10),//
            new ProtocolConfigBase("freezeDefrostErr", 1, 29, 6),//冷冻化霜故障
            new ProtocolConfigBase("fridgeDoorErr", 1, 27, 6),//冷藏门报警
    };
    public final ProtocolConfigBase[] configBCD401 = new ProtocolConfigBase[]{
            new ProtocolConfigBase("111c12002400081001010061800447000000000000", 0, 0, 0),
            new ProtocolConfigBase("refrigeratorShowTemperature", 1, 0, 0xff),
            new ProtocolConfigBase("freezerShowTemperature", 1, 1, 0xff),
            new ProtocolConfigBase("variableShowTemperature", 1, 2, 0xff),
            new ProtocolConfigBase("envShowTemperature", 1, 4, 0xff),
            new ProtocolConfigBase("refrigeratorTargetTemperature", 0, 6, 0xff),
            new ProtocolConfigBase("freezerTargetTemperature", 0, 7, 0xff),
            new ProtocolConfigBase("variableTargetTemperature", 0, 8, 0xff),

            new ProtocolConfigBase("intelligenceMode", 0, 21, 1),
            new ProtocolConfigBase("purifyMode", 0, 21, 2),
            new ProtocolConfigBase("quickFreezingMode", 2, 21, 3),
            new ProtocolConfigBase("quickRefrigerationMode", 2, 21, 4),

            new ProtocolConfigBase("refrigeratorDoorStatus", 1, 14, 8),

            new ProtocolConfigBase("envTemperatureSensorErr", 1, 22, 0),
            new ProtocolConfigBase("refrigeratorTemperatureErr", 1, 22, 1),
            new ProtocolConfigBase("freezerTemperatureErr", 1, 22, 3),
            new ProtocolConfigBase("variableTemperatureErr", 1, 22, 4),
            new ProtocolConfigBase("defrostingSensorErr", 1, 22, 8),
            new ProtocolConfigBase("communicationErr", 1, 22, 10),
            new ProtocolConfigBase("freezerDefrostingSensorErr", 1, 22, 14),
            new ProtocolConfigBase("refrigeratorDoorErr", 1, 20, 14),
    };
    public final ProtocolConfigBase[] configBCD256 = new ProtocolConfigBase[]{
            new ProtocolConfigBase("111c12002400081001030061800458410000000000", 0, 0, 0),
            new ProtocolConfigBase("refrigeratorShowTemperature", 1, 0, 0xff),
            new ProtocolConfigBase("freezerShowTemperature", 1, 1, 0xff),
            new ProtocolConfigBase("variableShowTemperature", 1, 2, 0xff),
            new ProtocolConfigBase("envShowTemperature", 1, 4, 0xff),
            new ProtocolConfigBase("refrigeratorTargetTemperature", 0, 6, 0xff),
            new ProtocolConfigBase("freezerTargetTemperature", 0, 7, 0xff),
            new ProtocolConfigBase("variableTargetTemperature", 0, 8, 0xff),

            new ProtocolConfigBase("intelligenceMode", 0, 12, 1),
            new ProtocolConfigBase("holidayMode", 0, 12, 2),
            new ProtocolConfigBase("quickFreezingMode", 2, 12, 3),
            new ProtocolConfigBase("quickRefrigerationMode", 2, 12, 4),

            new ProtocolConfigBase("refrigeratorDoorStatus", 1, 14, 8),

            new ProtocolConfigBase("envTemperatureSensorErr", 1, 22, 0),
            new ProtocolConfigBase("refrigeratorTemperatureErr", 1, 22, 1),
            new ProtocolConfigBase("freezerTemperatureErr", 1, 22, 3),
            new ProtocolConfigBase("variableTemperatureErr", 1, 22, 4),
            new ProtocolConfigBase("defrostingSensorErr", 1, 22, 8),
            new ProtocolConfigBase("communicationErr", 1, 22, 10),
            new ProtocolConfigBase("freezerDefrostingSensorErr", 1, 22, 14),
            new ProtocolConfigBase("refrigeratorDoorErr", 1, 20, 14),
    };
    public final ProtocolConfigBase[] configBCD630 = new ProtocolConfigBase[]{
            new ProtocolConfigBase("111C12002400081001010061800259430000000000", 0, 0, 0),
            new ProtocolConfigBase("refrigeratorShowTemperature", 1, 0, 0xff),
            new ProtocolConfigBase("freezerShowTemperature", 1, 1, 0xff),
            new ProtocolConfigBase("variableShowTemperature", 1, 2, 0xff),
            new ProtocolConfigBase("envShowTemperature", 1, 4, 0xff),
            new ProtocolConfigBase("refrigeratorTargetTemperature", 0, 6, 0xff),
            new ProtocolConfigBase("freezerTargetTemperature", 0, 7, 0xff),
            new ProtocolConfigBase("variableTargetTemperature", 0, 8, 0xff),

            new ProtocolConfigBase("intelligenceMode", 0, 12, 1),
            new ProtocolConfigBase("holidayMode", 0, 12, 2),
            new ProtocolConfigBase("quickFreezingMode", 2, 12, 3),
            new ProtocolConfigBase("quickRefrigerationMode", 2, 12, 4),

            new ProtocolConfigBase("refrigeratorDoorStatus", 1, 14, 8),

            new ProtocolConfigBase("envTemperatureSensorErr", 1, 22, 0),
            new ProtocolConfigBase("refrigeratorTemperatureErr", 1, 22, 1),
            new ProtocolConfigBase("freezerTemperatureErr", 1, 22, 3),
            new ProtocolConfigBase("variableTemperatureErr", 1, 22, 4),
            new ProtocolConfigBase("defrostingSensorErr", 1, 22, 8),
            new ProtocolConfigBase("communicationErr", 1, 22, 10),
            new ProtocolConfigBase("freezerDefrostingSensorErr", 1, 22, 14),
            new ProtocolConfigBase("refrigeratorDoorErr", 1, 20, 14),
    };

}

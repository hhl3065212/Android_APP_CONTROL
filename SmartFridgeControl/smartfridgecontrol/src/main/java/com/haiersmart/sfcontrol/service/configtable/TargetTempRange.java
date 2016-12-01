package com.haiersmart.sfcontrol.service.configtable;

/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2016/11/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class TargetTempRange {
    private int fridgeMinValue;//冷藏室温度最小值
    private int fridgeMaxValue;//冷藏室温度最大值
    private int fridgeDiffValue;//档位与温度对应差值 档位值-温度值=差值
    private byte fridgeMinGear;//冷藏最小档位
    private byte fridgeMaxGear;//冷藏最大档位

    private int freezeMinValue;//冷冻室温度最小值
    private int freezeMaxValue;//冷冻室温度最大值
    private int freezeDiffValue;//档位与温度对应差值 档位值-温度值=差值
    private byte freezeMinGear;//冷冻最小档位
    private byte freezeMaxGear;//冷冻最大档位

    private int changeMinValue;//变温室温度最小值
    private int changeMaxValue;//变温室温度最大值
    private int changeDiffValue;//档位与温度对应差值 档位值-温度值=差值
    private byte changeMinGear;//变温最小档位
    private byte changeMaxGear;//变温最大档位

    public TargetTempRange() {
        this.fridgeMinValue = 0;
        this.fridgeMaxValue = 0;
        this.fridgeDiffValue = 0;
        this.freezeMinValue = 0;
        this.freezeMaxValue = 0;
        this.freezeDiffValue = 0;
        this.changeMinValue = 0;
        this.changeMaxValue = 0;
        this.changeDiffValue = 0;
    }


    public void setFridgeRange(int fridgeMinValue, int fridgeMaxValue, int fridgeDiffValue){
        this.fridgeMinValue = fridgeMinValue;
        this.fridgeMaxValue = fridgeMaxValue;
        this.fridgeDiffValue = fridgeDiffValue;
        this.fridgeMinGear = (byte)(fridgeMinValue+fridgeDiffValue);
        this.fridgeMaxGear = (byte)(fridgeMaxValue+fridgeDiffValue);
    }
    public void setFreezeRange(int freezeMinValue, int freezeMaxValue, int freezeDiffValue){
        this.freezeMinValue = freezeMinValue;
        this.freezeMaxValue = freezeMaxValue;
        this.freezeDiffValue = freezeDiffValue;
        this.freezeMinGear = (byte)(freezeMinValue+freezeDiffValue);
        this.freezeMaxGear = (byte)(freezeMaxValue+freezeDiffValue);
    }
    public void setChangeRange(int changeMinValue, int changeMaxValue, int changeDiffValue){
        this.changeMinValue = changeMinValue;
        this.changeMaxValue = changeMaxValue;
        this.changeDiffValue = changeDiffValue;
        this.changeMinGear = (byte)(changeMinValue+changeDiffValue);
        this.changeMaxGear = (byte)(changeMaxValue+changeDiffValue);
    }

    public int getFridgeMinValue() {
        return fridgeMinValue;
    }

    public int getFridgeMaxValue() {
        return fridgeMaxValue;
    }

    public int getFridgeDiffValue() {
        return fridgeDiffValue;
    }

    public int getFreezeMinValue() {
        return freezeMinValue;
    }

    public int getFreezeMaxValue() {
        return freezeMaxValue;
    }

    public int getFreezeDiffValue() {
        return freezeDiffValue;
    }

    public int getChangeMinValue() {
        return changeMinValue;
    }

    public int getChangeMaxValue() {
        return changeMaxValue;
    }

    public int getChangeDiffValue() {
        return changeDiffValue;
    }

    public byte getFridgeMinGear() {
        return fridgeMinGear;
    }

    public byte getFridgeMaxGear() {
        return fridgeMaxGear;
    }

    public byte getFreezeMinGear() {
        return freezeMinGear;
    }

    public byte getFreezeMaxGear() {
        return freezeMaxGear;
    }

    public byte getChangeMinGear() {
        return changeMinGear;
    }

    public byte getChangeMaxGear() {
        return changeMaxGear;
    }
}

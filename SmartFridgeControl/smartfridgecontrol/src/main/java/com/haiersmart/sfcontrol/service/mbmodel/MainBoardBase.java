/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: 冰箱主控板模型，模型的配置，模式及档位同步，开关门及报警事件
 * Author:  Holy.Han
 * Date:  2016/11/28
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.service.mbmodel;

import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.FridgeControlDbMgr;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.service.configtable.ProtocolCommand;
import com.haiersmart.sfcontrol.service.configtable.ProtocolConfigBase;
import com.haiersmart.sfcontrol.service.configtable.TargetTempRange;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.PrintUtil;

import java.util.ArrayList;

import static com.haiersmart.sfcontrol.constant.EnumBaseName.changeTargetTemp;

/**
 * <p>function: </p>
 * <p>description:  冰箱主控板模型，模型的配置，模式及档位同步，开关门及报警事件。</p>
 * <p><b>使用子类对其进行创建。251冰箱：MainBoardFourSevenSix；476冰箱：MainBoardTwoFiveOne</b></p>
 * <p><b>模型创建后必须先进行初始化：init()</b></p>
 * history:  1. 2016/11/28
 * Author: Holy.Han
 * modification: create
 */
public abstract class MainBoardBase {
    protected final String TAG = "MainBoardBase";
    ArrayList<ProtocolConfigBase> mProtocolConfigStatus;//状态配置
    ArrayList<ProtocolConfigBase> mProtocolConfigDebug;//调试配置
    private ArrayList<MainBoardEntry> mainBoardControl;//控制类
    private ArrayList<MainBoardEntry> mainBoardStatus;//状态类
    private ArrayList<MainBoardEntry> mainBoardDebug;//调试类
    private TargetTempRange mTargetTempRange;//档位控制温度范围
    FridgeControlDbMgr mFridgeControlDbMgr;//数据库管理
    private ArrayList<FridgeControlEntry> dbFridgeControlEntry;//数据库对应查询的控制类
    ArrayList<FridgeControlEntry> dbFridgeControlCancel;//数据库中需要取消设置的类
    ArrayList<FridgeControlEntry> dbFridgeControlSet;//数据库中需要设置的类
    public boolean testDoor = false;//

    public MainBoardBase() {
    }

    /**
     * 选择配置表，子类定义
     */
    public abstract void initConfig();

    /**
     * 档位同步，子类定义,不同冰箱不同
     *
     * @return 下发命令的list
     */
    public abstract ArrayList<byte[]> packSyncLevel();

    /**
     * 无效信息的处理，子类定义
     *
     * @param frame 主控板返回状态码
     */
    public abstract void processInVainMessage(byte[] frame);

    /**
     * 冰箱门事件，包括开关门，开门报警
     */
    public abstract void handleDoorEvents();

    /**
     * 更新调试状态码到调试类 帧类型为0xff
     *
     * @param frame 主控板返回状态码
     */
    public void updateMainBoardDebugMessage(byte[] frame) {
        for (MainBoardEntry mainBoardEntry : mainBoardDebug) {
            if (mainBoardEntry.getByteShift() < 8) {
                int tmpint;
                tmpint = (frame[mainBoardEntry.getStartByte() - 1] >> mainBoardEntry.getByteShift()) & 0x01;
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 8) {
                int tmpint;
                tmpint = frame[mainBoardEntry.getStartByte() - 1] & 0xff;
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 16) {
                int tmpint;
                tmpint = ((frame[mainBoardEntry.getStartByte() - 1] & 0xff) << 8);
                tmpint += (frame[mainBoardEntry.getStartByte()] & 0xff);
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            }
        }
    }


    /**
     * 主控板信息类初始化
     */
    public void init() {
        mFridgeControlDbMgr = FridgeControlDbMgr.getInstance();
        initConfig();//选定配置表
        mainBoardControl = new ArrayList<>();
        mainBoardStatus = new ArrayList<>();
        mainBoardDebug = new ArrayList<>();
        initStatusClass();//生成状态类
        initDebugClass();//生成调试类
        creatFridgeControlDb();
    }

    /**
     * 主控板控制类、状态类初始化
     */
    private void initStatusClass() {
        mTargetTempRange = new TargetTempRange();
        for (ProtocolConfigBase protocolConfigBase : mProtocolConfigStatus) {
            int direction = protocolConfigBase.getDirection();
            if (direction == 0) {
                mainBoardControl.add(new MainBoardEntry(
                        protocolConfigBase.getName(), 0,
                        protocolConfigBase.getStartByte(),
                        protocolConfigBase.getByteShift(),
                        protocolConfigBase.getDiffValue()));
            } else if (direction == 1) {
                mainBoardStatus.add(new MainBoardEntry(
                        protocolConfigBase.getName(), 0,
                        protocolConfigBase.getStartByte(),
                        protocolConfigBase.getByteShift(),
                        protocolConfigBase.getDiffValue()));
            }
            initTargetTempRange(protocolConfigBase);
        }
        MyLogUtil.i(TAG, "Init board status class and control class success!");
    }

    /**
     * 主控板档位控制范围初始化
     *
     * @param protocolConfigBase 状态配置
     */
    private void initTargetTempRange(ProtocolConfigBase protocolConfigBase) {

        if (protocolConfigBase.getName().equals(EnumBaseName.fridgeTargetTemp.toString())) {
            mTargetTempRange.setFridgeRange(protocolConfigBase.getMinValue(),
                    protocolConfigBase.getMaxValue(),
                    protocolConfigBase.getDiffValue());
        }
        if (protocolConfigBase.getName().equals(EnumBaseName.freezeTargetTemp.toString())) {
            mTargetTempRange.setFreezeRange(protocolConfigBase.getMinValue(),
                    protocolConfigBase.getMaxValue(),
                    protocolConfigBase.getDiffValue());
        }
        if (protocolConfigBase.getName().equals(EnumBaseName.changeTargetTemp.toString())) {
            mTargetTempRange.setChangeRange(protocolConfigBase.getMinValue(),
                    protocolConfigBase.getMaxValue(),
                    protocolConfigBase.getDiffValue());
        }
    }

    /**
     * 主控板调试类初始化
     */
    private void initDebugClass() {
        for (ProtocolConfigBase protocolConfigBase : mProtocolConfigDebug) {
            int direction = protocolConfigBase.getDirection();
            if (direction == 1) {
                mainBoardDebug.add(new MainBoardEntry(
                        protocolConfigBase.getName(), 0,
                        protocolConfigBase.getStartByte(),
                        protocolConfigBase.getByteShift(),
                        protocolConfigBase.getDiffValue()));
            }
        }
        MyLogUtil.i(TAG, "Init board debug class success!");
    }

    /**
     * 更新主控板状态码到控制类、状态类 帧类型为0x02
     *
     * @param frame 主控板返回状态码
     */
    public void updataMainBoardParameters(byte[] frame) {
        for (MainBoardEntry mainBoardEntry : mainBoardControl) {
            if (mainBoardEntry.getByteShift() < 8) {
                int tmpint;
                tmpint = (frame[mainBoardEntry.getStartByte() - 1] >> mainBoardEntry.getByteShift()) & 0x01;
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 8) {
                int tmpint;
                tmpint = frame[mainBoardEntry.getStartByte() - 1] & 0xff;
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 16) {
                int tmpint;
                tmpint = ((frame[mainBoardEntry.getStartByte() - 1] & 0xff) << 8);
                tmpint += (frame[mainBoardEntry.getStartByte()] & 0xff);
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 9) {
                if ((frame[mainBoardEntry.getStartByte() - 1] & 0x00ff) == 0) {
                    mainBoardEntry.setValue(0);
                } else {
                    mainBoardEntry.setValue(1);
                }
            }
        }
        for (MainBoardEntry mainBoardEntry : mainBoardStatus) {
            if (mainBoardEntry.getByteShift() < 8) {
                int tmpint;
                tmpint = (frame[mainBoardEntry.getStartByte() - 1] >> mainBoardEntry.getByteShift()) & 0x01;
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 8) {
                int tmpint;
                tmpint = frame[mainBoardEntry.getStartByte() - 1] & 0xff;
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            } else if (mainBoardEntry.getByteShift() == 16) {
                int tmpint;
                tmpint = ((frame[mainBoardEntry.getStartByte() - 1] & 0xff) << 8);
                tmpint += (frame[mainBoardEntry.getStartByte()] & 0xff);
                tmpint = tmpint - mainBoardEntry.getDiffValue();
                mainBoardEntry.setValue(tmpint);
            }
        }
    }

    /**
     * 打包冷藏档位设置命令，根据状态配置打包不同
     *
     * @param value 冷藏档位温度
     * @return 冷藏档位命令帧
     */
    byte[] packFridgeTargetTemp(int value) {
        byte mValue;
        if (value > mTargetTempRange.getFridgeMaxValue()) {
            mValue = mTargetTempRange.getFridgeMaxGear();
        } else if (value < mTargetTempRange.getFridgeMinValue()) {
            mValue = mTargetTempRange.getFridgeMinGear();
        } else {
            mValue = (byte) (value + mTargetTempRange.getFridgeDiffValue());
        }
        return ProtocolCommand.PackCmdFrame(EnumBaseName.fridgeTargetTemp, mValue);
    }

    /**
     * 打包冷冻档位设置命令，根据状态配置打包不同
     *
     * @param value 冷冻档位温度
     * @return 冷冻档位命令帧
     */
    byte[] packFreezeTargetTemp(int value) {
        byte mValue;
        if (value > mTargetTempRange.getFreezeMaxValue()) {
            mValue = mTargetTempRange.getFreezeMaxGear();
        } else if (value < mTargetTempRange.getFreezeMinValue()) {
            mValue = mTargetTempRange.getFreezeMinGear();
        } else {
            mValue = (byte) (value + mTargetTempRange.getFreezeDiffValue());
        }
        return ProtocolCommand.PackCmdFrame(EnumBaseName.freezeTargetTemp, mValue);
    }

    /**
     * 打包变温档位设置命令，根据状态配置打包不同
     *
     * @param value 变温档位温度
     * @return 变温档位命令帧
     */
    byte[] packChangeTargetTemp(int value) {
        byte mValue;
        if (value > mTargetTempRange.getChangeMaxValue()) {
            mValue = mTargetTempRange.getChangeMaxGear();
        } else if (value < mTargetTempRange.getChangeMinValue()) {
            mValue = mTargetTempRange.getChangeMinGear();
        } else {
            mValue = (byte) (value + mTargetTempRange.getChangeDiffValue());
        }
        return ProtocolCommand.PackCmdFrame(EnumBaseName.changeTargetTemp, mValue);
    }

    public ArrayList<MainBoardEntry> getMainBoardControl() {
        return mainBoardControl;
    }

    public ArrayList<MainBoardEntry> getMainBoardStatus() {
        return mainBoardStatus;
    }

    public ArrayList<MainBoardEntry> getMainBoardDebug() {
        return mainBoardDebug;
    }

    /**
     * 打包智能设置命令
     *
     * @param b true:打开 false:关闭
     * @return 智能设置命令帧
     */
    protected byte[] packSmartMode(boolean b) {
        byte[] res;
        if (b) {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.smartMode, (byte) 1);
        } else {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.smartMode, (byte) 0);
        }
        return res;
    }

    /**
     * 打包假日设置命令
     *
     * @param b true:打开 false:关闭
     * @return 假日设置命令帧
     */
    protected byte[] packHolidayMode(boolean b) {
        byte[] res;
        if (b) {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.holidayMode, (byte) 1);
        } else {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.holidayMode, (byte) 0);
        }
        return res;
    }

    /**
     * 打包速冻设置命令
     *
     * @param b true:打开 false:关闭
     * @return 速冻设置命令帧
     */
    protected byte[] packQuickFreezeMode(boolean b) {
        byte[] res;
        if (b) {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.quickFreezeMode, (byte) 1);
        } else {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.quickFreezeMode, (byte) 0);
        }
        return res;
    }

    /**
     * 打包速冷设置命令
     *
     * @param b true:打开 false:关闭
     * @return 速冷设置命令帧
     */
    protected byte[] packQuickColdMode(boolean b) {
        byte[] res;
        if (b) {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.quickColdMode, (byte) 1);
        } else {
            res = ProtocolCommand.PackCmdFrame(EnumBaseName.quickColdMode, (byte) 0);
        }
        return res;
    }

    /**
     * 打包冷藏关闭命令
     *
     * @return 冷藏关闭命令帧
     */
    private byte[] packFridgeClose() {
        return ProtocolCommand.PackCmdFrame(EnumBaseName.fridgeTargetTemp, (byte) 0);
    }

    /**
     * 打包冷藏打开命令，并设置为原有档位
     *
     * @return 冷藏打开命令帧
     */
    private byte[] packFridgeOpen() {
        FridgeControlEntry mFridgeControlEntry = new FridgeControlEntry(EnumBaseName.fridgeTargetTemp.toString());
        mFridgeControlDbMgr.queryByName(mFridgeControlEntry);
        return ProtocolCommand.PackCmdFrame(EnumBaseName.fridgeTargetTemp, (byte) mFridgeControlEntry.value);
        //        return packFridgeTargetTemp(mFridgeControlEntry.value);
    }

    /**
     * 打包查询命令
     *
     * @return 查询命令帧
     */
    protected byte[] packQuerryCmd() {
        return ProtocolCommand.PackCmdFrame(EnumBaseName.getAllProperty);
    }

    /**
     * 按照名称打包模式命令，只适用于开启和关闭模式
     *
     * @param string 命令的名称
     * @param b      true:开启 false:关闭
     * @return
     */
    private byte[] packModeCmd(String string, boolean b) {
        byte[] tmp;
        if (b) {
            if (string.equals(EnumBaseName.fridgeSwitch.toString())) {
                tmp = packFridgeOpen();
            } else {
                tmp = ProtocolCommand.PackCmdFrame(EnumBaseName.valueOf(string), (byte) 1);
            }
        } else {
            if (string.equals(EnumBaseName.fridgeSwitch.toString())) {
                tmp = packFridgeClose();
            } else {
                tmp = ProtocolCommand.PackCmdFrame(EnumBaseName.valueOf(string), (byte) 0);
            }
        }
        return tmp;
    }

    /**
     * 获得档位温度范围
     *
     * @return TargetTempRange
     */
    public TargetTempRange getTargetTempRange() {
        return mTargetTempRange;
    }

    /**
     * 模式及档位同步，取出数据库中模式值，分为需要取消和需要设置。
     * 与主控板状态比较，先进行取消操作，再进行设置操作，最后同步档位
     *
     * @return
     */
    public ArrayList<byte[]> packSyncMode() {
        ArrayList<byte[]> tmpSendBytes = new ArrayList<>();
        getDbModeClass();//获得最新数据库模式状态，并分为取消和设置两个类
        //进行取消操作
        for (FridgeControlEntry fridgeControlEntry : dbFridgeControlCancel) {
            int mainBoardValue = getMainBoardControlByName(fridgeControlEntry.name);
            //与主控板状态不一致，则下发模式取消命令
            if (fridgeControlEntry.value != mainBoardValue) {
                byte[] tmp = packModeCmd(fridgeControlEntry.name, false);
                tmpSendBytes.add(tmp);
            }
        }
        //进行设置操作
        for (FridgeControlEntry fridgeControlEntry : dbFridgeControlSet) {
            int mainBoardValue = getMainBoardControlByName(fridgeControlEntry.name);
            //与主控板状态不一致，则下发模式设置命令
            if (fridgeControlEntry.value != mainBoardValue) {
                byte[] tmp = packModeCmd(fridgeControlEntry.name, true);
                tmpSendBytes.add(tmp);
            }
        }
        //模式同步以后进行档位同步
        ArrayList<byte[]> tmpBytes = packSyncLevel();
        for (byte[] bytes : tmpBytes) {
            tmpSendBytes.add(bytes);
        }
        for (byte[] bytes : tmpSendBytes) {
            MyLogUtil.i(TAG, "tmpSendBytes:" + PrintUtil.BytesToString(bytes, PrintUtil.HEX));
        }
        return tmpSendBytes;
    }

    /**
     * 获得数据库中冰箱控制类
     *
     * @return
     */
    private void getFridgeControlDb() {
        int size = dbFridgeControlEntry.size();
        for (int i = 0; i < size; i++) {
            FridgeControlEntry fridgeControlEntry = dbFridgeControlEntry.get(i);
            mFridgeControlDbMgr.queryByName(fridgeControlEntry);
            dbFridgeControlEntry.set(i, fridgeControlEntry);
        }
    }

    /**
     * 创建冰箱控制类，数据库的复制
     */
    private void creatFridgeControlDb() {
        dbFridgeControlEntry = new ArrayList<>();
        for (MainBoardEntry mainBoardEntry : mainBoardControl) {
            FridgeControlEntry mFridgeControlEntry = new FridgeControlEntry(mainBoardEntry.getName());
            mFridgeControlDbMgr.queryByName(mFridgeControlEntry);
            dbFridgeControlEntry.add(mFridgeControlEntry);
        }
        dbFridgeControlCancel = new ArrayList<>();
        dbFridgeControlSet = new ArrayList<>();
    }

    /**
     * 获得数据库中控制类，并按照value值进行分类，
     * 0：为需要取消的模式；1：为需要设置的模式
     */
    private void getDbModeClass() {
        dbFridgeControlSet.clear();
        dbFridgeControlCancel.clear();
        getFridgeControlDb();
        for (FridgeControlEntry fridgeControlEntry : dbFridgeControlEntry) {
            if (fridgeControlEntry.name.equals(EnumBaseName.fridgeTargetTemp.toString())) {
                continue;
            }
            if (fridgeControlEntry.name.equals(EnumBaseName.freezeTargetTemp.toString())) {
                continue;
            }
            if (fridgeControlEntry.name.equals(changeTargetTemp.toString())) {
                continue;
            }
            if (fridgeControlEntry.value == 0) {
                dbFridgeControlCancel.add(fridgeControlEntry);
            }
            if (fridgeControlEntry.value == 1) {
                dbFridgeControlSet.add(fridgeControlEntry);
            }
        }
    }

    /**
     * 通过名字查询主控板控制状态值
     *
     * @param string
     * @return
     */
    public int getMainBoardControlByName(String string) {
        int tmp = -1;
        for (MainBoardEntry mainBoardEntry : mainBoardControl) {
            if (mainBoardEntry.getName().equals(string)) {
                tmp = mainBoardEntry.getValue();
                break;
            }
        }
        return tmp;
    }

    /**
     * 通过名字查询主控板控制类对象
     * @param string
     * @return
     */
    private MainBoardEntry getMainBoardControlEntryByName(String string) {
        MainBoardEntry res = null;
        for (MainBoardEntry mainBoardEntry : mainBoardControl) {
            if (mainBoardEntry.getName().equals(string)) {
                res = mainBoardEntry;
                break;
            }
        }
        return res;
    }

    /**
     * 通过名字查询主控板状态值
     *
     * @param string
     * @return
     */
    public int getMainBoardStatusByName(String string) {
        int tmp = -1;
        for (MainBoardEntry mainBoardEntry : mainBoardStatus) {
            if (mainBoardEntry.getName().equals(string)) {
                tmp = mainBoardEntry.getValue();
                break;
            }
        }
        return tmp;
    }

    /**
     * 通过名字查询主控板调试状态值
     *
     * @param string
     * @return
     */
    public int getMainBoardDebugByName(String string) {
        int tmp = -1;
        for (MainBoardEntry mainBoardEntry : mainBoardDebug) {
            if (mainBoardEntry.getName().equals(string)) {
                tmp = mainBoardEntry.getValue();
                break;
            }
        }
        return tmp;
    }

    /**
     * 通过名字设置主控板状态值
     *
     * @param string
     * @param value
     */
    private void setMainBoardStatusByName(String string, int value) {
        for (MainBoardEntry mainBoardEntry : mainBoardStatus) {
            if (mainBoardEntry.getName().equals(string)) {
                mainBoardEntry.setValue(value);
            }
        }
    }

    /**
     * 设置通信超时状态
     *
     * @param b
     */
    public void setCommunicationOverTime(boolean b) {
        if (b) {
            setMainBoardStatusByName(EnumBaseName.communicationOverTime.name(), 1);
        } else {
            setMainBoardStatusByName(EnumBaseName.communicationOverTime.name(), 0);
        }
    }

    /**
     * 设置通信数据错误次数
     *
     * @param value
     */
    public void setCommunicationErr(int value) {
        setMainBoardStatusByName(EnumBaseName.communicationErr.name(), value);
    }

    /**
     * 设置冷藏门报警
     * @param b
     */
    void setFridgeDoorErr(boolean b) {
        if (b) {
            setMainBoardStatusByName(EnumBaseName.fridgeDoorErr.name(), 1);
        } else {
            setMainBoardStatusByName(EnumBaseName.fridgeDoorErr.name(), 0);
        }
    }

    /**
     * 设置冷冻门报警
     * @param b
     */
    void setFreezeDoorErr(boolean b) {
        if (b) {
            setMainBoardStatusByName(EnumBaseName.freezeDoorErr.name(), 1);
        } else {
            setMainBoardStatusByName(EnumBaseName.freezeDoorErr.name(), 0);
        }
    }
    /**
     * 设置变温门报警
     * @param b
     */
    void setChangeDoorErr(boolean b) {
        if (b) {
            setMainBoardStatusByName(EnumBaseName.changeDoorErr.name(), 1);
        } else {
            setMainBoardStatusByName(EnumBaseName.freezeDoorErr.name(), 0);
        }
    }

    /**
     * 为互联互通模块准备状态码
     * 状态码和数据库信息保持一致，从底板获得状态码后，重新把数据库信息反算回状态码，其他位置保持不变
     * @param get 底板返回的状态码
     * @return
     */
    public byte[] setDataBaseToBytes(byte[] get) {
        byte[] frame = get;
        boolean isFridgeSwitch = true;
        for (MainBoardEntry mainBoardEntry : mainBoardControl) {
            FridgeControlEntry fridgeControlEntry = new FridgeControlEntry(mainBoardEntry.getName());
            mFridgeControlDbMgr.queryByName(fridgeControlEntry);
            if (mainBoardEntry.getByteShift() < 8) {
                byte tmpbyte = frame[mainBoardEntry.getStartByte() - 1];
                if (fridgeControlEntry.value == 1) {
                    tmpbyte |= (0x01 << mainBoardEntry.getByteShift());
                } else {
                    tmpbyte &= ~(0x01 << mainBoardEntry.getByteShift());
                }
                frame[mainBoardEntry.getStartByte() - 1] = tmpbyte;
            } else if (mainBoardEntry.getByteShift() == 8) {
                if (fridgeControlEntry.name.equals(EnumBaseName.fridgeTargetTemp.toString()) && !isFridgeSwitch) {
                    frame[mainBoardEntry.getStartByte() - 1] = (byte) 0x00;
                } else {
                    int tmpint = fridgeControlEntry.value + mainBoardEntry.getDiffValue();
                    frame[mainBoardEntry.getStartByte() - 1] = (byte) tmpint;
                }
            } else if (mainBoardEntry.getByteShift() == 9) {
                if (fridgeControlEntry.name.equals(EnumBaseName.fridgeSwitch.toString())) {
                    if (fridgeControlEntry.value == 0) {
                        int startByte = getMainBoardControlEntryByName(EnumBaseName.fridgeTargetTemp.toString()).getStartByte();
                        frame[startByte - 1] = (byte) 0x00;
                        isFridgeSwitch = false;
                    }
                }
            }

        }
        return frame;
    }

}

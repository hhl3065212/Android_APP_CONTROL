/**
 * Copyright  2015,  Smart  Haier
 * All  rights  reserved.
 * Description:
 * Author:  shilin
 * Date:  15-12-3
 * FileName:  shilin.java
 * History:
 * 1.  Date:15-12-3 上午11:51
 * Author:shilin
 * Modification:
 */

package com.haiersmart.sfcontrol.service.powerctl;

import android.os.RemoteException;

import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.service.configtable.ProtocolCommand;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.PrintUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PowerSerialOpt {
    private final String TAG = "PowerSerialOpt";
    private SerialPort mSerialPort = null;//
    protected OutputStream mOutputStream;//
    private InputStream mInputStream;//
    private WriteReadThread mWriteReadThread;//
    public SerialData mSerialData;//

    protected boolean mReadFlag = false;//
    private int CommunicationErr = 0;//通信数据错误次数 从上电开始计算直到下次重启重新计数
//    private int CommunicationOverTime = 0;//
    private boolean vainTypeId = false;//
    private boolean ReadWriteParseThreadSwitch = true;//
    public List<byte[]> byteSendList;//
    private ProtocolCommand mProtocolCommand;//命令组

    private static PowerSerialOpt instance;

    public static synchronized PowerSerialOpt getInstance() throws IOException {
        if (instance == null) {
            synchronized (PowerSerialOpt.class) {
                if (instance == null)
                    instance = new PowerSerialOpt();
            }
        }
        return instance;
    }


    /**
     * 串口收发线程，从byteSendList中取出命令帧，发送给串口，
     * 等待响应。如果超时500ms重发2次。如果响应，验证数据帧正确，
     * 进行数据解析。如果发送3次命令无响应，记一次通讯错误。如果
     * 命令为查询id命令无响应，则按照默认型号配置。
     */
    private class WriteReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            int maxLength = 256;
            byte[] buffer = new byte[maxLength];
            int size = 0;
            int SendCnt = 0;

            try {
                waitSerialPortReady();//
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            while (!isInterrupted()) {
                if (ReadWriteParseThreadSwitch) {//读写线程开关
                    try {
                        if (!isSendListEmpty()) {
                            byte[] mSendByte = byteSendList.get(0);//
                            byteSendList.remove(0);
                            int outtimeCnt = 0;//超时计数
                            SendCnt = 0;//发送次数计数。每条命令最多3次
                            mReadFlag = false;//false:发送命令，true:等待回复
//                            每条命令超时重复发送3次
                            while (SendCnt < 3) {
                                if (mReadFlag == false) {//发送命令
                                    MyLogUtil.i(TAG,"mSendByte:"+ PrintUtil.BytesToString(mSendByte,16)+",SendCnt:"+SendCnt);
                                    if(mOutputStream != null) {
                                        mOutputStream.write(mSendByte);
                                    }
                                    SendCnt++;
                                    mReadFlag = true;
                                    try {
                                        sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {//等待回复
                                    if (mInputStream.available() < 1) {//没有接收到返回数据
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        outtimeCnt++;
                                        if (outtimeCnt >= 5) {
                                            mReadFlag = false;//超时500ms，重新发送命令
                                        }
                                    } else {
                                        size = mInputStream.read(buffer);
//                                        MyLogUtil.i(TAG,"mInputStream buffer size="+size);
                                        MyLogUtil.i(TAG,"buffer:"+ PrintUtil.BytesToString(buffer,16,size)+",size:"+size);
                                        if (size > 0) {
                                            mSerialData.copyByte(buffer, size);
                                            if (mSerialData.isDataCheck()) {//验证数据正确
                                                mSerialData.setCommunicationOverTime(false);//通信正常清除超时记录
                                                mSerialData.ProcData();
                                                mReadFlag = false;
                                                break;
                                            } else {
                                                CommunicationErr++;//通信数据错误计数
                                                mSerialData.setCommunicationErr(CommunicationErr);
                                                mReadFlag = false;
                                            }
                                        }
                                    }
                                }//end if (mReadFlag == false)
                            }//end while (SendCnt < 3)
                            //超时处理
                            if(SendCnt>=3){
                                mSerialData.setCommunicationOverTime(true);
                                //如果是查询id命令无响应特殊处理
                                if(mSendByte[3]==0x70){
                                    vainTypeId = true;
                                    mSerialData.createMainBoard();//
                                    MyLogUtil.i(TAG, "WriteReadThread BROADCAST_ACTION_QUERY_BACK");
                                    //TODO: write observer mode by self, consider application context maybe make OOM
                                    ControlApplication.getInstance().sendBroadcastToService(ConstantUtil.BROADCAST_ACTION_QUERY_BACK);
                                }
                            }//end if(SendCnt>=3)
                        }//end if(size > 0)
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }else {

                }
            }
        }
    }


    private void waitSerialPortReady() throws RemoteException {
        int countsReady = 0;
        boolean mWaiting = true;
        while (mWaiting) {
            try {
                mWriteReadThread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countsReady++;
            if(countsReady > 10 || mSerialPort.isReady()) {
                mWaiting = false;
            }
        }
        if(mSerialPort.isReady()) {
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        }else{
            mOutputStream = null;
            mInputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
        }
        mSerialData.setmOSVersion(getOSVersion());
        mSerialData.setmOSType(getOSType());
    }
    public PowerSerialOpt(){
        mProtocolCommand = new ProtocolCommand();
        mSerialPort = new SerialPort();

        byteSendList = java.util.Collections.synchronizedList(new LinkedList<byte[]>());

        mSerialData = SerialData.getInstance();

        mWriteReadThread = new WriteReadThread();
        mWriteReadThread.start();
    }

    public boolean isSendListEmpty(){
        boolean res = true;
        if(byteSendList.size() > 0) {
            res = false;
        }
            return res;
    }


    public void sendCmdArray(ArrayList<byte[]> cmdArray){
        for (byte[] cmd:cmdArray){
            byteSendList.add(cmd);
        }
    }
    public void sendCmd(byte[] cmd){
        byteSendList.add(cmd);
    }
    public void sendCmdByName(String string){
        byte[] cmd = mProtocolCommand.PackCmdFrame(EnumBaseName.valueOf(string));
        byteSendList.add(cmd);
    }
    public void sendCmdByName(String string,int value){
        byte[] cmd = mProtocolCommand.PackCmdFrame(EnumBaseName.valueOf(string),(byte)value);
        byteSendList.add(cmd);
    }
    public void sendCmdById(EnumBaseName mEnumBaseName){
        byte[] cmd = mProtocolCommand.PackCmdFrame(mEnumBaseName);
        byteSendList.add(cmd);
    }
    public void sendCmdById(EnumBaseName mEnumBaseName,int value){
        byte[] cmd = mProtocolCommand.PackCmdFrame(mEnumBaseName,(byte)value);
        byteSendList.add(cmd);
    }



    /**
     * Created by holyhan on 16-8-11.
     */


    public void PowerSerialOptClose() {
        ReadWriteParseThreadSwitch = false;
        mSerialPort.SerialPortClose();
    }

    public void PowerSerialOptReOpen() {
        mSerialPort.SerialPortReOpen();
        ReadWriteParseThreadSwitch = true;
    }

    public boolean isReadWriteParseThreadSwitch() {
        return ReadWriteParseThreadSwitch;
    }

    public void setReadWriteParseThreadSwitch(boolean readWriteParseThreadSwitch) {
        ReadWriteParseThreadSwitch = readWriteParseThreadSwitch;
    }

    public String getOSVersion(){
        return mSerialPort.getStrVersion();
    }
    public String getOSType(){
        return mSerialPort.getStrModel();
    }
}

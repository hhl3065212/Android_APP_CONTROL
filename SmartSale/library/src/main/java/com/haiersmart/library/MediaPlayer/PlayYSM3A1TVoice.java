/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description: Play the Voice playback module of the serial port connection
 * Author:  Holy.Han
 * Date:  2017/12/15
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.library.MediaPlayer;

import android.util.Log;

import com.haiersmart.library.SerialPort.SerialPort;
import com.haiersmart.library.Utils.ConvertData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>function: </p>
 * <p>description:  Play the Voice playback module of the serial port connection,
 * which is suitable for ys-m3a1t voice playback module</p>
 * <p>Use the static Playvoiceonce () method if you send it once and for a long interval.
 * If you repeatedly send, create a new object using the Playvoice () method.</p>
 * history:  1. 2017/12/15
 * Author: Holy.Han
 * modification:
 */
public class PlayYSM3A1TVoice {
    protected static final String TAG = "PlayYSM3A1TVoice";

    private static final String portName = "/dev/ttyS1";
    private static final int portBaund = 9600;

    private SerialPort serialPort;
    private List<byte[]> sendList = Collections.synchronizedList(new LinkedList<byte[]>());
    private boolean switchRun = true;

    public PlayYSM3A1TVoice() {
        serialPort = new SerialPort(portName, portBaund);
        WriteReadTrhead.start();
    }

    public void release(){
        switchRun = false;
        WriteReadTrhead.interrupt();
        serialPort.SerialPortClose();
    }

    private void sendCmd(byte cmd) {
        byte[] send = CmdYSM3A1T.packCmd(cmd);
        sendList.add(send);
    }

    private void sendCmd(byte cmd, byte data) {
        byte[] send = CmdYSM3A1T.packCmd(cmd, data);
        sendList.add(send);
    }

    private void sendCmd(byte cmd, byte data1, byte data2) {
        byte[] send = CmdYSM3A1T.packCmd(cmd, data1, data2);
        sendList.add(send);
    }

    public void playVoice(int voice) {
        sendCmd(CmdYSM3A1T.selectVoice, (byte) 0x00, (byte) voice);
    }

    public static void playVoiceOnce(int voice) {
        byte[] tmpReceive = new byte[1024];
        int lenReceive = 0;
        int countsSend = 0;
        boolean isStateRead = false;
        SerialPort serial = new SerialPort(portName,portBaund);

        byte[] tmpSend = CmdYSM3A1T.packCmd(CmdYSM3A1T.selectVoice, (byte) 0x00, (byte) voice);
        while (countsSend < 3) {
            if (!isStateRead) {
                Log.i(TAG, "Voice send "+countsSend+":" + ConvertData.bytesToString(tmpSend, 16));
                serial.write(tmpSend);
                countsSend++;
                isStateRead = true;
            } else {
                lenReceive = serial.read(tmpReceive, 500);
                if (lenReceive < 0) {
                    isStateRead = false;
                } else {
                    Log.i(TAG, "Voice receive:" + ConvertData.bytesToString(tmpReceive, 16,lenReceive));
                    if (new String(tmpReceive).contains("OK")) {
                        break;
                    }
                }
            }
        }// while (countsSend < 3)
        serial.SerialPortClose();
    }

    private Thread WriteReadTrhead = new Thread() {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (serialPort == null || !serialPort.isReady()) {
                    continue;
                }
                if (switchRun) {
                    if (!isEmptySendList()) {
                        byte[] tmpSend = sendList.get(0);
                        byte[] tmpReceive = new byte[1024];
                        int lenReceive = 0;
                        int countsSend = 0;
                        boolean isStateRead = false;
                        sendList.remove(0);
                        while (countsSend < 3) {
                            if (!isStateRead) {
                                Log.i(TAG, "Voice send "+countsSend+":" + ConvertData.bytesToString(tmpSend, 16));
                                serialPort.write(tmpSend);
                                countsSend++;
                                isStateRead = true;
                            } else {
                                lenReceive = serialPort.read(tmpReceive, 500);
                                if (lenReceive < 0) {
                                    isStateRead = false;
                                } else {
                                    Log.i(TAG, "Voice receive:" + ConvertData.bytesToString(tmpReceive, 16,lenReceive));
                                    if (processData(tmpSend, tmpReceive)) {
                                        break;
                                    }
                                }
                            }
                        }// while (countsSend < 3)
                    }// if(!isEmptySendList())
                }// if(switchRun)
            }// while (!isInterrupted())
        }
    };

    public boolean isSwitchRun() {
        return switchRun;
    }

    public void setSwitchRun(boolean switchRun) {
        this.switchRun = switchRun;
    }

    private boolean isEmptySendList() {
        boolean res = true;
        if (sendList.size() > 0) {
            res = false;
        }
        return res;
    }

    private boolean processData(byte[] send, byte[] receive) {
        if (new String(receive).contains("OK")) {
            return true;
        }
        return false;
    }
}

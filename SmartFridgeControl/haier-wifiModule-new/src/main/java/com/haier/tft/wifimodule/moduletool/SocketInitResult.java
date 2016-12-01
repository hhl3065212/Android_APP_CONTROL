package com.haier.tft.wifimodule.moduletool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2015/11/16.
 */
public interface SocketInitResult {

    public void getSuccessResult(Socket mSocket, DataInputStream in, DataOutputStream out);


    public void getFailed(String e);
}
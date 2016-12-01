package com.haier.tft.wifimodule.moduletool;

import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2015/11/16.
 */
public class SocketSendRunable implements Runnable{

    private byte[] mData;
    private  DataOutputStream out;
    private SendCmdResult mSendCmdResult;

    public SocketSendRunable(byte[] data,DataOutputStream outputStream,SendCmdResult sendCmdResult){
        this.mData=data;
        this.out=outputStream;
        this.mSendCmdResult=sendCmdResult;
    }


@Override
    public void run(){

      if(mData!=null){

          Log.i("thread","send cmd thread name is "+Thread.currentThread().getName());
          sendMessage(mData);

      }else{
          Log.e("sdk", "send cmd data is null");
      }


    }


    /**
     * 发送消息到服务端
     *
     */
    public synchronized boolean sendMessage(byte[] msgByte)
    {
        try
        {
            if(out!=null)
            {
                // 发送给服务器
                out.write(msgByte);
                out.flush();
                mSendCmdResult.success();
                return true;
            }else{
                mSendCmdResult.failed("send cmd failed:outInputStream is null so send cmd failed");
                return false;
            }

        }
        catch (Exception e)
        {
            mSendCmdResult.failed("send cmd failed: Exception = "+e.toString());
            e.printStackTrace();
            return false;
        }
    }
}

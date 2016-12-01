package com.haier.tft.wifimodule.moduletool;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2015/11/16.
 */
public class SSLSocketInitRunable implements Runnable{

    private Socket socket;
    private SSLContext mSSLContext;
    private  String url;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;
    private SocketInitResult mSocketInitResult;
    @Override
    public void run() {

     start();

    }





public SSLSocketInitRunable(String url,int port ,SocketInitResult socketInitResult){
    this.url=url;
    this.port =port;
    mSocketInitResult=socketInitResult;
}



    /**
     * 开启TCP客户端
     *
     */
    public  boolean initValues()
    {
        try
        {
            if(socket!=null)
            {

//				StaticValueAndConnectUrl.ThreadOpenFlag=true;

                if(null==in)
                {

                    //in=new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    Log.i("sdk", "init input");

                    if(socket==null){
                        Log.i("sdk", "init input but socket ==null");
                    }
                    in = new DataInputStream(socket.getInputStream());
                    Log.i("sdk", "init input over");
                }
                if(null==out)
                {
                    out = new DataOutputStream(socket.getOutputStream());
                    Log.i("sdk", "init output");
                }
                Log.i("socket","setIsSocketLive true");

                mSocketInitResult.getSuccessResult(socket, in, out);
                return true;
            }

            else
            {
//                mControlPrefence.setIsSocketLive(false);

//                mSocketInitResult.
                mSocketInitResult.getFailed("sdk is null,so open fail");
                Log.i("sdk", "socket ==null ,so open fail");
                return false;
            }

        } catch (IOException ex) {
            mSocketInitResult.getFailed("open fail IOException");
            Log.i("sdk", " open fail IOException");
            close();
            ex.printStackTrace();
            return false;
        }
    }
    public boolean start(){
        {

            if(socket!=null){
                return true;
            }

            try {
                mSSLContext = SSLContext.getInstance("SSL");
                X509TrustManager x509m = new X509TrustManager() {

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // TODO Auto-generated method stub
                        Log.i("sdk", "Class SSLSocketInitRunable: checkServerTrusted CertificateException");
//                        mSocketInitResult.getFailed("Class SSLSocketInitThread: checkServerTrusted CertificateException ");

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // TODO Auto-generated method stub
//                        mSocketInitResult.getFailed("Class SSLSocketInitThread: checkClientTrusted CertificateException ");

                        Log.i("sdk","Class SSLSocketInitThread: checkClientTrusted CertificateException");
                    }
                };

                mSSLContext.init(null, new TrustManager[]{x509m}, new java.security.SecureRandom());
                SocketFactory factory = mSSLContext.getSocketFactory();
                socket = (SSLSocket) factory.createSocket(url, port);
                socket.setSoTimeout(StaticValueAndConnectUrl.READ_TIME_OUT);
                initValues();
//			socket = (SSLSocket) factory.createSocket("103.8.220.166", 56808);
//			103.8.220.166
//			socket.startHandshake();

            } catch (Exception ex) {

                Log.e("sdk", "SSLsocketClient Exception");
                mSocketInitResult.getFailed("start fail IOException");
                Thread.currentThread().setName("CommandDataReceiver");
                close();
                ex.printStackTrace();
                return false;
            }

            return true;


        }

    }

    public void close(){
        if(socket==null)
        {
            return;
        }
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
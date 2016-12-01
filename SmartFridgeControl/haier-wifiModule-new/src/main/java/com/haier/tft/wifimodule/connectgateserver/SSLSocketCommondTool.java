package com.haier.tft.wifimodule.connectgateserver;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by Administrator on 2015/9/11.
 */
public class SSLSocketCommondTool{

	private SSLContext mSSLContext;
	private SSLSocket socket;
	private byte[] dataByte ;

	/**因为安卓无法识别JKS，不用Key
	 *
	 *
	 */
	@SuppressLint("TrulyRandom")
	public SSLSocketCommondTool(String url,int port ) {
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

				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub

				}
			};

			mSSLContext.init(null, new TrustManager[]{x509m}, new java.security.SecureRandom());
			SocketFactory factory = mSSLContext.getSocketFactory();
			socket = (SSLSocket) factory.createSocket(url, port);

//			socket = (SSLSocket) factory.createSocket("103.8.220.166", 56808);
//			103.8.220.166
//			socket.startHandshake();



		} catch (Exception ex) {

			Log.e("sdk", "SSLSocketCommondTool Exception");
			ex.printStackTrace();
		}

	}
	/**
	 *
	 * @param num
	 * 接收的byte字节数
	 * @param sendData
	 * 要发送的字节流
	 * @return
	 */
	public byte[] getRequest(int num,byte[] sendData) {
		try {
			if(socket==null){
				Log.e("sdk", "socket ==null return null");
				return null;
			}

			Log.i("connect","before DataOutput ");
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			Log.i("connect","before write ");
			out.write(sendData);
			Log.i("connect","before flush");
			out.flush();
			Log.i("connect","over flush");

			DataInputStream in = new DataInputStream(socket.getInputStream());
			Log.i("connect","inputstream  ");
			dataByte = new byte[num];

			int len = 0;
			Log.i("connect","before read ");
			len=in.read(dataByte);
			Log.i("connect","after read ");
//			String test_string ="";
//		
//			while (-1 != (len = in.read(dataByte))) {
//				System.out.println("=======len========" + len);
//				test_string = new String(dataByte, "ASCII");
//
//			}
//			

			Log.i("connect","get Data lengh =  "+len);



		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("sdk","request exception");
			dataByte=null;
			e.printStackTrace();
		} finally {
			try {

				if(socket!=null){
					Log.i("sdk","socket !=null and close it is for get the url and port");

					socket.close();}
				else
					Log.i("sdk","socket ==null");


			} catch (IOException e) {
				dataByte=null;
			}
		}
		return dataByte;

	}

}

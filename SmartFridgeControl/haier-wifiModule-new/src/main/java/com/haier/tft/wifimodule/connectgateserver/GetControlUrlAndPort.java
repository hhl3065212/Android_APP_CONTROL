package com.haier.tft.wifimodule.connectgateserver;


import java.io.UnsupportedEncodingException;

import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.haier.tft.wifimodule.alldata.DataToBytesForPowerOn;
import com.haier.tft.wifimodule.moduletool.BytesToInt;
import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;
import com.haier.tft.wifimodule.resolvingXml.ResolingXGetUrlAndPort;

public class GetControlUrlAndPort extends SSLSocketCommondTool {

	private byte[] SendData;
	private byte[] GetData;
	private GetUrlAndPortResult getUrlAndPortResult;
	public GetControlUrlAndPort(String url, int port,GetUrlAndPortResult getUrlAndPortResult ) {
		super(url, port);
		this.getUrlAndPortResult=getUrlAndPortResult;
		// TODO Auto-generated constructor stub
	}

	/**
	 * 信息处理参考4.6.2
	 * @param num
	 * 要发送的字节流
	 * @param devicetypid
	 * 设备识别码
	 * @param mac
	 * mac地址
	 * @return
	 */
	public boolean doRequest(int num, String devicetypid,String mac) throws UnsupportedEncodingException{
		DataToBytesForPowerOn myData = new DataToBytesForPowerOn();
		//111c120024000810010300618003474800000000000061800350000000000000
		//34E6ADB686EA
		SendData=myData.getSendVersionToGenServer165Byte(devicetypid,mac);

		GetData=this.getRequest(220, SendData);

		if(GetData==null){
			Log.i("sdk", "GetData ==null");
			getUrlAndPortResult.failed("get url and port error because GetData =null");
			return false;
		}

		BytesToInt mytran = new BytesToInt();
		Log.i("report", "report data for power ="+mytran.bytesToHexString(SendData));

		BytesToInt mytran2 = new BytesToInt();
		Log.i("report", "get data for power from server="+mytran2.bytesToHexString(GetData));


		ResolingXGetUrlAndPort mResolv = new ResolingXGetUrlAndPort(GetData);
		String controlurl = mResolv.getUrl();

		int controlport=mResolv.getPort();

		Editor edit=StaticValueAndConnectUrl.mySharedPreferences.edit();
		edit.putString(StaticValueAndConnectUrl.SharePreferenceName.controlUrl, controlurl);
		edit.putInt(StaticValueAndConnectUrl.SharePreferenceName.controPort, controlport);
		edit.commit();
		Log.i("GetControlUrlAndPort",  "get and save  url and port success url = " +controlurl +":"+controlport);
		Log.i("sdk", "get and save  url and port success url = " +controlurl +":"+controlport);

	    if(controlurl.length()>5&&controlport>0){
			getUrlAndPortResult.success(controlurl,controlport);
			return true;
		}else{
			getUrlAndPortResult.failed("get url and port error");
			return false;
		}


	}




}

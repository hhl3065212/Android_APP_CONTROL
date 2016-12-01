package com.haier.tft.wifimodule.wifimodule;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import com.haier.tft.wifimodule.DataForEplus.DataToBytesForChangeToEPlusFactory;
import com.haier.tft.wifimodule.alldata.DataToByteForSendToServer;

import com.haier.tft.wifimodule.moduletool.BytesToInt;
import com.haier.tft.wifimodule.moduletool.ControlPrefence;

import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;
import com.haier.tft.wifimodule.moduletool.StringToBytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.StringConv;

public class CommandDataReceiver extends BroadcastReceiver {
	private String TAG= this.getClass().getSimpleName();
	private byte[] sendData;
	//美食模式
	private String goodGoodModelS;
	//美食模式值
	private int goodFoodInt;
	//UV杀菌模式
	private String UVModelS;
	//杀菌模式值
	private int UVModelValue = 0;
	//杀菌模式存储数组
	byte[] UVModel =new byte[1];
	
	private String typeid="0";
	private BytesToInt myda = new BytesToInt();
	private StringToBytes mStringToBytes = new StringToBytes();
	private  ControlPrefence mControlPrefence ;
	private ConnectService connectService;
	private DataToBytesForChangeToEPlusFactory dataToBytesForChangeToEPlusFactory;
	private List<byte[]> SNByteList = new ArrayList<byte[]>();



	private HashMap<String,String> CommandDataHashMap =new HashMap<String,String>();
	public CommandDataReceiver() {
		Log.i(TAG, "CommandDataReceiver() ");
	}

	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		String action =intent.getAction();
		if(mControlPrefence==null){
			mControlPrefence =ControlPrefence.getInstance();
		}
		if(connectService==null){
			connectService=ConnectService.getInstance();
		}

		if(action.equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")){


//    		Log.i("", msg)
			Log.i("boot", "yes it is started");
    /*		Intent intent2 = context.getPackageManager().getLaunchIntentForPackage("com.haier.tft.wifimodule.wifimodule");
    		
    		 context.startActivity(intent2 ); */
			/*
			Intent mIntent = new Intent();
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mIntent.setClass(context, MainActivity.class);
			context.startActivity(mIntent);*/
		}


		if(action.equals("com.haier.tft.control.broadcast")){

			/**
			 * 接收到从协议解析模块发送的数据，然后转发给服务器
			 */
             Log.i("sdk","get data from pcb");
			sendData = intent.getByteArrayExtra("getBytes");
			HashMap datamap = (HashMap)intent.getSerializableExtra("getState");

//		/**
			/**
			 * 处理typeid
			 */
				if(!handleTypeid(intent)){
					Log.i("sdk","typeid havenot got so send cmd failed");
					return;
				}


			/**
			 * 生成上报的数据
			 */
			if (dataToBytesForChangeToEPlusFactory == null) {
				dataToBytesForChangeToEPlusFactory = DataToBytesForChangeToEPlusFactory.getInstance(StaticValueAndConnectUrl.devtype);
				if (dataToBytesForChangeToEPlusFactory == null) {
					Log.i("sdk", "creat the dataToBytesForChangeToEPlusFactory failed, so send cmd failed!");
					return;
				}
			}

			/**
			 * 处理美食模式
			 */
			handleGoodFoodModel(datamap);
			//处理杀菌模式
			handlerUVModel(datamap);

			if(mControlPrefence.getIsSocketLive() == false) {
				Log.i("sdk", "get broadcast from pcb but socket flag =false so return null");

				return;
			}

			if(mControlPrefence.getIsSocketLive()&&sendData!=null&&sendData.length>19){
				Log.i("sdk", "getdata from pcb and send to the service");
				byte[] ePlusData = dataToBytesForChangeToEPlusFactory.getBytesForReportState(sendData, 
						mControlPrefence.getGoodFoodModel(),UVModel, mControlPrefence.getIsAnswer());
				Log.i("answer", "send to server session is "+myda.bytesToHexString(StaticValueAndConnectUrl.Session));
				DataToByteForSendToServer sendtoServer = new DataToByteForSendToServer();
				SNByteList=	mControlPrefence.getMessageSNList();

						if(SNByteList==null||SNByteList.size()==0){
							mControlPrefence.setSN(null);
							byte[] mydata=	sendtoServer.getBytesToSendToServer(StaticValueAndConnectUrl.Session, StaticValueAndConnectUrl.MAC, mControlPrefence.getSN(), ePlusData);
//
							connectService.sendCmd(mydata);
//
//                            mssl.sendMessage(mydata);
							Log.i("answer", "to the server sn is  0");
							Log.i("data", "To the server "+myda.bytesToHexString(mydata));
						}else{
							for(int i=0;i<SNByteList.size();i++){
								byte[] mydata=	sendtoServer.getBytesToSendToServer(StaticValueAndConnectUrl.Session, StaticValueAndConnectUrl.MAC,SNByteList.get(i), ePlusData);
								connectService.sendCmd(mydata);
								Log.i("answer", "to the server sn is " + myda.bytesToHexString(SNByteList.get(i)));
								Log.i("data", "To the server "+myda.bytesToHexString(mydata));
								try {
									Thread.currentThread().sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}


						}

				mControlPrefence.setIsAnswer(false);//用于确定是否应答

				Log.i("data", "from pcb data "+myda.bytesToHexString(sendData));

				Log.i("data", "translate for e++ " + myda.bytesToHexString(ePlusData));

			}


		}



		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态

				if (isConnected) {

					Log.i("sdk-net", "isConnected");
					Log.i("sdk-net", "IsSocketLive= "+mControlPrefence.getIsSocketLive()) ;
//    	                	 if(!StaticValueAndConnectUrl.IsSocketLive){
//    	                		 Log.i("sdk-net", "start thread to connect ") ;
//    	                	
//    	                		 new Thread(){
//    	                			 public void run(){
//    	                				 boolean flag=false;
//    	                				 StartConnectService my =new StartConnectService();
//    	                				 flag= my.start();
//    	                				 int i=0;
//    	                				 
//    	                				 while(flag==false&&i<3){
//    	                					 
//    	                					 flag=my.start();
//    	                					 i++;
//    	                					 
//    	                				 }
//    	                			 }
//    	                		 }.start();
//    	                		
//    	                		 
//    	                	 }


				} else {


					Log.i("sdk-net", "not Connected");
					if(mControlPrefence.getIsSocketLive()){
//						new Thread(){
//							public void run(){
//
//								Thread.currentThread().setName("CommandDataReceiver");
//								String url =StaticValueAndConnectUrl.mySharedPreferences.getString(StaticValueAndConnectUrl.SharePreferenceName.controlUrl, "");
//								int port =StaticValueAndConnectUrl.mySharedPreferences.getInt(StaticValueAndConnectUrl.SharePreferenceName.controPort, 0);
//								SSLsocketClient mssl = SSLsocketClient.getInstance(url, port);
//								mssl.close();
//							}
//						}.start();


					}
				}



			}
		}


		//   throw new UnsupportedOperationException("Not yet implemented");
	}

	public  boolean handleTypeid(Intent intent){
			typeid =intent.getStringExtra("typeid");
			Log.i("typeid","from pcb typeid = "+typeid);
			Log.i("sdk","from pcb typeid = "+typeid);
		if(typeid==null){
			typeid="0";
		}

		if(typeid.equalsIgnoreCase("0")){//||typeid.length()!=64||typeid.length()!=42
			Log.i("sdk","from pcb typeid in hashmap = "+typeid+" so wont send cmd");
			return false;
		}else {
			if(typeid.length()==42){
				typeid =typeid+"0000000000000000000000";
			}

				Log.i("sdk", "save the typeid");
			    if(mControlPrefence.getTypeid().equalsIgnoreCase(typeid)){
					return true;
				}else{
					//需要进行重新生成，因此进行判断
					mControlPrefence.setTypeid(typeid);
					StaticValueAndConnectUrl.devtype = typeid;
					dataToBytesForChangeToEPlusFactory=null;
					return true;
				}



		}
	}
	/***
	 * 处理美食模式
	 * @date 2016-2-27 下午2:25:19
	 * @description TODO
	 * @param datamap
	 */
	public void handleGoodFoodModel(HashMap datamap){
		goodGoodModelS= (String)datamap.get("getGoodFoodModel");

		byte[] GoodFoodModel =new byte[2];
		if(goodGoodModelS==null){
			//FEFE为关闭状态

			GoodFoodModel[0]= (byte) 0xFE;
			GoodFoodModel[1]= (byte) 0xFE;
			mControlPrefence.setGoodFoodModel(GoodFoodModel);


			Log.i("data","goodGoodModel is null ");
		}else{
			goodFoodInt=65278;//默认为关闭状态，防止转换失败
			goodFoodInt= StringConv.parseInt(goodGoodModelS);

			GoodFoodModel[0]= mStringToBytes.getIntTo2Bytes(goodFoodInt)[0];
			GoodFoodModel[1]=mStringToBytes.getIntTo2Bytes(goodFoodInt)[1];
			mControlPrefence.setGoodFoodModel(GoodFoodModel);
			Log.i("data","good food model is = "+myda.bytesToHexString(mControlPrefence.getGoodFoodModel()));
		}
	}
	
	/**
	 * 处理UV杀菌模式
	 * @date 2016-2-27 下午2:25:55
	 * @description TODO key：getUVModel
	 * @param datamap
	 */
	private void handlerUVModel(HashMap datamap){
		UVModelS = (String)datamap.get("getUVModel");
		
		if(UVModelS==null){
			//0为关闭状态
			UVModel[0]= (byte) 0x00;

			Log.i("data","UVModel is null ");
		}else{
			UVModelValue = 0;//默认为关闭状态，防止转换失败
			UVModelValue= StringConv.parseInt(UVModelS);
			UVModel[0]= mStringToBytes.getIntTo2Bytes(UVModelValue)[1];
			Log.i("data","good UV model is = "+myda.bytesToHexString(UVModel));
		}
	}

}
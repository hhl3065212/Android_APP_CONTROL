package com.haier.tft.wifimodule.moduletool;

import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/9/10.
 */
public class StaticValueAndConnectUrl {



//	public static boolean InternetGood=false;

//	public static boolean IsOnline =false;
//	public static  boolean IsSocketLive=false;
	public static class SharePreferenceName{
		public static String controlUrl ="controlUrl";
		public static String controPort ="controlPort";
	}

//	public static byte[] CmdFromServer;
	public static byte[] Session;

	/**
	 * 用于确定是否应答，应答的话是02，不是应答的话是06，但是除了查询，如果查询的话属于06
	 */
//	public static boolean isAnswer=false;
	//	public static boolean ThreadOpenFlag=true;
	public static SharedPreferences mySharedPreferences;

	public static android.os.Handler Handler;

	public static int GetDATAFROMSERVERFORHANDLE =0x01;

	public static int SENDDATATOSERVERFORHANDLE =0x02;

//    public static  String ControlServerUrl ="103.8.220.166";

//    public static  int ControlServerPort =56808;

	public static String MAC="";//D022BE7CC301

	public static int READ_TIME_OUT =54000;
	public static int HEART_BEAT_TIME=45000;
	public static int CHECK_SOCKET_TIME=57000;
	public static int HEART_BEAT_TIME_FOR_NOT=60000;// 设置为间隔60秒，也就是当有一个心跳无法获取时候就基本可以设置为不在线，然后重新获取信息。

	public static String GateWayUrl ="gw.haier.net";//"gw.haier.net";//"103.8.220.166";;//绔彛涓猴細56808
//	public static String devtype ="111c120024000810010200618001184200000000000000000000000000000000";
    public static String devtype ="0";
//   111c120024000810010200618001184200000000000000000000000000000000目前的
//   111c120024000810010300618003474800000000000061800350000000000000 随机的一个

}

package com.haier.tft.wifimodule.moduletool;

import android.util.Log;

public class BytesToInt {

	/**
	 * 将byte转为int
	 * @param begin
	 * @param end
	 * @param buff
	 * @return
	 */

	public int getInt(int begin,int end,byte[] buff){
		if(buff==null){
			return 0;
		}
		Log.i("sdk", "buff.length = "+buff.length);
		byte[] myInt = new byte[4];
		int j =0;
		for(int i=begin;i<end;i++){
			myInt[j]=buff[i];
			j++;

		}

		int value =(int)((myInt[3]&0xFF)|(myInt[2]&0xFF)<<8|(myInt[1]&0xFF)<<16|(myInt[0]&0xFF)<<24);
		return value;
	}


	public int getPort(byte[] buff){

		byte[] port = new byte[4];

		int j =0;

		for(int i=148;i<152;i++){
			port[j]=buff[i];
			j++;

		}


		int value =(int)((port[3]&0xFF)|(port[2]&0xFF)<<8|(port[1]&0xFF)<<16|(port[0]&0xFF)<<24);

		return value;
	}


	private static char[] HexCode = {'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public String bytesToHexString(byte[] src){
		if(src==null){
			return"0";
		}
		StringBuilder stringBuilder = new StringBuilder();

		for(int i = 0; i < src.length; i++){
			byte b =src[i];

			stringBuilder.append(HexCode[(b >>> 4) & 0x0f]);
			stringBuilder.append(HexCode[b & 0x0f]);
		}
        
/*          if (src == null || src.length <= 0) {       
              return null;       
          }       
          for (int i = 0; i < src.length; i++) {       
              int v = src[i] & 0xFF;       
              String hv = Integer.toHexString(v);       
              if (hv.length() < 2) {       
                  stringBuilder.append(0);       
              }       
              stringBuilder.append(hv);       
          }    */
		return stringBuilder.toString();
	}





///**
//   * byte2HexString
//   *
//   * @param b
//   * @return
//   */
//  public static String byte2HexString(byte b) {
//      StringBuffer buffer = new StringBuffer();
//      buffer.append(HexCode[(b >>> 4) & 0x0f]);
//      buffer.append(HexCode[b & 0x0f]);
//      return buffer.toString();
//  }
}

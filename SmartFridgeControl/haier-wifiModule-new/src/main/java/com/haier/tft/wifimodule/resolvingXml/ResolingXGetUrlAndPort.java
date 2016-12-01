package com.haier.tft.wifimodule.resolvingXml;

import com.haier.tft.wifimodule.moduletool.BytesToInt;

import common.StringConv;

public class ResolingXGetUrlAndPort {

	private byte[] data;

	public ResolingXGetUrlAndPort(byte[] data){

		this.data=data;

	}

	public String getUrl(){

		String controlurl = new String(data,84, 64);

		int i;

		for(i=controlurl.length()-1;i>1;i=i-1 ){
//			System.out.println("begin" );
			String co =controlurl.substring(i, i+1);

			if(isInteger(co)){
//				System.out.println("yes it is num" +"co ="+co +"   i= "+i);

				break;
			}
			else{
//				System.out.println("no it is not num" +"co ="+co );
			}

		}

		String url =  controlurl.substring(0, i+1);
		return url;

	}

	public int getPort(){
		int controlport=0;
		BytesToInt mmport = new BytesToInt();
		controlport=mmport.getPort(data);
		return controlport;
	}



	private boolean isInteger(String value)
	{
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e){
			return false;
		}

	}

}

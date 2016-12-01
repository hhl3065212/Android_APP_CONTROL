package com.haier.tft.wifimodule.connectgateserver;

import com.haier.tft.wifimodule.moduletool.StaticValueAndConnectUrl;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2015/12/4.
 */
public class GetUrlAndPortThread implements  Runnable{

    private GetUrlAndPortResult getUrlAndPortResult;

    public GetUrlAndPortThread(GetUrlAndPortResult getUrlAndPortResult){

        this.getUrlAndPortResult=getUrlAndPortResult;


    }
    @Override
    public void run() {
        {

            GetControlUrlAndPort  getControlUrlAndPort = new GetControlUrlAndPort(StaticValueAndConnectUrl.GateWayUrl, 56808,getUrlAndPortResult);

            try {
                if (StaticValueAndConnectUrl.MAC == null)
                    StaticValueAndConnectUrl.MAC = "";
                 getControlUrlAndPort.doRequest(220, StaticValueAndConnectUrl.devtype, StaticValueAndConnectUrl.MAC);
            } catch (UnsupportedEncodingException e) {
                getUrlAndPortResult.failed("GetUrlAndPortThread get url and port error:UnsupportedEncodingException");
                e.printStackTrace();

            }

        }

    }





}

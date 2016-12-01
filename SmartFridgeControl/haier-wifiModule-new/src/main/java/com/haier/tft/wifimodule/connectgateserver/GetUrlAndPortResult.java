package com.haier.tft.wifimodule.connectgateserver;

/**
 * Created by Administrator on 2015/12/4.
 */
public interface GetUrlAndPortResult {

    public void success(String url,int port);
    public void failed(String e);

}

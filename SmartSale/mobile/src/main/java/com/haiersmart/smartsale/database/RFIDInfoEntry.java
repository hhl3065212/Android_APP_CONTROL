package com.haiersmart.smartsale.database;

import java.io.Serializable;

/**
 * Created by tingting on 2017/12/28.
 */

public class RFIDInfoEntry implements Serializable{
    public int id;
    public String mac;
    public String userid;
    public int counts;
    public String epcid;

    public RFIDInfoEntry(String mac, String userid, int counts, String epcid) {
        this.mac = mac;
        this.userid = userid;
        this.counts = counts;
        this.epcid = epcid;
    }

    public RFIDInfoEntry() {
    }
    public int getId() {
        return id;
    }

    public String getMac() {
        return mac;
    }

    public String getUserid() {
        return userid;
    }

    public int getCounts() {
        return counts;
    }

    public String getEpcid() {
        return epcid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public void setEpcid(String epcid) {
        this.epcid = epcid;
    }


}

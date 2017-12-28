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
}

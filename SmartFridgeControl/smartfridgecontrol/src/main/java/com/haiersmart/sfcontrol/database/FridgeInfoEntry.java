package com.haiersmart.sfcontrol.database;

import java.io.Serializable;

/**
 * Created by tingting on 2016/9/28.
 */
public class FridgeInfoEntry implements Serializable {
    public int id;
    public String name;
    public String value;


    public FridgeInfoEntry(String name, String value) {
        this.name = name;
        this.value = value;

    }

    public FridgeInfoEntry() {
    }
}

package com.haiersmart.sfcontrol.database;

import java.io.Serializable;

/**
 * Created by tingting on 2016/9/28.
 */
public class FridgeControlEntry implements Serializable {
    public int id;
    public String name;
    public int value;
    public int disable;

    public  FridgeControlEntry(String name, int value, int disable) {
        this.name = name;
        this.value = value;
        this.disable = disable;
    }

    public  FridgeControlEntry(String name) {
        this.name = name;
    }

    public FridgeControlEntry() {
    }
}

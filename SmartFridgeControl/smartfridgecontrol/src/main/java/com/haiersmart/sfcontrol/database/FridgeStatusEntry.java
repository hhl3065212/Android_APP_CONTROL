package com.haiersmart.sfcontrol.database;

import java.io.Serializable;

/**
 * Created by tingting on 2016/9/28.
 */
public class FridgeStatusEntry implements Serializable{
    public int id;
    public String name;
    public int value;


    public FridgeStatusEntry(String name, int value) {
        this.name = name;
        this.value = value;

    }

    public FridgeStatusEntry() {
    }
}

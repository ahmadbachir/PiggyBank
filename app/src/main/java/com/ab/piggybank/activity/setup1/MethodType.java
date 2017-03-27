package com.ab.piggybank.activity.setup1;

/**
 * Created by Ahmad-PC on 3/27/2017.
 */

public class MethodType {
    String name;
    int picId;

    public MethodType(String name, int picId, int id) {
        this.name = name;
        this.picId = picId;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    int id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }
}

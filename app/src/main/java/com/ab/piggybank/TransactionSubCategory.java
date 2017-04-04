package com.ab.piggybank;

public class TransactionSubCategory {
    int id;
    String name;
    int picId;
    public TransactionSubCategory(int id, String name, int picId) {
        this.id = id;
        this.name = name;
        this.picId = picId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPicId() {
        return picId;
    }
}

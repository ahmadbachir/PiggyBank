package com.ab.piggybank;


import java.util.ArrayList;

public class TransactionCategory {
    int id;
    ArrayList<TransactionSubCategory> transactionSubCategories;
    String name;
    int picId;

    public int getId() {
        return id;
    }

    public ArrayList<TransactionSubCategory> getTransactionSubCategories() {
        return transactionSubCategories;
    }

    public String getName() {
        return name;
    }

    public int getPicId() {
        return picId;
    }

    public TransactionCategory(int id, ArrayList<TransactionSubCategory> transactionSubCategories, String name, int picId) {
        this.id = id;
        this.transactionSubCategories = transactionSubCategories;
        this.name = name;
        this.picId = picId;
    }
}

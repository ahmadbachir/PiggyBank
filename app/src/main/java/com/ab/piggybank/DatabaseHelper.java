package com.ab.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "PiggyBank.db";
    public static final String TABLENAME_1 = "TRANSACTIONS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_AMOUNT = "AMOUNT";
    public static final String COLUMN_ENTRYTYPE = "TYPE";
    public static final String COLUMN_CATEGORY = "CATEGORY";
    public static final String COLUMN_SUBCATEGORY = "SUBCATEGORY";
    public static final String COLUMN_DATE_DAY = "DAY";
    public static final String COLUMN_DATE_MONTH = "MONTH";
    public static final String COLUMN_DATE_YEAR = "YEAR";
    public static final String COLUMN_PAYMENT_METHOD_ID = "PAYMENTMETHOD";
    public static final String TABLENAME_2 = "CURRENCYRATE";
    public static final String COLUMN_RATE = "RATE";
    public static final String TABLENAME_3 = "PAYMENTMETHODS";
    public static final String COLUMN_PAYMENT_METHOD_NAME = "METHODNAME";
    public static final String COLUMN_METHOD_TYPE = "METHODTYPE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("dbHelper","dbHelper Called.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTransactionTable(db);
        createCurrencyRateTable(db);
        createPaymentMethodType(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_3);
        onCreate(db);
    }

    private void createTransactionTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_1 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_ENTRYTYPE + " INTEGER, " +
                COLUMN_CATEGORY + " INTEGER, " +
                COLUMN_SUBCATEGORY + " INTEGER, " +
                COLUMN_DATE_DAY + " INTEGER, " +
                COLUMN_DATE_MONTH + " INTEGER, " +
                COLUMN_DATE_YEAR + " INTEGER, " +
                COLUMN_PAYMENT_METHOD_ID + " INTEGER)");
    }
    private void createCurrencyRateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_2 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RATE + " REAL)");
    }

    private void createPaymentMethodType(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_3 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PAYMENT_METHOD_NAME + " TEXT, " +
                COLUMN_METHOD_TYPE + " INTEGER)");
    }

    public void insertTransaction(double amount,int type,int category,int subcategory,int day,int month,int year,String paymentMethod){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AMOUNT,amount);
        contentValues.put(COLUMN_ENTRYTYPE,type);
        contentValues.put(COLUMN_CATEGORY,category);
        contentValues.put(COLUMN_SUBCATEGORY,subcategory);
        contentValues.put(COLUMN_DATE_DAY,day);
        contentValues.put(COLUMN_DATE_MONTH,month);
        contentValues.put(COLUMN_DATE_YEAR,year);
        contentValues.put(COLUMN_PAYMENT_METHOD_ID,paymentMethod);
        db.insertOrThrow(TABLENAME_1,null,contentValues);

    }


}

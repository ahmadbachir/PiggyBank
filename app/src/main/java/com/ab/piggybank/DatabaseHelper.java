package com.ab.piggybank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PiggyBank.db";
    public static final String TABLENAME_1 = "TRANSACTIONS";
    public static final String COLUMN_ID = "_id";
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
    public static final String COLUMN_ABV = "CURRENCYABV";
    public static final String COLUMN_CURRENCY_NAME = "CURRENCYNAME";
    public static final String TABLENAME_3 = "PAYMENTMETHODS";
    public static final String COLUMN_PAYMENT_METHOD_NAME = "METHODNAME";
    public static final String COLUMN_METHOD_TYPE = "METHODTYPE";
    public static final String COLUMN_METHOD_TYPE_AFTER_SORT = "TYPEAFTERSORT";
    public static final String COLUMN_METHOD_USABLE = "ISUSABLE";
    public static final String TABLENAME_4 = "CURRENCYNAMES_ENG";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("dbHelper", "dbHelper Called.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTransactionTable(db);
        createCurrencyRateTable(db);
        createPaymentMethodType(db);
        createCurrencyNameTableENG(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_4);
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

    private void createPaymentMethodType(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_3 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PAYMENT_METHOD_NAME + " TEXT, " +
                COLUMN_METHOD_TYPE_AFTER_SORT + " INTEGER, " +
                COLUMN_METHOD_USABLE + " NUMERIC, " +
                COLUMN_METHOD_TYPE + " INTEGER)");
    }

    private void createCurrencyNameTableENG(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_4 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ABV + " TEXT, " +
                COLUMN_CURRENCY_NAME + " TEXT UNIQUE)");
    }

    public void insertTransaction(double amount, int type, int category, int subcategory, int day, int month, int year, String paymentMethod) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_ENTRYTYPE, type);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_SUBCATEGORY, subcategory);
        contentValues.put(COLUMN_DATE_DAY, day);
        contentValues.put(COLUMN_DATE_MONTH, month);
        contentValues.put(COLUMN_DATE_YEAR, year);
        contentValues.put(COLUMN_PAYMENT_METHOD_ID, paymentMethod);
        db.insertOrThrow(TABLENAME_1, null, contentValues);
    }
    public void updateTransaction(long id,double amount, int type, int category, int subcategory, int day, int month, int year, String paymentMethod) {
        Log.i("update","updating");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_ENTRYTYPE, type);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_SUBCATEGORY, subcategory);
        contentValues.put(COLUMN_DATE_DAY, day);
        contentValues.put(COLUMN_DATE_MONTH, month);
        contentValues.put(COLUMN_DATE_YEAR, year);
        contentValues.put(COLUMN_PAYMENT_METHOD_ID, paymentMethod);
        db.update(TABLENAME_1, contentValues,COLUMN_ID + " = " + id,null);
    }

    public Cursor getCurrencyRate() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(false, TABLENAME_2, null, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor getTransaction(long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1,null,COLUMN_ID + " = " + id,null,null,null,null);
        return cursor;
    }

    public void insertCurrencyTable(double rate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RATE, rate);
        db.insert(TABLENAME_2, null, contentValues);
    }

    public void updateCurrencyTable(int row, double rate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RATE, rate);
        db.update(TABLENAME_2, contentValues, COLUMN_ID + " = " + row, null);
    }

    public String getABVENGString(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_4, new String[]{COLUMN_ABV}, COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToPosition(0);
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ABV));
    }

    public Cursor getABVEng() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_4, new String[]{COLUMN_ID, COLUMN_ABV}, null, null, null, null, COLUMN_ABV + " ASC");
        return cursor;
    }

    public Cursor getMethodTable() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_3, null, null, null, null, null, null, null);
        return cursor;
    }
    public Cursor getUserMethod(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_3, null, COLUMN_ID + " = " + id, null, null, null, null, null);
        return cursor;
    }

    public Cursor getCurrencyDataEng(String name) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLENAME_4, null, COLUMN_ABV + " LIKE '%" + name + "%' OR " + COLUMN_CURRENCY_NAME + " LIKE '%" + name + "%'", null, null, null, COLUMN_CURRENCY_NAME + " ASC");
    }

    public void insertPaymentMethods(String name, int posAfterSort, int type) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PAYMENT_METHOD_NAME, name);
        contentValues.put(COLUMN_METHOD_TYPE, type);
        contentValues.put(COLUMN_METHOD_TYPE_AFTER_SORT, posAfterSort);
        contentValues.put(COLUMN_METHOD_USABLE, 1);
        db.insert(TABLENAME_3, null, contentValues);
    }

    public void insertCurrencyDataENG(String name, String abv) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CURRENCY_NAME, name);
        contentValues.put(COLUMN_ABV, abv);
        db.insert(TABLENAME_4, null, contentValues);
    }

    public Cursor getDaysInWeek(int firstDay, int lastDay, int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, null, COLUMN_DATE_DAY + " BETWEEN " + firstDay + " AND " + lastDay + " AND " + COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year, null, COLUMN_DATE_DAY, null, COLUMN_DATE_DAY + " ASC");
        return cursor;
    }

    public Cursor getTransactionsInDay(int day, int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, null, COLUMN_DATE_DAY + " = " + day + " AND " + COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year, null, null, null, COLUMN_DATE_DAY + " ASC");
        return cursor;
    }

    public Cursor getTransactionsInMonth(int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, null, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year, null, null, null, COLUMN_DATE_DAY + " ASC");
        return cursor;
    }

    public void deleteTransaction(long id){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLENAME_1,COLUMN_ID + " = " + id,null);
    }

}

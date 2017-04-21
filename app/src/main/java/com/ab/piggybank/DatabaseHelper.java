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
    public static final String TABLENAME_5 = "DEBT_RELATIONSHIPS";
    public static final String COLUMN_RELATIONSHIP_ICON = "ICON_ID";
    public static final String COLUMN_RELATIONSHIP_NAME = "RELATIONSHIPNAME";
    public static final String TABLENAME_6 = "DEBT_TRANSACTIONS";
    public static final String COLUMN_WHICH_RELATIONSHIP = "RELATIONSHIP_ID";
    public static final String COLUMN_DEBT_DESCRIPTION = "DEBT_DESC";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTransactionTable(db);
        createCurrencyRateTable(db);
        createPaymentMethodType(db);
        createCurrencyNameTableENG(db);
        createDebtRelationshipTable(db);
        createDebtTransactionTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_4);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_5);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_6);
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

    private void createDebtRelationshipTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_5 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RELATIONSHIP_ICON + " INTEGER, " +
                COLUMN_METHOD_USABLE + " NUMERIC, " +
                COLUMN_RELATIONSHIP_NAME + " TEXT UNIQUE)");
    }

    private void createDebtTransactionTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLENAME_6 + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ENTRYTYPE + " INTEGER, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_DATE_DAY + " INTEGER, " +
                COLUMN_DATE_MONTH + " INTEGER, " +
                COLUMN_DATE_YEAR + " INTEGER, " +
                COLUMN_DEBT_DESCRIPTION + " TEXT, " +
                COLUMN_WHICH_RELATIONSHIP + " INTEGER )");
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

    public void updateTransaction(long id, double amount, int type, int category, int subcategory, int day, int month, int year, String paymentMethod) {
        Log.i("update", "updating");
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
        db.update(TABLENAME_1, contentValues, COLUMN_ID + " = " + id, null);
    }

    public Cursor getCurrencyRate() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(false, TABLENAME_2, null, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor getTransaction(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, null, COLUMN_ID + " = " + id, null, null, null, null);
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
    public Cursor getCurrencyNameEng() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_4, new String[]{COLUMN_ID, COLUMN_CURRENCY_NAME}, null, null, null, null, COLUMN_CURRENCY_NAME + " ASC");
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

    public double getAmount(long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_2,new String[]{COLUMN_RATE}, COLUMN_ID + " = " + id,null,null,null,null);
        cursor.moveToPosition(0);
        return cursor.getDouble(0);
    }

    public Cursor getTransactionsInDay(int day, int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, null, COLUMN_DATE_DAY + " = " + day + " AND " + COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year, null, null, null, COLUMN_DATE_DAY + " ASC");
        cursor.moveToPosition(0);
        return cursor;
    }

    public Cursor getTransactionsInMonth(int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, null, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year, null, null, null, COLUMN_DATE_DAY + " ASC");
        cursor.moveToPosition(0);
        return cursor;
    }

    public void deleteTransaction(long id) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(TABLENAME_1, COLUMN_ID + " = " + id, null);
    }

    public Cursor getDaysInMonth(int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{COLUMN_DATE_DAY, COLUMN_DATE_MONTH, COLUMN_DATE_YEAR}, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 0", null, COLUMN_DATE_DAY, null, COLUMN_DATE_DAY + " ASC");
        cursor.moveToPosition(0);
        return cursor;
    }

    public float sumOfExpenseTransactionsDay(int day, int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_DATE_DAY + " = " + day + " AND " + COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 0", null, null, null, null);
        cursor.moveToPosition(0);
        float amount = cursor.getFloat(0);
        cursor.close();
        return amount;
    }

    public Cursor getDaysInMonthIncome(int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{COLUMN_DATE_DAY, COLUMN_DATE_MONTH, COLUMN_DATE_YEAR}, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 1", null, COLUMN_DATE_DAY, null, COLUMN_DATE_DAY + " ASC");
        cursor.moveToPosition(0);
        return cursor;
    }

    public float sumOfIncomeTransactionsDay(int day, int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_DATE_DAY + " = " + day + " AND " + COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 1", null, null, null, null);
        cursor.moveToPosition(0);
        float amount = cursor.getFloat(0);
        cursor.close();
        return amount;
    }

    public float sumOfExponseTransactionsMonth(int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 0", null, null, null, null);
        cursor.moveToPosition(0);
        float amount = cursor.getFloat(0);
        cursor.close();
        return amount;
    }

    public float sumOfIncomeTransactionsMonth(int month, int year) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 1", null, null, null, null);
        cursor.moveToPosition(0);
        float amount = cursor.getFloat(0);
        cursor.close();
        return amount;
    }

    public float sumOfExpenseCategoryInMonth(int month, int year, int category) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 0 AND " + COLUMN_CATEGORY + " = " + category, null, null, null, null);
        cursor.moveToPosition(0);
        float amount = cursor.getFloat(0);
        cursor.close();
        return amount;
    }

    public float sumOfIncomeCategoryInMonth(int month, int year, int category) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_1, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_DATE_MONTH + " = " + month + " AND " + COLUMN_DATE_YEAR + " = " + year + " AND " + COLUMN_ENTRYTYPE + " = 1 AND " + COLUMN_CATEGORY + " = " + category, null, null, null, null);
        cursor.moveToPosition(0);
        float amount = cursor.getFloat(0);
        cursor.close();
        return amount;
    }

    public Cursor getDebtRelationships() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_5, null, COLUMN_METHOD_USABLE + " = 1", null, null, null, null);
        return cursor;
    }

    public Cursor getDeletedDebtRelationships() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_5, null, COLUMN_METHOD_USABLE + " = 0", null, null, null, null);
        return cursor;
    }


    public Cursor getDebtRelationshipAtId(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_5, null, COLUMN_ID + " = " + id, null, null, null, null);
        return cursor;
    }

    public double sumOfDebtRelationIngoing(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_6, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_WHICH_RELATIONSHIP + " = " + id + " AND " + COLUMN_ENTRYTYPE + " = " + 1, null, null, null, null);
        cursor.moveToPosition(0);
        if (cursor.moveToFirst()) {
            double amount = cursor.getDouble(0);
            cursor.close();
            return amount;
        } else {
            cursor.close();
            return 0;
        }
    }

    public double sumOfDebtRelationOutgoing(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_6, new String[]{"SUM (" + COLUMN_AMOUNT + ")"}, COLUMN_WHICH_RELATIONSHIP + " = " + id + " AND " + COLUMN_ENTRYTYPE + " = " + 0, null, null, null, null);
        cursor.moveToPosition(0);
        if (cursor.moveToFirst()) {
            double amount = cursor.getDouble(0);
            cursor.close();
            return amount;
        } else {
            cursor.close();
            return 0;
        }
    }

    public void updateDebtRelationship(long id, int picId, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RELATIONSHIP_ICON, picId);
        contentValues.put(COLUMN_RELATIONSHIP_NAME, name);
        db.update(TABLENAME_5, contentValues, COLUMN_ID + " = " + id, null);
    }



    public void insertDebtRelationship(int picId, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RELATIONSHIP_ICON, picId);
        contentValues.put(COLUMN_METHOD_USABLE, 1);
        contentValues.put(COLUMN_RELATIONSHIP_NAME, name);
        db.insert(TABLENAME_5, null, contentValues);
    }

    public void deleteDebtRelationship(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_METHOD_USABLE, 0);
        db.update(TABLENAME_5, contentValues, COLUMN_ID + " = " + id, null);
    }

    public void unDeleteDebtRelationship(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_METHOD_USABLE, 1);
        db.update(TABLENAME_5, contentValues, COLUMN_ID + " = " + id, null);
    }

    public void deleteDebtTransaction(long id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_METHOD_USABLE, 0);
        db.delete(TABLENAME_6, COLUMN_ID + " = " + id, null);
    }


    public Cursor getDebtTransactionsOfRelationship(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_6, null, COLUMN_WHICH_RELATIONSHIP + " = " + id, null, null, null, COLUMN_DATE_DAY + " DESC, " + COLUMN_DATE_MONTH + " DESC, " + COLUMN_DATE_YEAR + " DESC");
        return cursor;
    }

    public Cursor getDebtTransactionsOfRelationshipNew(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME_6 + " WHERE " + COLUMN_WHICH_RELATIONSHIP + " = " + id, null);
        return cursor;
    }

    public void insertDebtTransaction(int entryType, String desc, double amount, int day, int month, int year, int whichRelationship) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ENTRYTYPE, entryType);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_DATE_DAY, day);
        contentValues.put(COLUMN_DATE_MONTH, month);
        contentValues.put(COLUMN_DATE_YEAR, year);
        contentValues.put(COLUMN_DEBT_DESCRIPTION, desc);
        contentValues.put(COLUMN_WHICH_RELATIONSHIP, whichRelationship);
        db.insert(TABLENAME_6, null, contentValues);
    }

    public void editDebtTransaction(long id, String desc, int entryType, double amount, int day, int month, int year, int whichRelationship) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ENTRYTYPE, entryType);
        contentValues.put(COLUMN_AMOUNT, amount);
        contentValues.put(COLUMN_DATE_DAY, day);
        contentValues.put(COLUMN_DATE_MONTH, month);
        contentValues.put(COLUMN_DATE_YEAR, year);
        contentValues.put(COLUMN_DEBT_DESCRIPTION, desc);
        contentValues.put(COLUMN_WHICH_RELATIONSHIP, whichRelationship);
        db.update(TABLENAME_6, contentValues, COLUMN_ID + " = " + id, null);
    }

    public Cursor getDebtTransactionAtID(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLENAME_6, null, COLUMN_ID + " = " + id, null, null, null, null);
        return cursor;
    }
    public Cursor returnedARowWithTheSameName(String name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_5, new String[]{COLUMN_ID,COLUMN_RELATIONSHIP_NAME}, COLUMN_RELATIONSHIP_NAME + " = '" + name+"'", null, null, null, null);
        return cursor;
    }

}

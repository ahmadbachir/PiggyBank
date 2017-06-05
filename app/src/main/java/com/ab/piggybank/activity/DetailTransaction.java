package com.ab.piggybank.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.ab.piggybank.activity.AddTransactionActivity;
import com.daimajia.easing.linear.Linear;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailTransaction extends AppCompatActivity {
    long id;
    Cursor cursor;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    AdView adView;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);
        id = getIntent().getLongExtra("id", 0);
        cursor = dbHelper.getTransaction(id);
        cursor.moveToPosition(0);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        adView = (AdView) findViewById(R.id.DetailTransactionBannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        getSupportActionBar().setTitle(getString(R.string.transaction) + " #" + id);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_close);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        TextView typeTextView = (TextView) findViewById(R.id.transactionTypeView);
        switch (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE))) {
            case 0:
                typeTextView.setText(R.string.expense);
                break;
            case 1:
                typeTextView.setText(R.string.income);
        }
        TextView amountTextView = (TextView) findViewById(R.id.amountView);
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_AMOUNT));
        String amountString;
        if (amount > 1000000) {
            amountString = (amount / 1000000) + " " + getString(R.string.mn);
        } else if (amount > 1000) {
            amountString = amount / 1000 + " " + getString(R.string.k);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            amountString = decimalFormat.format(amount);
        }
        amountTextView.setText(amountString);
        TextView currencyTextView = (TextView) findViewById(R.id.currencyTextView);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Arabic":
                currencyTextView.setText(dbHelper.getABVENGString(preferences.getInt("country", 1)));
                break;
            default:
                currencyTextView.setText(dbHelper.getABVENGString(preferences.getInt("country", 1)));
        }
        TextView dateTextView = (TextView) findViewById(R.id.dateText);
        Date date = new Date(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_YEAR)) - 1900, cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_MONTH)), cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_DAY)));
        dateTextView.setText(DateFormat.getDateInstance().format(date));
        ImageView category = (ImageView) findViewById(R.id.categoryIcon);
        TextView subCategoryTextView = (TextView) findViewById(R.id.categorySubCatView);
        TextView categoryTextView = (TextView) findViewById(R.id.categoryView);
        Utils utils = new Utils();
        if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_SUBCATEGORY)) != -1) {
            category.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.categoryGroups(this).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE))).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CATEGORY))).getTransactionSubCategories().get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_SUBCATEGORY))).getPicId()), 120, 120, false));
            subCategoryTextView.setText(utils.categoryGroups(this).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE))).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CATEGORY))).getTransactionSubCategories().get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_SUBCATEGORY))).getName());
            categoryTextView.setText(utils.categoryGroups(this).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE))).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CATEGORY))).getName());
        } else {
            category.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.categoryGroups(this).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE))).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CATEGORY))).getPicId()), 120, 120, false));
            subCategoryTextView.setText(utils.categoryGroups(this).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE))).get(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CATEGORY))).getName());
            categoryTextView.setVisibility(View.GONE);
            subCategoryTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            subCategoryTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        }

        if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE)) != 0) {
            LinearLayout paymentMethod = (LinearLayout) findViewById(R.id.paymentMethodLayout);
            paymentMethod.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.textView15);
            textView.setVisibility(View.GONE);
        } else {
            ImageView paymentMethodIcon = (ImageView) findViewById(R.id.methodIconView);
            Cursor methodCursor = dbHelper.getUserMethod(cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_PAYMENT_METHOD_ID)));
            methodCursor.moveToPosition(0);
            if (methodCursor.getCount() != 0) {
                if (methodCursor.getInt(methodCursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT)) != -1) {
                    paymentMethodIcon.setImageResource(utils.paymentMethodIcons()[methodCursor.getInt(methodCursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT))]);
                    TextView paymentMethodName = (TextView) findViewById(R.id.methodNameView);
                    paymentMethodName.setText(methodCursor.getString(methodCursor.getColumnIndexOrThrow(dbHelper.COLUMN_PAYMENT_METHOD_NAME)));
                    TextView paymentMethodType = (TextView) findViewById(R.id.methodTypeView);
                    paymentMethodType.setText(getResources().getStringArray(R.array.paymentMethodNames)[methodCursor.getInt(methodCursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT))]);
                } else {
                    paymentMethodIcon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cash), 120, 120, false));
                    TextView paymentMethodName = (TextView) findViewById(R.id.methodNameView);
                    TextView paymentMethodType = (TextView) findViewById(R.id.methodTypeView);
                    paymentMethodName.setText(R.string.cash);
                    paymentMethodName.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                    paymentMethodName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    paymentMethodType.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_transaction_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        if (item.getItemId() == R.id.edit) {
            Intent i = new Intent(this, AddTransactionActivity.class);
            i.putExtra("editing", true);
            i.putExtra("id", cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID)));
            i.putExtra("amount", cursor.getDouble(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_AMOUNT)));
            i.putExtra("day", cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_DAY)));
            i.putExtra("month", cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_MONTH)));
            i.putExtra("year", cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_YEAR)));
            i.putExtra("type", cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE)));
            i.putExtra("cat", cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CATEGORY)));
            i.putExtra("subCat", cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_SUBCATEGORY)));
            startActivity(i);
            finish();
        }
        if (item.getItemId() == R.id.delete) {
            dbHelper.deleteTransaction(id);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (adView!= null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView!= null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView!= null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    
}

package com.ab.piggybank.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailDebtActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_debt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        dbHelper = new DatabaseHelper(this);
        final long id1 = getIntent().getLongExtra("id", 1);
        Cursor cursor = dbHelper.getDebtRelationshipAtId(id1);
        cursor.moveToPosition(0);
        if (id1 == 1) {
            getSupportActionBar().setTitle(R.string.the_bank);
        } else {
            getSupportActionBar().setTitle(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_NAME)));
        }
        cursor.close();
        final ListView listView = (ListView) findViewById(R.id.debt_transaction_list);
        MyListAdapter listAdapter = new MyListAdapter(this, dbHelper.getDebtTransactionsOfRelationship(id1));
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailDebtActivity.this);
                builder.setTitle(R.string.what_would_you_like_to_do);
                builder.setItems(new String[]{getString(R.string.edit), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(getApplicationContext(), AddDebtTransaction.class);
                                Cursor cursor1 = dbHelper.getDebtTransactionAtID(id);
                                cursor1.moveToPosition(0);
                                intent.putExtra("editing", true);
                                intent.putExtra("debt", true);
                                intent.putExtra("amount", cursor1.getDouble(cursor1.getColumnIndexOrThrow(dbHelper.COLUMN_AMOUNT)));
                                intent.putExtra("id", id);
                                intent.putExtra("year", cursor1.getInt(cursor1.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_YEAR)));
                                intent.putExtra("month", cursor1.getInt(cursor1.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_MONTH)));
                                intent.putExtra("day", cursor1.getInt(cursor1.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_DAY)));
                                intent.putExtra("desc", cursor1.getString(cursor1.getColumnIndexOrThrow(dbHelper.COLUMN_DEBT_DESCRIPTION)));
                                Cursor cursor2 = dbHelper.getDebtRelationships();
                                cursor2.moveToPosition(0);
                                boolean foundPos = false;
                                while (!foundPos) {
                                    long rowId = cursor2.getLong(cursor2.getColumnIndexOrThrow(dbHelper.COLUMN_ID));
                                    if (id1 == rowId) {
                                        foundPos = true;
                                    } else {
                                        cursor2.moveToNext();
                                    }
                                }
                                intent.putExtra("spinnerPos", String.valueOf(cursor2.getPosition()));
                                intent.putExtra("type", cursor1.getInt(cursor1.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE)));
                                startActivity(intent);
                                cursor1.close();
                                cursor2.close();
                                finish();
                                break;
                            case 1:
                                dbHelper.deleteDebtTransaction(id);
                                if (listView.getAdapter().getCount() == 1) {
                                    finish();
                                } else {
                                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                    ((CursorAdapter )listView.getAdapter()).changeCursor(dbHelper.getDebtTransactionsOfRelationship(id1));
                                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(listView);
                                }


                        }
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyListAdapter extends CursorAdapter {
        String currency = null;

        public MyListAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.debt_transaction_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.desc = (TextView) view.findViewById(R.id.descText);
            viewHolder.dateText = (TextView) view.findViewById(R.id.dateText);
            viewHolder.amountView = (TextView) view.findViewById(R.id.amountView);
            viewHolder.indicator = (ImageView) view.findViewById(R.id.typeIndicator);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (currency == null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                currency = getResources().getStringArray(R.array.currency_abv)[preferences.getInt("country", 1) - 1];
            }
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_AMOUNT));
            String amountString;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            if (amount > 1000000) {
                amountString = decimalFormat.format(amount / 1000000) + " " + getString(R.string.mn);
            } else if (amount > 1000) {
                amountString = decimalFormat.format(amount / 1000) + " " + getString(R.string.k);
            } else {

                amountString = decimalFormat.format(amount);
            }
            viewHolder.desc.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DEBT_DESCRIPTION)));
            if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE)) != 0) {
                viewHolder.indicator.setBackgroundColor(getColor(R.color.green_accent));
            } else {
                viewHolder.indicator.setBackgroundColor(getColor(R.color.red_accent));
            }
            viewHolder.amountView.setText(amountString + " " + currency);
            Date date = new Date(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_YEAR)) - 1900, cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_MONTH)), cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_DAY)));
            viewHolder.dateText.setText(DateFormat.getDateInstance().format(date));
        }

        private class ViewHolder {
            TextView desc;
            TextView dateText;
            TextView amountView;
            ImageView indicator;
        }
    }

}

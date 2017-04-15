package com.ab.piggybank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class DetailDebtActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_debt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        dbHelper = new DatabaseHelper(this);
        long id = getIntent().getLongExtra("id", 1);
        Cursor cursor = dbHelper.getDebtRelationships();
        cursor.moveToPosition((int) (id - 1));
        toolbar.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_NAME)));
        cursor.close();
        final ListView listView = (ListView) findViewById(R.id.debt_transaction_list);
        MyListAdapter listAdapter = new MyListAdapter(this, dbHelper.getDebtTransactionsOfRelationship(id));
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailDebtActivity.this);
                builder.setTitle(R.string.what_would_you_like_to_do);
                builder.setItems(new String[]{getString(R.string.edit), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 1: //TODO Insert intent to edit add debt transaction
                                break;
                            case 2: dbHelper.deleteDebtTransaction(id);
                                listView.deferNotifyDataSetChanged();
                        }
                    }
                });
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
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
            view.setTag(view);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if(currency == null){
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                currency = getResources().getStringArray(R.array.currency_abv)[preferences.getInt("country",1)];
            }
            viewHolder.desc.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DEBT_DESCRIPTION)));
            if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ENTRYTYPE)) != 0) {
                viewHolder.amountView.setTextColor(getResources().getColor(R.color.red_accent));
            } else {
                viewHolder.amountView.setTextColor(getResources().getColor(R.color.green_accent));
            }
            viewHolder.amountView.setText(cursor.getDouble(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_AMOUNT)) + " " +  currency);
            Date date = new Date(cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_YEAR)),cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_MONTH)),cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_DATE_DAY)));
            viewHolder.dateText.setText(DateFormat.getDateInstance().format(date));
        }

        private class ViewHolder {
            TextView desc;
            TextView dateText;
            TextView amountView;
        }
    }

}

package com.ab.piggybank;

import android.app.DatePickerDialog;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.activity.Calculator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddDebtTransaction extends AppCompatActivity {
    boolean editing = false;
    java.util.Calendar todaysCalendar = java.util.Calendar.getInstance();
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    DatePickerDialog.OnDateSetListener onDateSetListener;
    double amount;
    long id = 0;
    int spinnerPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_close);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ImageView imageView = (ImageView) findViewById(R.id.flagImage);
        Utils utils = new Utils();
        final Spinner spinner = (Spinner) findViewById(R.id.whoSpinner);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageView.setImageResource(utils.flagIds()[preferences.getInt("country", 0) - 1]);
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                updateDateText();
            }
        };
        final TextInputLayout editText = (TextInputLayout) findViewById(R.id.descriptionInput);
        updateDateText();
        TextView textView = (TextView) findViewById(R.id.dateTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog().show();
            }
        });
        TextView amountView = (TextView) findViewById(R.id.amount_text);
        amountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Calculator.class);
                i.putExtra("debt", true);
                if (amount != 0) {
                    i.putExtra("amount", amount);
                }
                if (todaysCalendar.get(java.util.Calendar.DATE) != calendar.get(java.util.Calendar.DATE)) {
                    i.putExtra("day", calendar.get(java.util.Calendar.DAY_OF_MONTH));
                    i.putExtra("month", calendar.get(java.util.Calendar.MONTH));
                    i.putExtra("year", calendar.get(java.util.Calendar.YEAR));
                }
                i.putExtra("editing", editing);
                if (id != 0) {
                    i.putExtra("id", id);
                }

                if(editText.getEditText().getText().toString().length() != 0){
                    i.putExtra("desc",editText.getEditText().getText().toString());
                }
                RadioButton radioButton1 = (RadioButton) findViewById(R.id.ingoingRadiobutton);
                RadioButton radioButton2 = (RadioButton) findViewById(R.id.outgoingRadiobutton);
                if(radioButton1.isChecked()){
                    i.putExtra("type",1);
                }
                if(radioButton2.isChecked()){
                    i.putExtra("type",0);
                }
                i.putExtra("spinnerPos", String.valueOf(spinner.getSelectedItemPosition()));
                startActivity(i);
                finish();
            }
        });
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, dbHelper.getDebtRelationships());
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPos = position;
                updateMethodIcon(spinnerPos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.done) {
            if (amount == 0) {
                Toast.makeText(this, R.string.amount_needs_to_more_than_zero, Toast.LENGTH_LONG).show();
                return true;
            }
            TextInputLayout editText = (TextInputLayout) findViewById(R.id.descriptionInput);
            if (editText.getEditText().getText().toString().length() == 0) {
                Toast.makeText(this, R.string.need_desc_to_proceed, Toast.LENGTH_LONG).show();
                return true;
            }
            RadioButton radioButton1 = (RadioButton) findViewById(R.id.ingoingRadiobutton);
            RadioButton radioButton2 = (RadioButton) findViewById(R.id.outgoingRadiobutton);
            if (!(radioButton1.isChecked() || radioButton2.isChecked())) {
                Toast.makeText(this, R.string.choose_ingoing_outgoing, Toast.LENGTH_LONG).show();
                return true;
            }
            int type = 0;
            if (radioButton1.isChecked()) {
                type = 1;
            }
            if (radioButton2.isChecked()) {
                type = 0;
            }
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            final Spinner spinner = (Spinner) findViewById(R.id.whoSpinner);
            if (editing) {
                dbHelper.editDebtTransaction(id,editText.getEditText().getText().toString(), type, amount, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), (int) spinner.getSelectedItemId());
            } else {
                dbHelper.insertDebtTransaction(type, editText.getEditText().getText().toString(),amount, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), (int) spinner.getSelectedItemId());
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = (ImageView) findViewById(R.id.flagImage);
        Utils utils = new Utils();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageView.setImageResource(utils.flagIds()[preferences.getInt("country", 0) - 1]);
        TextView currencyTextView = (TextView) findViewById(R.id.currency_text);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Arabic":
                currencyTextView.setText(dbHelper.getABVENGString(preferences.getInt("country", 1)));
                break;
            default:
                currencyTextView.setText(dbHelper.getABVENGString(preferences.getInt("country", 1)));
        }
        editing = getIntent().getBooleanExtra("editing", false);
        id = getIntent().getLongExtra("id", 0);
        amount = getIntent().getDoubleExtra("amount", 0.0);
        TextView amountText = (TextView) findViewById(R.id.amount_text);
        String amountString;
        if (amount > 1000000) {
            amountString = (amount / 1000000) + " " + getString(R.string.mn);
        } else if (amount > 1000) {
            amountString = amount / 1000 + " " + getString(R.string.k);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            amountString = decimalFormat.format(amount);
        }
        amountText.setText(amountString);
        if (getIntent().getIntExtra("day", -1) != -1) {
            calendar.set(getIntent().getIntExtra("year", 0), getIntent().getIntExtra("month", 0), getIntent().getIntExtra("day", 0));
            updateDateText();
        }
        if (getIntent().getStringExtra("spinnerPos") != null) {
            spinnerPos = Integer.valueOf(getIntent().getStringExtra("spinnerPos"));
        }
        if(getIntent().getStringExtra("desc") != null){
            final TextInputLayout editText = (TextInputLayout) findViewById(R.id.descriptionInput);
            editText.getEditText().setText(getIntent().getStringExtra("desc"));
        }
        final Spinner spinner = (Spinner) findViewById(R.id.whoSpinner);
        spinner.setSelection(spinnerPos);
        updateMethodIcon(spinnerPos);
        if(getIntent().getIntExtra("type",-1) != -1){
            RadioButton radioButton1 = (RadioButton) findViewById(R.id.ingoingRadiobutton);
            RadioButton radioButton2 = (RadioButton) findViewById(R.id.outgoingRadiobutton);
            if(getIntent().getIntExtra("type",-1) == 1){
                radioButton1.toggle();
            }
            if (getIntent().getIntExtra("type",-1) == 0){
                radioButton2.toggle();
            }
        }


    }


    private void updateMethodIcon(int pos) {
        ImageView icon = (ImageView) findViewById(R.id.whoIcon);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getDebtRelationships();
        cursor.moveToPosition(pos);
        Utils utils = new Utils();
        YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_longAnimTime)).playOn(icon);
        if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_ICON)) != -1) {
            icon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.debt_relations_icon()[cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_ICON))]), 120, 120, false));
        } else {
            icon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bank), 120, 120, false));

        }
        YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_longAnimTime)).playOn(icon);
    }

    private DatePickerDialog datePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(todaysCalendar.getTimeInMillis());
        return datePickerDialog;
    }

    private void updateDateText() {

        TextView textView = (TextView) findViewById(R.id.dateTextView);
        if (calendar.get(java.util.Calendar.DATE) != todaysCalendar.get(java.util.Calendar.DATE)) {
            Date date = new Date(calendar.getTimeInMillis());
            textView.setText(DateFormat.getDateInstance().format(date));
        } else {
            textView.setText(R.string.today);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculator_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class SpinnerAdapter extends CursorAdapter {
        DatabaseHelper dbHelper;

        public SpinnerAdapter(Context context, Cursor c) {
            super(context, c);
            dbHelper = new DatabaseHelper(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mainText = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_ICON)) != -1) {
                viewHolder.mainText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_RELATIONSHIP_NAME)));
            }
            else{
                viewHolder.mainText.setText(R.string.the_bank);
            }
            viewHolder.mainText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        }

        private class ViewHolder {
            TextView mainText;
        }
    }

}

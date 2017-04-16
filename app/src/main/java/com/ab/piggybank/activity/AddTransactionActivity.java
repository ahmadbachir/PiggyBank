package com.ab.piggybank.activity;

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
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.Category;
import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.TransactionCategory;
import com.ab.piggybank.Utils;
import com.ab.piggybank.activity.setup1.setupActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddTransactionActivity extends AppCompatActivity {
    double amount = 0;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    java.util.Calendar todaysCalendar = java.util.Calendar.getInstance();
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    int type = -1;
    int cat = -1;
    int subCat = -1;
    int spinnerPos = 0;
    boolean editing = false;
    long id = 0;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_close);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        updateDateText();
        final Spinner spinner = (Spinner) findViewById(R.id.methodSpinner);
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
                if (amount != 0) {
                    i.putExtra("amount", amount);
                }
                if (todaysCalendar.get(java.util.Calendar.DATE) != calendar.get(java.util.Calendar.DATE)) {
                    i.putExtra("day", calendar.get(java.util.Calendar.DAY_OF_MONTH));
                    i.putExtra("month", calendar.get(java.util.Calendar.MONTH));
                    i.putExtra("year", calendar.get(java.util.Calendar.YEAR));
                }
                if (type != -1) {
                    i.putExtra("type", type);
                    i.putExtra("cat", cat);
                    i.putExtra("subCat", subCat);
                }
                if (isExpense()) {
                    i.putExtra("isExpense", isExpense());
                }
                i.putExtra("editing", editing);
                if(id != 0){
                    i.putExtra("id",id);
                }

                i.putExtra("spinnerPos", spinner.getSelectedItemPosition());
                startActivity(i);
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                updateDateText();
            }
        };

        TextView chooseCategory = (TextView) findViewById(R.id.categoryFiller);
        chooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Category.class);
                if (amount != 0) {
                    i.putExtra("amount", amount);
                }
                if (todaysCalendar.get(java.util.Calendar.DATE) != calendar.get(java.util.Calendar.DATE)) {
                    i.putExtra("day", calendar.get(java.util.Calendar.DAY_OF_MONTH));
                    i.putExtra("month", calendar.get(java.util.Calendar.MONTH));
                    i.putExtra("year", calendar.get(java.util.Calendar.YEAR));
                }
                if (type != -1) {
                    i.putExtra("type", type);
                    i.putExtra("cat", cat);
                    i.putExtra("subCat", subCat);
                }
                if (isExpense()) {
                    i.putExtra("isExpense", isExpense());
                }
                if (editing) {
                    i.putExtra("editing", editing);
                }
                if(id != 0){
                    i.putExtra("id",id);
                }

                i.putExtra("spinnerPos", spinner.getSelectedItemPosition());
                startActivity(i);
            }
        });
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, dbHelper.getMethodTable());
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != spinnerPos) {
                    spinnerPos = position;
                    updateMethodIcon(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private Boolean isExpense() {
        return (type == 0);
    }

    class SpinnerAdapter extends CursorAdapter {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        public SpinnerAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_2texts, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.item_text);
            viewHolder.type = (TextView) view.findViewById(R.id.item_subText);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_PAYMENT_METHOD_NAME)));
            if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT)) != -1) {
                if (viewHolder.type.getVisibility() == View.GONE) {
                    viewHolder.type.setVisibility(View.VISIBLE);
                    viewHolder.name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                viewHolder.type.setText(context.getResources().getStringArray(R.array.paymentMethodNames)[cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT))]);
            } else {
                viewHolder.type.setVisibility(View.GONE);
                viewHolder.name.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            }
        }

        private class ViewHolder {
            TextView name;
            TextView type;
        }
    }

    private DatePickerDialog datePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(todaysCalendar.getTimeInMillis());
        return datePickerDialog;
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
        if (getIntent().getIntExtra("type", -1) != -1) {
            type = getIntent().getIntExtra("type", -1);
            cat = getIntent().getIntExtra("cat", -1);
            subCat = getIntent().getIntExtra("subCat", -1);
            updateCategoryView();
        }
        if (getIntent().getStringExtra("spinnerPos") != null) {
            spinnerPos = Integer.valueOf(getIntent().getStringExtra("spinnerPos"));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            done();
        }
        return false;
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.calculator_menu, menu);
        return true;
    }

    private void updateCategoryView() {
        TextView chooseCategory = (TextView) findViewById(R.id.categoryFiller);
        if (chooseCategory.getAlpha() == 1f) {
            chooseCategory.setAlpha(0f);
        }
        TextView typeText = (TextView) findViewById(R.id.typeTextView);
        if (type == 0) {
            typeText.setText(getString(R.string.expense));
        } else {
            typeText.setText(getString(R.string.income));
        }
        TextView catText = (TextView) findViewById(R.id.categoryTextView);
        Utils utils = new Utils();
        if (subCat == -1) {
            ImageView categoryImageView = (ImageView) findViewById(R.id.categoryIcon);
            if (type == 0) {
                TransactionCategory transactionCategory = utils.expenseGroups(this).get(cat);
                catText.setText(transactionCategory.getName());
                categoryImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), transactionCategory.getPicId()), 120, 120, false));
            } else {
                TransactionCategory transactionCategory = utils.incomeGroups(this).get(cat);
                catText.setText(transactionCategory.getName());
                categoryImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), transactionCategory.getPicId()), 120, 120, false));

            }
        } else {
            ImageView categoryImageView = (ImageView) findViewById(R.id.categoryIcon);
            if (type == 0) {
                TransactionCategory transactionCategory = utils.expenseGroups(this).get(cat);
                catText.setText(transactionCategory.getTransactionSubCategories().get(subCat).getName());
                categoryImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), transactionCategory.getTransactionSubCategories().get(subCat).getPicId()), 120, 120, false));
            } else {
                TransactionCategory transactionCategory = utils.incomeGroups(this).get(cat);
                catText.setText(transactionCategory.getName());
                categoryImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), transactionCategory.getPicId()), 120, 120, false));

            }
        }
    }

    private void updateMethodSpinnerStatus() {
        ImageView overlay = (ImageView) findViewById(R.id.overlay);
        Spinner spinner = (Spinner) findViewById(R.id.methodSpinner);
        if (isExpense()) {
            spinner.setEnabled(true);
            YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(overlay);
            spinner.setSelection(spinnerPos);
        } else {
            spinner.setEnabled(false);
            overlay.animate().alpha(0.5f).setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            spinner.setSelection(spinnerPos);
        }
        updateMethodIcon(spinnerPos);
    }

    private void updateMethodIcon(int pos) {
        ImageView icon = (ImageView) findViewById(R.id.methodIcon);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getMethodTable();
        cursor.moveToPosition(pos);
        Utils utils = new Utils();
        if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT)) != -1) {

            icon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), utils.paymentMethodIcons()[cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_METHOD_TYPE_AFTER_SORT))]), 120, 120, false));
        } else {
            icon.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cash), 120, 120, false));
        }
        YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_longAnimTime));
    }

    private void done() {
        if (amount == 0) {
            Toast toast = Toast.makeText(this, R.string.amount_needs_to_more_than_zero, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (type == -1) {
            Toast toast = Toast.makeText(this, R.string.need_to_choose_a_category, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Spinner spinner = (Spinner) findViewById(R.id.methodSpinner);
        if (!editing) {
            if (isExpense()) {
                dbHelper.insertTransaction(amount, type, cat, subCat, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), String.valueOf(spinner.getItemIdAtPosition(spinnerPos)));
            } else {
                dbHelper.insertTransaction(amount, type, cat, subCat, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), null);
            }
        } else {
            if (isExpense()) {
                dbHelper.updateTransaction(id, amount, type, cat, subCat, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), String.valueOf(spinner.getItemIdAtPosition(spinnerPos)));
            } else {
                dbHelper.updateTransaction(id, amount, type, cat, subCat, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), null);
            }
        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}

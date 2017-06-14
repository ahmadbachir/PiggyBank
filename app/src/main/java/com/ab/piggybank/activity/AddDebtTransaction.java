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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.R;
import com.ab.piggybank.Utils;
import com.ab.piggybank.activity.Calculator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddDebtTransaction extends AppCompatActivity {
    boolean editing = false;
    java.util.Calendar todaysCalendar = java.util.Calendar.getInstance();
    java.util.Calendar calendar = java.util.Calendar.getInstance();
    DatePickerDialog.OnDateSetListener onDateSetListener;
    double amount;
    long id = 0;
    int spinnerPos = 0;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
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

    private void showCurrencyDialog() {
        if (amount != 0) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.currency_dialog_layout, null, false);
            final AlertDialog dialog = builder.create();
            final Utils utils = new Utils();
            final int toCurrency = preferences.getInt("country", 1);
            final ImageView fromFlag = (ImageView) view.findViewById(R.id.fromFlag);
            fromFlag.setImageResource(utils.flagIds()[preferences.getInt("country", 1) - 1]);
            ImageView toFlag = (ImageView) view.findViewById(R.id.toFlag);
            toFlag.setImageResource(utils.flagIds()[preferences.getInt("country", 1) - 1]);
            final double afterAmount = amount;
            TextView fromAmount = (TextView) view.findViewById(R.id.fromAmount);
            String fromAmountString;
            if (amount > 1000000) {
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                fromAmountString = decimalFormat.format(amount / 1000000) + " " + getString(R.string.mn);
            } else if (amount > 1000) {
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                fromAmountString = decimalFormat.format(amount / 1000) + " " + getString(R.string.k);
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                fromAmountString = decimalFormat.format(amount);
            }
            fromAmount.setText(fromAmountString);
            final TextView toAmount = (TextView) view.findViewById(R.id.toAmount);
            String toAmountString;
            if (afterAmount > 1000000) {
                toAmountString = (afterAmount / 1000000) + " " + getString(R.string.mn);
            } else if (afterAmount > 1000) {
                toAmountString = afterAmount / 1000 + " " + getString(R.string.k);
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                toAmountString = decimalFormat.format(afterAmount);
            }
            toAmount.setText(toAmountString);

            final Spinner fromCurrencyName = (Spinner) view.findViewById(R.id.fromSpinner);
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            Cursor cursor = dbHelper.getCurrencyNameEng();
            cursor.moveToPosition(0);
            int spinnerPos = 0;
            boolean foundSpinnerpos = false;
            while (!foundSpinnerpos) {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID)) == preferences.getInt("country", 1)) {
                    spinnerPos = cursor.getPosition();
                    foundSpinnerpos = true;
                } else {
                    cursor.moveToNext();
                }
            }
            DialogSpinnerAdapter spinnerAdapter = new DialogSpinnerAdapter(this, cursor);
            fromCurrencyName.setAdapter(spinnerAdapter);
            fromCurrencyName.setSelection(spinnerPos);

            TextView toCurrencyName = (TextView) view.findViewById(R.id.toCurrencyName);
            toCurrencyName.setText(getResources().getStringArray(R.array.currencyName)[preferences.getInt("country", 1) - 1]);

            final TextView fromCurrencyAbv = (TextView) view.findViewById(R.id.fromCurrencyAbv);
            fromCurrencyAbv.setText(getResources().getStringArray(R.array.currency_abv)[preferences.getInt("country", 1) - 1]);

            TextView toCurrencyAbv = (TextView) view.findViewById(R.id.toCurrencyABV);
            toCurrencyAbv.setText(getResources().getStringArray(R.array.currency_abv)[preferences.getInt("country", 1) - 1]);

            fromCurrencyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(fromCurrencyAbv);
                    fromCurrencyAbv.setText(getResources().getStringArray(R.array.currency_abv)[(int) (id - 1)]);
                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(fromCurrencyAbv);
                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(fromFlag);
                    fromFlag.setImageResource(utils.flagIds()[(int) (id - 1)]);
                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(fromFlag);
                    double afterAmount = convertedAmount(amount, id);
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    afterAmount = Double.parseDouble(decimalFormat.format(afterAmount));
                    String toAmountString;
                    if (afterAmount > 1000000) {
                        afterAmount = afterAmount / 1000000;
                        afterAmount = Double.parseDouble(decimalFormat.format(afterAmount));
                        toAmountString = afterAmount + " " + getString(R.string.mn);
                    } else if (afterAmount > 1000) {
                        afterAmount = afterAmount / 1000;
                        afterAmount = Double.parseDouble(decimalFormat.format(afterAmount));
                        toAmountString = afterAmount + " " + getString(R.string.k);
                    } else {
                        toAmountString = decimalFormat.format(afterAmount);
                    }
                    YoYo.with(Techniques.FadeOut).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(toAmount);
                    toAmount.setText(toAmountString);
                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(toAmount);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ImageButton dismissButton = (ImageButton) view.findViewById(R.id.dismiss);
            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ImageButton doneButton = (ImageButton) view.findViewById(R.id.done);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    DecimalFormat decimalFormat = new DecimalFormat("#.#");
                    setAmount(Double.parseDouble(decimalFormat.format(convertedAmount(amount, fromCurrencyName.getSelectedItemId()))));
                    TextView amountText = (TextView) findViewById(R.id.amount_text);
                    String amountString;
                    if (amount > 1000000) {

                        amountString = decimalFormat.format(amount / 1000000) + " " + getString(R.string.mn);
                    } else if (amount > 1000) {

                        amountString = decimalFormat.format(amount / 1000) + " " + getString(R.string.k);
                    } else {

                        amountString = decimalFormat.format(amount);
                    }
                    amountText.setText(amountString);

                    YoYo.with(Techniques.FadeIn).duration(getResources().getInteger(android.R.integer.config_mediumAnimTime)).playOn(amountText);
                }
            });
            dialog.setView(view);
            dialog.show();
            final float scale = getResources().getDisplayMetrics().density;
            int px = (int) (400 * scale + 0.5f);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, px);
        }else {
            Toast.makeText(this,R.string.amount_needs_to_more_than_zero,Toast.LENGTH_LONG).show();
        }
    }
    private double convertedAmount(double amount, long id) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        double amount1 = amount / dbHelper.getAmount(id);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        amount1 = amount1 * dbHelper.getAmount(preferences.getInt("country", 1));
        return Double.parseDouble(decimalFormat.format(amount1));
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    private static class DialogSpinnerAdapter extends CursorAdapter {

        DatabaseHelper dbHelper;

        public DialogSpinnerAdapter(Context context, Cursor c) {
            super(context, c);
            dbHelper = new DatabaseHelper(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.abvText = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.abvText.setText(cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_CURRENCY_NAME)));
            viewHolder.abvText.setGravity(Gravity.CENTER);
        }

        private class ViewHolder {
            TextView abvText;
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = (ImageView) findViewById(R.id.flagImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrencyDialog();
            }
        });
        Utils utils = new Utils();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        imageView.setImageResource(utils.flagIds()[preferences.getInt("country", 0) - 1]);
        TextView currencyTextView = (TextView) findViewById(R.id.currency_text);
        currencyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrencyDialog();
            }
        });
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


        new FancyShowCaseQueue()
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_1").titleStyle(R.style.textAppearanceShowcase, Gravity.CENTER_VERTICAL | Gravity.START).backgroundColor(getResources().getColor(android.R.color.white)).title(getString(R.string.add_debt_transaction_1)).build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_2").focusCircleRadiusFactor(0.2).focusOn(findViewById(R.id.cardView)).focusShape(FocusShape.ROUNDED_RECTANGLE).titleStyle(R.style.textAppearanceShowcase, Gravity.START | Gravity.BOTTOM).backgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent)).title(getString(R.string.add_transaction_2)).build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_3").focusOn(findViewById(R.id.cardView)).focusShape(FocusShape.ROUNDED_RECTANGLE).titleStyle(R.style.textAppearanceShowcase, Gravity.START | Gravity.BOTTOM).backgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent)).title(getString(R.string.add_transaction_3)).build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_4").focusOn(findViewById(R.id.cardView2)).focusShape(FocusShape.ROUNDED_RECTANGLE).titleStyle(R.style.textAppearanceShowcase, Gravity.START | Gravity.BOTTOM).backgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent)).title(getString(R.string.add_transaction_4)).build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_5").focusOn(findViewById(R.id.cardView3)).focusShape(FocusShape.ROUNDED_RECTANGLE).titleStyle(R.style.textAppearanceShowcase, Gravity.START | Gravity.BOTTOM).backgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent)).title(getString(R.string.add_debt_transaction_5)).build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_6").focusOn(findViewById(R.id.cardView4)).focusShape(FocusShape.ROUNDED_RECTANGLE).titleStyle(R.style.textAppearanceShowcase, Gravity.START | Gravity.BOTTOM).backgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent)).title("Another thing about debt is that you will have to choose who your debt transaction was with. Here is the dropdown menu of your debt relationships.").build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_7").focusOn(findViewById(R.id.cardView5)).focusShape(FocusShape.ROUNDED_RECTANGLE).titleStyle(R.style.textAppearanceShowcase, Gravity.START | Gravity.CENTER_VERTICAL).backgroundColor(getResources().getColor(R.color.colorPrimaryDarkTranslucent)).title("Here is where you choose the type of debt, is it ingoing (meaning that the person owes you money) or outgoing (meaning that you owe the person money)?").build())
                .add(new FancyShowCaseView.Builder(this).showOnce("add_debt_transaction_8").titleStyle(R.style.textAppearanceShowcase, Gravity.CENTER).backgroundColor(getResources().getColor(android.R.color.white)).title("We hope you enjoy using PiggyBank!").build())
                .show();

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

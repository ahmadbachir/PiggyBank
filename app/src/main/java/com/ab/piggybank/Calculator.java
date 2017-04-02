package com.ab.piggybank;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.piggybank.activity.AddTransactionActivity;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class Calculator extends AppCompatActivity {
    private TextView screen;
    private String display = "";
    private String currentOperator = "";
    private String result = "";
    private double amount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_your_amount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_close);
        upArrow.setColorFilter(Color.parseColor("#424242"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        screen = (TextView) findViewById(R.id.calculator_screen);
        screen.setText(display);

        updateCurrentAmount(getIntent().getDoubleExtra("amount", 0.00));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(this, AddTransactionActivity.class);
            if (getIntent().getExtras() != null) {
                i.putExtras(getIntent().getExtras());
            }
            startActivity(i);
            finish();
        }
        if (id == R.id.done) {
            onDone();
        }
        return false;
    }

    /*** Deprecated
     public void onClick(View v) {
     Button button = (Button) v;
     display += button.getText();

     }*/


    private void updateScreen() {
        screen.setText(display);
    }

    private boolean isOperator(String op) {
        switch (op) {
            case "+":
                return true;

            case "-":
                return true;

            case "÷":
                return true;

            case "×":
                return true;

            default:
                return false;
        }

    }

    public void onClickNumber(View v) {
        if (result != "") {
            clear();
            updateScreen();
        }
        Button button = (Button) v;
        display += button.getText().toString();
        updateScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.calculator_menu, menu);
        return true;
    }

    public void onClickOperator(View v) {
        Button button = (Button) v;
        if (display.length() == 0) return;
        if (isOperator(String.valueOf(display.charAt(display.length() - 1)))) return;
        if (String.valueOf(display.charAt(display.length() - 1)) == ".") return;
        if (display == "") return;
        if (result != "") {
            String _display = result;
            clear();
            display = _display;
        }
        if (currentOperator != "") {
            if (isOperator(String.valueOf(display.charAt(display.length() - 1)))) {
                display.replace(display.charAt(display.length() - 1), button.getText().charAt(0));
            } else {
                getResult();
                display = result;
                result = "";
            }
        }
        display += button.getText();
        currentOperator = button.getText().toString();
        updateScreen();
    }


    private double operate(String a, String b, String op) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        switch (op) {
            case "+":


                return Double.parseDouble(decimalFormat.format(Double.valueOf(a) + Double.valueOf(b)));
            case "-":

                return Double.parseDouble(decimalFormat.format(Double.valueOf(a) - Double.valueOf(b)));
            case "÷":
                try {
                    return Double.parseDouble(decimalFormat.format(Double.valueOf(a) / Double.valueOf(b)));
                } catch (Exception e) {
                    Log.i("Calc", e.getMessage());
                }
            case "×":
                return Double.parseDouble(decimalFormat.format(Double.valueOf(a) * Double.valueOf(b)));
            default:
                return -1;
        }
    }

    public void onClickComma(View v) {
        if (result != "") return;
        if (display.length() == 0) return;
        if (String.valueOf(display.charAt(display.length() - 1)).equals(".")) return;
        if (isOperator(String.valueOf(display.charAt(display.length() - 1)))) return;
        Button button = (Button) v;
        display += button.getText().toString();
        updateScreen();

    }

    private void updateCurrentAmount(double amount) {
        int langID;
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Arabic":
                langID = 1;
                break;
            default:
                langID = 0;
        }
        TextView textView = (TextView) findViewById(R.id.currentAmountView);
        if (langID == 0) {
            textView.setText(getString(R.string.current_amount_is) + " " + amount);
        }
        this.amount = amount;
    }


    public void onClickClear(View v) {
        clear();
        updateScreen();
    }

    private void clear() {
        display = "";
        currentOperator = "";
        result = "";

    }

    private boolean getResult() {
        String[] operation = display.split(Pattern.quote(currentOperator));
        if (operation.length < 2) {
            return false;
        }
        if (currentOperator != "") {
            result = String.valueOf(operate(operation[0], operation[1], currentOperator));
            updateCurrentAmount(Double.parseDouble(result));
            return true;
        } else {
            result = display;
            updateCurrentAmount(Double.parseDouble(result));
            return false;
        }


    }

    public void onClickEqual(View v) {
        if (display.length() == 0) return;
        if (String.valueOf(display.charAt(display.length() - 1)) == ".") return;
        if (display == "") return;
        if (!getResult()) {
            screen.setText(String.valueOf(result));
        } else {
            screen.setText(display + "\n" + String.valueOf(result));
        }
    }

    private void onDone() {
        int langID;
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Arabic":
                langID = 1;
                break;
            default:
                langID = 0;
        }
        if (display.length() == 0) {
            if (langID == 0) {
                Toast.makeText(this, getString(R.string.add_amount_to_proceed) + "\n " + "\uD83D\uDE10", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (String.valueOf(display.charAt(display.length() - 1)) == ".") {
            if (langID == 0) {
                Toast.makeText(this, getString(R.string.complete_ur_operation) + "\n " + "\uD83D\uDE10", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (display == "") {
            if (langID == 0) {
                Toast.makeText(this, getString(R.string.add_amount_to_proceed) + "\n " + "\uD83D\uDE10", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (amount == 0) {
            if (langID == 0) {
                Toast.makeText(this, "The amount must be more than zero." + "\n " + "\uD83D\uDE10", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (amount == getIntent().getDoubleExtra("amount", 0)) {
            if (langID == 0) {
                Toast.makeText(this, "The amount must be different than the previous amount." + "\n " + "\uD83D\uDE10", Toast.LENGTH_LONG).show();
            }
            return;
        }
        Intent i = new Intent(this, AddTransactionActivity.class);
        if (getIntent().getExtras() != null) {
            i.putExtras(getIntent().getExtras());
        }
        i.putExtra("calc", true);
        i.putExtra("amount", amount);
        startActivity(i);
        finish();
    }

}

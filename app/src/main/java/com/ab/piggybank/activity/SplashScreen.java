package com.ab.piggybank.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ab.piggybank.ApiCallerService;
import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.FillCurrencyNameTableService;
import com.ab.piggybank.activity.setup1.setupActivity;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent apiCall = new Intent(this, ApiCallerService.class);
        startService(apiCall);
        int languageID;
        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Arabic":
                languageID = 1;
                break;
            default:
                languageID = 0;
        }
        Intent fillCurrencyTable = new Intent(SplashScreen.this, FillCurrencyNameTableService.class);
        fillCurrencyTable.putExtra("lang", languageID);
        startService(fillCurrencyTable);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!preferences.getBoolean("finishedsetupslide", false)) {
                    Intent i = new Intent(getApplicationContext(), SetupSlideActivity.class);
                    startActivity(i);
                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(SplashScreen.this);
                    if (dbHelper.getMethodTable().getCount() == 0) {
                        Intent i = new Intent(SplashScreen.this, setupActivity.class);
                        startActivity(i);
                    } else if(preferences.getInt("country", 0) == 0) {
                        Intent i = new Intent(SplashScreen.this, ChooseCountryActivity.class);
                        startActivity(i);
                    }
                    else {
                        Intent i = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(i);
                    }
                }


            }
        }, 2000);


    }
}





































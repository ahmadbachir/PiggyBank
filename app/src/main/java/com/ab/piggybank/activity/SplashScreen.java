package com.ab.piggybank.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ab.piggybank.ApiCallerService;
import com.ab.piggybank.DatabaseHelper;
import com.ab.piggybank.activity.setup1.setupActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent apiCall = new Intent(this, ApiCallerService.class);
        startService(apiCall);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(preferences.getInt("country",-1) == -1){
                    if(!preferences.getBoolean("finishedsetupslide",false)){
                        Intent i = new Intent(getApplicationContext(), SetupSlideActivity.class);
                        startActivity(i);
                    }
                    else {
                        DatabaseHelper dbHelper = new DatabaseHelper(SplashScreen.this);
                        if(dbHelper.getMethodTable().getCount() == 0){
                            Intent i = new Intent(SplashScreen.this, setupActivity.class);
                            startActivity(i);
                        }
                        else{

                        }
                    }

                }
            }
        },2000);




    }
}





































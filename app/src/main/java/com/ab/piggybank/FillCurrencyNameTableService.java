package com.ab.piggybank;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


public class FillCurrencyNameTableService extends IntentService {

    public FillCurrencyNameTableService() {
        super("FillCurrencyNameTableService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(FillCurrencyNameTableService.this);
        String[] names = getResources().getStringArray(R.array.currencyName);
        String[] abvS = getResources().getStringArray(R.array.currency_abv);
        if (intent.getIntExtra("lang", 0) == 0) {
            if (dbHelper.getCurrencyDataEng("").getCount() == 0) {
                for (int i = 0; i < names.length; i++) {
                    dbHelper.insertCurrencyDataENG(names[i], abvS[i]);
                }
            }
        }
    }
}

package com.ab.piggybank;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.ab.piggybank.CurrencyApi.ApiCall;
import com.ab.piggybank.CurrencyApi.ApiCaller;
import com.ab.piggybank.CurrencyApi.Quotes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCallerService extends IntentService {
    Context context;

    public ApiCallerService() {
        super("api-call");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final Calendar calendar = Calendar.getInstance();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ApiCallerService.this);
        if (!preferences.getString("datestamp", "null").equals(calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR))) {
            if (isOnline()) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://apilayer.net/api")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                ApiCaller apiCaller = retrofit.create(ApiCaller.class);
                apiCaller.getApiCall().enqueue(new Callback<ApiCall>() {
                    @Override
                    public void onResponse(Call<ApiCall> call, Response<ApiCall> response) {
                        if (response.body().getSuccess()) {
                            Quotes b = response.body().getQuotes();
                            double[] doubles = {b.getAED(),
                                    b.getAFN(),
                                    b.getALL(),
                                    b.getAMD(),
                                    b.getANG(),
                                    b.getAOA(),
                                    b.getARS(),
                                    b.getAUD(),
                                    b.getAWG(),
                                    b.getAZN(),
                                    b.getBAM(),
                                    b.getBBD(),
                                    b.getBDT(),
                                    b.getBGN(),
                                    b.getBHD(),
                                    b.getBIF(),
                                    b.getBMD(),
                                    b.getBND(),
                                    b.getBOB(),
                                    b.getBRL(),
                                    b.getBSD(),
                                    b.getBTC(),
                                    b.getBTN(),
                                    b.getBWP(),
                                    b.getBYR(),
                                    b.getBZD(),
                                    b.getCAD(),
                                    b.getCDF(),
                                    b.getCHF(),
                                    b.getCLF(),
                                    b.getCLP(),
                                    b.getCNY(),
                                    b.getCOP(),
                                    b.getCRC(),
                                    b.getCUC(),
                                    b.getCUP(),
                                    b.getCVE(),
                                    b.getCZK(),
                                    b.getDJF(),
                                    b.getDKK(),
                                    b.getDOP(),
                                    b.getDZD(),
                                    b.getEEK(),
                                    b.getEGP(),
                                    b.getERN(),
                                    b.getETB(),
                                    b.getEUR(),
                                    b.getFJD(),
                                    b.getFKP(),
                                    b.getGBP(),
                                    b.getGEL(),
                                    b.getGHS(),
                                    b.getGIP(),
                                    b.getGMD(),
                                    b.getGNF(),
                                    b.getGTQ(),
                                    b.getHKD(),
                                    b.getHNL(),
                                    b.getHRK(),
                                    b.getHTG(),
                                    b.getHUF(),
                                    b.getIDR(),
                                    b.getIMP(),
                                    b.getINR(),
                                    b.getIQD(),
                                    b.getIRR(),
                                    b.getISK(),
                                    b.getJEP(),
                                    b.getJMD(),
                                    b.getJOD(),
                                    b.getJPY(),
                                    b.getKES(),
                                    b.getKGS(),
                                    b.getKHR(),
                                    b.getKMF(),
                                    b.getKPW(),
                                    b.getKRW(),
                                    b.getKWD(),
                                    b.getKYD(),
                                    b.getKZT(),
                                    b.getLAK(),
                                    b.getLBP(),
                                    b.getLKR(),
                                    b.getLRD(),
                                    b.getLSL(),
                                    b.getLTL(),
                                    b.getLVL(),
                                    b.getLYD(),
                                    b.getMAD(),
                                    b.getMDL(),
                                    b.getMKD(),
                                    b.getMMK(),
                                    b.getMNT(),
                                    b.getMRO(),
                                    b.getMUR(),
                                    b.getMVR(),
                                    b.getMWK(),
                                    b.getMXN(),
                                    b.getMYR(),
                                    b.getMZN(),
                                    b.getNAD(),
                                    b.getNGN(),
                                    b.getNIO(),
                                    b.getNOK(),
                                    b.getNPR(),
                                    b.getNZD(),
                                    b.getOMR(),
                                    b.getPAB(),
                                    b.getPEN(),
                                    b.getPGK(),
                                    b.getPHP(),
                                    b.getPKR(),
                                    b.getPLN(),
                                    b.getPYG(),
                                    b.getQAR(),
                                    b.getRON(),
                                    b.getRSD(),
                                    b.getRUB(),
                                    b.getRWF(),
                                    b.getSAR(),
                                    b.getSBD(),
                                    b.getSCR(),
                                    b.getSDG(),
                                    b.getSEK(),
                                    b.getSGD(),
                                    b.getSLL(),
                                    b.getSRD(),
                                    b.getSTD(),
                                    b.getSVC(),
                                    b.getSYP(),
                                    b.getSZL(),
                                    b.getTHB(),
                                    b.getTJS(),
                                    b.getTMT(),
                                    b.getTND(),
                                    b.getTOP(),
                                    b.getTRY(),
                                    b.getTTD(),
                                    b.getTWD(),
                                    b.getTZS(),
                                    b.getUAH(),
                                    b.getUGX(),
                                    b.getUSD(),
                                    b.getUYU(),
                                    b.getUZS(),
                                    b.getVEF(),
                                    b.getVND(),
                                    b.getVUV(),
                                    b.getWST(),
                                    b.getXPF(),
                                    b.getYER(),
                                    b.getZAR(),
                                    b.getZMW()};
                            DatabaseHelper dbhelper = new DatabaseHelper(ApiCallerService.this);
                            String[] names = ApiCallerService.this.getResources().getStringArray(R.array.currencyName);
                            String[] abv = ApiCallerService.this.getResources().getStringArray(R.array.currency_abv);
                            if (dbhelper.getCurrencyRateAlphabetized().getCount() != 0) {
                                for (int i = 0; i < doubles.length; i++) {
                                    dbhelper.updateCurrencyTable(i + 1, doubles[i]);
                                }

                            } else {
                                for (int i = 0; i < doubles.length; i++) {
                                    dbhelper.insertCurrencyTable(doubles[i], names[i], abv[i]);
                                }
                            }
                            preferences.edit().putString("datestamp", calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR)).apply();
                            preferences.edit().putBoolean("currency_on", true).apply();

                        } else {
                            preferences.edit().putBoolean("currency_on", false).apply();
                            Toast.makeText(ApiCallerService.this, R.string.surver_not_working, Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiCall> call, Throwable t) {
                        preferences.edit().putBoolean("currency_on", false).apply();
                        Toast.makeText(ApiCallerService.this, R.string.surver_not_working, Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                DatabaseHelper dbhelper = new DatabaseHelper(ApiCallerService.this);
                if (dbhelper.getCurrencyRateAlphabetized().getCount() != 0) {
                    preferences.edit().putString("datestamp", calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR)).apply();
                }
                else {
                    preferences.edit().putBoolean("currency_on", false).apply();
                    Toast.makeText(ApiCallerService.this, R.string.no_internet_in_session, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}

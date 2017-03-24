package com.ab.piggybank;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ab.piggybank.CurrencyApi.ApiCall;
import com.ab.piggybank.CurrencyApi.ApiCaller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ahmad-PC on 3/25/2017.
 */

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

            }

            @Override
            public void onFailure(Call<ApiCall> call, Throwable t) {

            }
        });

    }
}

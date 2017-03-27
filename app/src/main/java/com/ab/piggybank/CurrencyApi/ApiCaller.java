package com.ab.piggybank.CurrencyApi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiCaller {
    @GET("live?access_key=68dc20f399d49abe51aad7f00525777a&currencies=&source=USD&format=1")
    Call<ApiCall> getApiCall();

}

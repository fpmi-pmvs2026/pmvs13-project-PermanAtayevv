package com.perman.habittracker.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApi {
    @GET("api/1.0/?method=getQuote&format=json&lang=ru")
    Call<Quote> getRandomQuote();
}
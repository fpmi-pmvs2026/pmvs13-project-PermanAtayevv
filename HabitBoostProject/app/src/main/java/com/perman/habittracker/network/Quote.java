package com.perman.habittracker.network;

import com.google.gson.annotations.SerializedName;

public class Quote {
    // Говорим библиотеке, что в JSON это поле называется "quoteText"
    @SerializedName("quoteText")
    public String quote;

    // А это поле называется "quoteAuthor"
    @SerializedName("quoteAuthor")
    public String author;
}
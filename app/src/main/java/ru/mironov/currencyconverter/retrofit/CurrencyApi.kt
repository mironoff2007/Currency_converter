package ru.mironov.currencyconverter.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface CurrencyApi {

    @GET("latest")
    fun getRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
    ): Call<JsonObject?>
}

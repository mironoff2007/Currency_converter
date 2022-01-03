package ru.mironov.currencyconverter.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface CurrencyApi {

    @GET("latest")
    fun getRates(
        @Query("apikey") apiKey: String
    ): Call<JsonRates?>

    @GET("latest")
    fun getRatesBySpecific(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
    ): Call<JsonRates?>

    @GET("historical")
    fun getHistory(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
        @Query("date_from") date_from: String,
        @Query("date_to") date_to: String,
    ): Call<JsonHistory?>
}

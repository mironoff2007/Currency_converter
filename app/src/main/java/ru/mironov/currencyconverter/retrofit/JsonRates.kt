package ru.mironov.currencyconverter.retrofit

import com.google.gson.annotations.SerializedName
import java.util.*

class JsonRates {
    @SerializedName("query")
    private var query: JsonQuery? = null

    @SerializedName("data")
    private var rates: TreeMap<String, Double> = TreeMap()

    fun getRates(): TreeMap<String, Double> {
        return rates
    }

    fun getBaseCurrency(): String? {
        return query?.getBaseCurrency()
    }
}



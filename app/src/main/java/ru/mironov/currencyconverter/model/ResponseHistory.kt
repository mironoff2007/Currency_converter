package ru.mironov.currencyconverter.model

import com.google.gson.annotations.SerializedName
import java.util.*

class ResponseHistory {
    @SerializedName("query")
    private var query: JsonQuery? = null

    @SerializedName("data")
    private var data: TreeMap<Date, TreeMap<String, Double>> = TreeMap()

    private var rates: TreeMap<String, Double> = TreeMap()

    fun getRates(): TreeMap<String, Double> {
        return rates
    }

    fun getData(): TreeMap<Date, TreeMap<String, Double>>  {
        return data
    }

    fun getBaseCurrency(): String? {
        return query?.getBaseCurrency()
    }
}



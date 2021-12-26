package ru.mironov.currencyconverter.retrofit

import com.google.gson.annotations.SerializedName
import java.util.*

class JsonObject {
    @SerializedName("query")
    private var query: Query? = null

    @SerializedName("data")
    private var rates: TreeMap<String, Double> = TreeMap()

    fun getRates(): TreeMap<String, Double> {
        return rates
    }

    fun getBaseCurrency(): String? {
        return query?.getBaseCurrency()
    }
}

class Query {
    @SerializedName("base_currency")
    private var baseCurrency: String? = null

    fun getBaseCurrency():String?{
        return baseCurrency
    }
}

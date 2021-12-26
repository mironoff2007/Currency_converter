package ru.mironov.currencyconverter.retrofit

import com.google.gson.annotations.SerializedName

class JsonObject {
    @SerializedName("query")
    private var query: Query? = null

    @SerializedName("data")
    private var rates: HashMap<String, Double> = HashMap()

    fun getRates(): HashMap<String, Double> {
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

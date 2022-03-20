package ru.mironov.currencyconverter.model

import com.google.gson.annotations.SerializedName
import java.util.*

class ResponseRates {
    @SerializedName("last_updated_at")
    private var lastUpdatedAt: String? = null

    @SerializedName("data")
    private var rates: TreeMap<String, CurrencyRate> = TreeMap()

    fun getRates(): TreeMap<String, CurrencyRate> {
        return rates
    }

    fun getBaseCurrency(): String? {
        var name:String?=null
        rates.forEach { (key, value) ->
            if (value.rate.toInt() == 1) {
                name = value.name
                return@forEach
            }
        }
        return name
    }
}



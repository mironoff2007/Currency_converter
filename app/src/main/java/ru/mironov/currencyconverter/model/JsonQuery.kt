package ru.mironov.currencyconverter.model

import com.google.gson.annotations.SerializedName

class JsonQuery {
    @SerializedName("base_currency")
    private var baseCurrency: String? = null

    fun getBaseCurrency():String?{
        return baseCurrency
    }
}
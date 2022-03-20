package ru.mironov.currencyconverter.model

import com.google.gson.annotations.SerializedName

class CurrencyRate(
    @SerializedName("code")
    var name: String,
    @SerializedName("value")
    var rate: Double
) : Cloneable {
    public override fun clone(): Any {
        return CurrencyRate(this.name, this.rate)
    }
}

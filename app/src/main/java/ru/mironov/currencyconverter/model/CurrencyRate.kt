package ru.mironov.currencyconverter.model

class CurrencyRate(
    var name: String,
    var rate: Double
) : Cloneable {
    public override fun clone(): Any {
        return CurrencyRate(this.name, this.rate)
    }
}

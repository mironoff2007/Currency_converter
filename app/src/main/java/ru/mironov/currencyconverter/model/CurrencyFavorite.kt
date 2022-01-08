package ru.mironov.currencyconverter.model



class CurrencyFavorite(
    var name: String?,
    var isFavorite: Boolean
) : Cloneable {

    public override fun clone(): Any {
        return CurrencyFavorite(this.name, this.isFavorite)
    }
}

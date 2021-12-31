package ru.mironov.currencyconverter.model

import java.util.*

data class CurrencyHistory(
    var nameBaseCurrency:String,
    var date:Date,
    var rate:Double
)

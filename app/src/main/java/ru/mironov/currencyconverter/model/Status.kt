package ru.mironov.currencyconverter.model

sealed class Status {
    class LOADING:Status()
    data class ERROR(var message:String,var code:Int) :Status()
    class DATA:Status()
}

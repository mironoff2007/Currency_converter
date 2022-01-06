package ru.mironov.currencyconverter.model

import java.util.*

sealed class Status {
    class LOADING:Status()
    data class ERROR(var message:String,var code:Int) :Status()
    class DATA(val someData:List<Objects>?):Status()
}

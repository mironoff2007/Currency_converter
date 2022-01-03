package ru.mironov.currencyconverter.model

import okhttp3.ResponseBody
import ru.mironov.currencyconverter.retrofit.ErrorBodyParser

sealed class Status {
    class LOADING:Status()
    data class ERROR(var message:String) :Status()
    class DATA:Status()
}

package ru.mironov.currencyconverter.repository

import ru.mironov.currencyconverter.retrofit.CurrencyApi
import javax.inject.Inject


class Repository @Inject constructor (var encryptedDataShared: EncryptedDataShared, var retrofit: CurrencyApi) {

    val API_KEY="API_KEY"

    fun isApiKeySaved():Boolean{
        return !encryptedDataShared.getString(API_KEY).isNullOrBlank()
    }

    fun setApiKey(key:String){
        encryptedDataShared.saveString(key,API_KEY)
    }
}
package ru.mironov.currencyconverter.repository

import javax.inject.Inject


class Repository @Inject constructor (var dataShared: DataShared) {

    val API_KEY="API_KEY"

    fun isApiKeySaved():Boolean{
        return !dataShared.getString(API_KEY).isNullOrBlank()
    }

    fun setApiKey(key:String){
        dataShared.saveString(key,API_KEY)
    }
}
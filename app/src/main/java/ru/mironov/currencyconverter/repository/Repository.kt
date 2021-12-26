package ru.mironov.currencyconverter.repository

import ru.mironov.currencyconverter.repository.DataShared


import javax.inject.Inject

class Repository {

    val API_KEY="API_KEY"

    @Inject
    lateinit var data: DataShared

    fun isApiKeySaved():Boolean{
        return !data.getString(API_KEY).isNullOrBlank()
    }

    fun setApiKey(key:String){
        data.saveString(key,API_KEY)
    }
}
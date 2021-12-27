package ru.mironov.currencyconverter.repository


import retrofit2.Call
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonRates
import javax.inject.Inject

class Repository @Inject constructor (var encryptedDataShared: EncryptedDataShared, var retrofit: CurrencyApi) {

    private val API_KEY="API_KEY"

    fun isApiKeySaved():Boolean{
        return !encryptedDataShared.getString(API_KEY).isNullOrBlank()
    }

    fun setApiKey(key:String){
        encryptedDataShared.saveString(key,API_KEY)
    }

    fun getApiKey():String?{
        return encryptedDataShared.getString(API_KEY)
    }

    fun getObjectFromNetwork(name:String): Call<JsonRates?>? {
       return  retrofit.getRates(getApiKey().toString(),name)
    }
}
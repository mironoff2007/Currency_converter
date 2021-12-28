package ru.mironov.currencyconverter.repository


import retrofit2.Call
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonRates
import javax.inject.Inject

class Repository @Inject constructor (var dataShared: DataShared,var encryptedDataShared: EncryptedDataShared, var retrofit: CurrencyApi) {

    companion object {
    private const val API_KEY="API_KEY"
    }

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

    fun saveCurrenciesNames(arrayNames: ArrayList<String>) {
        //val n =dataShared.getCurrenciesNumber()
            dataShared.saveCurrenciesNames(arrayNames)
    }

    fun getCurrenciesNames(): ArrayList<String> {
        return dataShared.getCurrenciesNames()
    }
}
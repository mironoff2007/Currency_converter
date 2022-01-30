package ru.mironov.currencyconverter.repository

import retrofit2.Call
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonHistory
import ru.mironov.currencyconverter.retrofit.JsonRates
import javax.inject.Inject

open class Repository @Inject constructor (protected var dataShared: DataShared, protected var encryptedDataShared: EncryptedDataShared, var retrofit: CurrencyApi) {

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

    fun getRatesBaseSpecificFromNetwork(name:String): Call<JsonRates?>? {
        return  retrofit.getRatesBySpecific(getApiKey().toString(),name)
    }

    fun getRatesFromNetwork(apiKey:String): Call<JsonRates?>? {
        return  retrofit.getRates(apiKey)
    }

    fun getHistoryFromNetwork(name:String,dateFrom:String,dateTo:String): Call<JsonHistory?>? {
        return  retrofit.getHistory(getApiKey().toString(),name,dateFrom,dateTo)
    }

    fun saveCurrenciesNames(arrayNames: ArrayList<String>) {
        dataShared.saveCurrenciesNames(arrayNames)
    }

    fun getCurrenciesNames(): ArrayList<String> {
        return dataShared.getCurrenciesNames()
    }

    fun getFavoriteCurrencies(): ArrayList<CurrencyFavorite>? {
        return dataShared.getFavoriteCurrencies()
    }

    fun saveFavoriteCurrencies(currencies:ArrayList<CurrencyFavorite>) {
        return dataShared.saveFavoriteCurrencies(currencies)
    }

    fun getFavoriteCurrenciesNames(): ArrayList<String> {
        val list=ArrayList<String>()

        getFavoriteCurrencies()?.forEach(){
            if(it.name!=null&&it.isFavorite){
                list.add(it.name!!)}
        }

        return list
    }

    fun getSelectedCurrency():String?{
        return dataShared.getSelectedCurrency()
    }

    fun saveSelectedCurrency(name:String){
        dataShared.saveSelectedCurrency(name)
    }
}
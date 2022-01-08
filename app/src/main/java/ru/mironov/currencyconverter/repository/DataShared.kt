package ru.mironov.currencyconverter.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.mironov.currencyconverter.model.CurrencyFavorite

class DataShared(context: Context, name: String) {

    companion object {
        private const val NUMBER_OF_CURRENCIES = "NUMBER_OF_CURRENCIES"
        private const val CURRENCY_NAME_KEY = "CURRENCY_NAME_KEY_"
        private const val CURRENCIES_FAVORITE_KEY = "CURRENCIES_FAVORITE_KEY"
    }

    private val pref: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun saveFavoriteCurrencies(favoriteList: ArrayList<CurrencyFavorite>) {
        val gson = Gson()
        val gsonString = gson.toJson(favoriteList)
        editor.putString(CURRENCIES_FAVORITE_KEY, gsonString).apply()
    }

    fun getFavoriteCurrencies(): ArrayList<CurrencyFavorite>? {
        val gson = Gson()
        val gsonString = pref.getString(CURRENCIES_FAVORITE_KEY, "")

        return gson.fromJson(
            gsonString,
            object : TypeToken<ArrayList<CurrencyFavorite?>?>() {}.type
        )
    }

    fun saveCurrenciesNames(names: ArrayList<String>) {
        editor.putInt(NUMBER_OF_CURRENCIES, names.size).apply()
        var i = 0
        names.sort()
        names.forEach() {
            editor.putString(CURRENCY_NAME_KEY + i, it).apply()
            i++
        }
    }

    fun getCurrenciesNumber(): Int {
        return pref.getInt(NUMBER_OF_CURRENCIES, 0)
    }

    fun getCurrenciesNames(): ArrayList<String> {
        val arrayNames = ArrayList<String>()
        val n = pref.getInt(NUMBER_OF_CURRENCIES, 0) - 1
        for (i in 0..n) {
            arrayNames.add(pref.getString(CURRENCY_NAME_KEY + i, "")!!)
        }
        return arrayNames
    }

    fun clearPrefs() {
        editor.clear().commit()
    }


}
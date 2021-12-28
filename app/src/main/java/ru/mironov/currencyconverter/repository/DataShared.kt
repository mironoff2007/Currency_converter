package ru.mironov.currencyconverter.repository

import android.content.Context
import android.content.SharedPreferences

class DataShared(context: Context, name: String) {

    companion object {
        private const val NUMBER_OF_CURRENCIES = "NUMBER_OF_CURRENCIES"
        private const val CURRENCY_NAME_KEY = "CURRENCY_NAME_KEY_"
    }

    private val pref: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun saveCurrenciesNames(names: ArrayList<String>) {
        editor.putInt(Companion.NUMBER_OF_CURRENCIES, names.size).apply()
        var i = 0
        names.forEach() {
            editor.putString(CURRENCY_NAME_KEY + i, it)
            i++
        }
    }

    fun getCurrenciesNames(): ArrayList<String> {
        val arrayNames = ArrayList<String>()
        val n = pref.getInt(Companion.NUMBER_OF_CURRENCIES, 0)
        for (i in 0..n) {
            arrayNames.add(pref.getString(CURRENCY_NAME_KEY + i, "")!!)
        }
        return arrayNames
    }

    fun clearPrefs() {
        editor.clear().commit()
    }


}
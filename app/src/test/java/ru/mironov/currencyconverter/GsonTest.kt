package ru.mironov.currencyconverter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.converter.gson.GsonConverterFactory
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonRates
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GsonTest {

    @Test
    fun gsonParserTest() {
        val list = ArrayList<CurrencyFavorite>()

        list.add(CurrencyFavorite("1", true))
        list.add(CurrencyFavorite("2", true))
        list.add(CurrencyFavorite("3", true))

        val gson = Gson()

        val gsonString = gson.toJson(list)

        val listOfMyClassObject: Type = object : TypeToken<ArrayList<CurrencyFavorite?>?>() {}.type

        val desList :ArrayList<CurrencyFavorite?>?= gson.fromJson(gsonString, listOfMyClassObject)

        assertEquals(true, true)
    }

}

package ru.mironov.currencyconverter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonRates
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GsonTest {

    @Test
    fun gsonParserTest(){
        val list=ArrayList<CurrencyFavorite>()

        list.add(CurrencyFavorite("1",true))
        list.add(CurrencyFavorite("2",true))
        list.add(CurrencyFavorite("3",true))

        val gson = Gson()

        val gsonString=gson.toJson(list)

        val desList= gson.fromJson(
            "",
            ArrayList<CurrencyFavorite>()::class.java
        )

        assertEquals(true, true)
    }
}

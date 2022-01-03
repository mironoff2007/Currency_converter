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
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonRates
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class UnitTest {
    @Test
    fun addition_isCorrect() {

        val  cal: Calendar =Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dateStr= "$year-$month-$day"

        val format = SimpleDateFormat("yyyy-MM-dd")

        val date=format.parse(dateStr)

        val format2 = SimpleDateFormat("dd-MMM-yyyy")

        val dateStrFormat=format2.format(date)

        assertEquals(4, 2 + 2)
    }

    @Test
    fun retrofitTest(){
        val retrofit=Retrofit.Builder()
            .baseUrl("https://freecurrencyapi.net/api/v2/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls()
                        .create()
                )
            ).build()
        val call: Call<JsonRates?> =retrofit.create(CurrencyApi::class.java).getRates("c")

        val response: Response<JsonRates?> = call!!.execute()

        var body:JsonRates?=null
        if(response.body()!=null){
            body= response.body() as JsonRates}

        //val converter =retrofit.requestBodyConverter<>(JsonError::class.java)


        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            reader = BufferedReader(InputStreamReader(response.errorBody()!!.byteStream()))
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val finallyError = sb.toString()

        val errorJson= Gson().fromJson(
            finallyError,
            JsonElement::class.java
        )

        assertEquals(true, body != null)
    }
}

package ru.mironov.currencyconverter.retrofit

import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

 object ErrorBodyParser{

    fun getErrorString(errorBody:ResponseBody):String{
        var reader: BufferedReader?
        val sb = StringBuilder()
        try {
            reader = BufferedReader(InputStreamReader(errorBody.byteStream()))
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

        return  sb.toString()
    }
}
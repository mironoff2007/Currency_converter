package ru.mironov.currencyconverter.retrofit

import android.content.Context
import okhttp3.ResponseBody
import ru.mironov.currencyconverter.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object ErrorUtil {

    fun parseError(errorBody: ResponseBody): String {
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

        return sb.toString()
    }

    fun getErrorMessage(context: Context, code: Int): String {

        var message = ""
        when (code) {
            429 -> {
                message = context.getString(R.string.hit_rate_limit_error_message)
            }
        }
        return message

    }
}
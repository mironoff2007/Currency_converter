package ru.mironov.currency_converter.util

import android.util.Log
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.roundToInt

object FormatNumbers {

    fun getDoubleFromText(text: String): Double {
        var value = 0.0
        try {
            value = text.toDouble()
        } catch (e: NumberFormatException) {
            Log.d("tag",e.toString())
        }
        return value
    }
    fun formatDoubleToString(numb: Double,locale:Locale): String {
        if (numb > 99.99) {
            return numb.roundToInt().toString()
        } else {
            return DecimalFormat("#.##", DecimalFormatSymbols(locale)).format(numb)
        }
    }
}
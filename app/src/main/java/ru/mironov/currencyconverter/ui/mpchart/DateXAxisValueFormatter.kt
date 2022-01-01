package ru.mironov.currencyconverter.ui.mpchart

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateXAxisValueFormatter : IndexAxisValueFormatter() {

    companion object {
        private const val GRAPH_DATE_FORMAT = "dd.MM.yy"
    }

    override fun getFormattedValue(value: Float): String? {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        val emissionsMilliSince1970Time = value.toLong()

        // Show time in local version
        val timeMilliseconds = Date(emissionsMilliSince1970Time)
        val dateTimeFormat: DateFormat =
            SimpleDateFormat(GRAPH_DATE_FORMAT)
        return dateTimeFormat.format(timeMilliseconds)
    }
}
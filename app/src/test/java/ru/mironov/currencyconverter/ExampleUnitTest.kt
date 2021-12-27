package ru.mironov.currencyconverter

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
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




}

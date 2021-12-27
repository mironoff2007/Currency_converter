package ru.mironov.currencyconverter.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DateDeserializer : JsonDeserializer<Date?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement?,
        typeOF: Type?,
        context: JsonDeserializationContext?
    ): Date? {
        if (jsonElement == null) return null
        val dateStr = jsonElement.asString
        try {
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).parse(dateStr)
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return null
    }
}
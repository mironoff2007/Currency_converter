package ru.mironov.currencyconverter.util

import android.annotation.SuppressLint
import android.widget.ImageView
import java.util.*

object FlagSetter {
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setFlag(name: String, imageView: ImageView) {
        //Lib flags names have 2 characters, but API have 3
        if (name.length > 2) {
            val curName = name.toLowerCase(Locale.ENGLISH)
            val id=imageView.context.resources.getIdentifier(
                curName,
                "drawable",
                imageView.context.packageName
            )
            if(id!=0){
                val drawable = imageView.context.getDrawable(id)
                imageView.setImageDrawable(drawable)
            }
        }
    }
}
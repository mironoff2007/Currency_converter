package ru.mironov.currencyconverter.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import ru.mironov.currencyconverter.R
import java.util.*

object FlagSetter {
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setFlag(name: String, imageView: ImageView) {
        //Lib flags names have 2 characters, but API have 3
        if (name.length > 2) {
            val curName = "ic_"+name.toLowerCase(Locale.ENGLISH)
            val id=imageView.context.resources.getIdentifier(
                curName,
                "drawable",
                imageView.context.packageName
            )

            var drawable:Drawable? = if(id==0){
                imageView.context.getDrawable(R.drawable.ic_coin)
            } else {
                imageView.context.getDrawable(id)
            }

            imageView.setImageDrawable(drawable)
        }
    }
}
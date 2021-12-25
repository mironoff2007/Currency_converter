package ru.mironov.currencyconverter

import android.content.Context
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ViewModelSetKeyFragment @Inject constructor() : ViewModel() {
//val context: Context
    @Inject
    lateinit var myClass:MyClass

    init{

    }

}
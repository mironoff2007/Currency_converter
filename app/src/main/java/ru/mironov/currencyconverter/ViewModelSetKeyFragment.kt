package ru.mironov.currencyconverter

import android.content.Context
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ViewModelSetKeyFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    lateinit var myClass:MyClass

    init{

    }

}
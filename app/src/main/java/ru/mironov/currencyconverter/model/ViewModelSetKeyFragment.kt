package ru.mironov.currencyconverter.model

import android.content.Context
import androidx.lifecycle.ViewModel
import ru.mironov.currencyconverter.repository.Repository
import javax.inject.Inject

class ViewModelSetKeyFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    lateinit var repository:Repository


    fun isApiKeySaved():Boolean{
        return repository.isApiKeySaved()
    }

    fun setApiKey(key:String){
        repository.setApiKey(key)
    }

}
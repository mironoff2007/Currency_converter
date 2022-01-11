package ru.mironov.currencyconverter.model

import androidx.lifecycle.ViewModel
import ru.mironov.currencyconverter.repository.Repository
import javax.inject.Inject

open class ViewModelMainActivity @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var repository:Repository


    fun isApiKeySaved():Boolean{
        return repository.isApiKeySaved()
    }

}
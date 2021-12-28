package ru.mironov.currencyconverter.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.retrofit.JsonRates
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.collections.ArrayList

class ViewModelGraphFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    lateinit var repository:Repository

    fun getCurrenciesNames(): ArrayList<String> {
        return repository.getCurrenciesNames()
    }


}
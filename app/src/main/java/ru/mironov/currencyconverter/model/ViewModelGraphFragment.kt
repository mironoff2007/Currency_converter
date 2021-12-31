package ru.mironov.currencyconverter.model

import android.content.Context
import android.util.Log
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
import ru.mironov.currencyconverter.retrofit.JsonHistory
import javax.inject.Inject
import kotlin.collections.ArrayList

class ViewModelGraphFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    lateinit var repository: Repository

    val arrayHistory = ArrayList<CurrencyHistory>()

    var mutableStatus = MutableLiveData<Status>()

    private var ratesObject: JsonHistory? = null

    fun getCurrenciesNames(): ArrayList<String> {
        return repository.getCurrenciesNames()
    }

    fun getCurrencyHistory(
        nameBaseCurrency: String,
        nameConvCur: String,
        dateFrom: String,
        dateTo: String
    ) {
        mutableStatus.postValue(Status.LOADING)
        repository.getHistoryFromNetwork(nameBaseCurrency, dateFrom, dateTo)!!
            .enqueue(object : Callback<JsonHistory?> {
                override fun onResponse(
                    call: Call<JsonHistory?>,
                    response: Response<JsonHistory?>
                ) {
                    if (response.body() != null) {
                        viewModelScope.launch(Dispatchers.Default) {
                            ratesObject = response.body()

                            if (arrayHistory.isEmpty()) {
                                val arrayNames = ArrayList<String>()

                                //responseCurrency = ratesObject?.getBaseCurrency().toString()

                                //Set currencies to convert to
                                ratesObject?.getData()?.forEach { it ->
                                    arrayHistory.add(
                                        CurrencyHistory(
                                            ratesObject?.getBaseCurrency().toString(),
                                            it.key,
                                            it.value[nameConvCur] ?: 0.0
                                        )
                                    )
                                }

                            }
                            mutableStatus.postValue(Status.DATA)
                        }
                    } else {
                        mutableStatus.postValue(Status.ERROR)
                    }
                }

                override fun onFailure(call: Call<JsonHistory?>, t: Throwable) {
                    mutableStatus.postValue(Status.ERROR)
                }
            })
    }

}
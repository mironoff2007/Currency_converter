package ru.mironov.currencyconverter.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mironov.currencyconverter.repository.Repository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mironov.currencyconverter.retrofit.ErrorUtil
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
        return repository.getCurrenciesNames().clone() as ArrayList<String>
    }

    fun getCurrencyHistory(
        nameBaseCurrency: String,
        nameConvCur: String,
        dateFrom: String,
        dateTo: String
    ) {
        mutableStatus.postValue(Status.LOADING())
        repository.getHistoryFromNetwork(nameBaseCurrency, dateFrom, dateTo)!!
            .enqueue(object : Callback<JsonHistory?> {
                override fun onResponse(
                    call: Call<JsonHistory?>,
                    response: Response<JsonHistory?>
                ) {
                    if (response.body() != null) {
                        viewModelScope.launch(Dispatchers.Default) {
                            ratesObject = response.body()

                            arrayHistory.clear()

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
                            mutableStatus.postValue(Status.DATA())
                        }
                    } else {
                        if (response.errorBody() != null) {
                            mutableStatus.postValue(
                                Status.ERROR(
                                    ErrorUtil.parseError(response.errorBody()!!),
                                    response.raw().code()

                                )
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<JsonHistory?>, t: Throwable) {
                    mutableStatus.postValue(Status.ERROR(t.message.toString(),0))
                }
            })
    }

}
package ru.mironov.currencyconverter.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.retrofit.JsonObject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.collections.ArrayList

class ViewModelCurrenciesFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    lateinit var repository:Repository

    var mutableStatus = MutableLiveData<Status>()

    private var ratesObject: JsonObject? = null

    var responseCurrency: String? = null

    val arrayRates = ArrayList<CurrencyRate>()

    fun getCurrencyRate(name: String) {
        mutableStatus.postValue(Status.LOADING)
        repository.getObjectFromNetwork(name)
            ?.enqueue(object : Callback<JsonObject?> {
                override fun onResponse(
                    call: Call<JsonObject?>,
                    response: Response<JsonObject?>
                ) {
                    if (response.body() != null) {
                        viewModelScope.launch(Dispatchers.Default) {
                            ratesObject = response.body()

                            if (arrayRates.isEmpty()) {
                                responseCurrency = ratesObject?.getBaseCurrency().toString()

                                //First currency to convert from
                                arrayRates.add(
                                    CurrencyRate(
                                        ratesObject?.getBaseCurrency().toString(),
                                        1.0
                                    )
                                )
                                //Set currencies to convert to

                                ratesObject?.getRates()?.forEach { cur ->
                                    arrayRates.add(CurrencyRate(cur.key, cur.value))
                                }
                            } else {
                                synchronized(arrayRates) {
                                    arrayRates.forEach { it ->
                                        val rate = ratesObject?.getRates()?.get(it.name)
                                        if (rate != null) {
                                            it.rate = rate
                                        }
                                    }
                                }
                            }
                            mutableStatus.postValue(Status.DATA)
                        }
                    } else {
                        mutableStatus.postValue(Status.ERROR)
                    }
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    mutableStatus.postValue(Status.ERROR)
                }
            })
    }

    fun swap(pos: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            synchronized(arrayRates) {
                val convRate = arrayRates[pos].rate

                responseCurrency = arrayRates[pos].name

                val tempCur = arrayRates[pos]
                arrayRates.removeAt(pos)
                arrayRates.add(0, tempCur)

                arrayRates.forEach {
                    it.rate = it.rate / convRate
                }
            }
        }
    }
}
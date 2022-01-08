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
import ru.mironov.currencyconverter.retrofit.ErrorUtil
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

open class ViewModelCurrenciesFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    protected lateinit var repository: Repository

    var mutableStatus = MutableLiveData<Status>()

    private var ratesObject: JsonRates? = null

    var responseCurrency: String? = null

    private val arrayRates = ArrayList<CurrencyRate>()

    fun isRatesEmpty(): Boolean {
        return arrayRates.isEmpty()
    }

    fun getCurrencyRate(name: String) {
        mutableStatus.postValue(Status.LOADING())
        repository.getRatesBaseSpecificFromNetwork(name)
            ?.enqueue(object : Callback<JsonRates?> {
                override fun onResponse(
                    call: Call<JsonRates?>,
                    response: Response<JsonRates?>
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

                                //get Favorite
                                val favorite = repository.getFavoriteCurrenciesNames()

                                //Set currencies to convert to
                                ratesObject?.getRates()?.forEach { cur ->
                                    //IsFavorite and not base currency
                                    if (cur.key != ratesObject?.getBaseCurrency()
                                            .toString() && favorite.contains(cur.key)
                                    ) {
                                        arrayRates.add(CurrencyRate(cur.key, cur.value))
                                    }
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
                            val linkedArray = LinkedList<CurrencyRate>()

                            arrayRates.forEach() {
                                linkedArray.add(it.clone() as CurrencyRate)
                            }

                            mutableStatus.postValue(Status.DATA(linkedArray as LinkedList<Objects>))
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

                override fun onFailure(call: Call<JsonRates?>, t: Throwable) {
                    mutableStatus.postValue(Status.ERROR(t.message.toString(), 0))
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

    fun calculateCurrencies(value: Double) {
        if (arrayRates.isNotEmpty()) {
            val changedRates = LinkedList<CurrencyRate>()

            arrayRates.forEach { it ->
                changedRates.add(CurrencyRate(it.name, it.rate * value))
            }
            changedRates[0].rate = value
            mutableStatus.postValue(Status.DATA(changedRates as LinkedList<Objects>))
        }
    }

    fun getFavoriteCurrencies(): ArrayList<CurrencyFavorite>? {
        return repository.getFavoriteCurrencies()
    }

    fun saveFavoriteCurrencies(favoriteCurrencies: ArrayList<CurrencyFavorite>) {
        repository.saveFavoriteCurrencies(favoriteCurrencies)
    }
}
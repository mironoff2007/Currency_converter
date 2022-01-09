package ru.mironov.currencyconverter.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.retrofit.ErrorUtil
import ru.mironov.currencyconverter.retrofit.JsonRates
import javax.inject.Inject

open class ViewModelSetKeyFragment @Inject constructor(val context: Context) : ViewModel() {

    @Inject
    protected lateinit var repository:Repository

    var mutableStatus = MutableLiveData<Status>()

    private var ratesObject: JsonRates? = null

    fun saveNames(arrayNames: ArrayList<String>){
        repository.saveCurrenciesNames(arrayNames)

        val list=ArrayList<CurrencyFavorite>()
        arrayNames.forEach(){
            list.add(CurrencyFavorite(it,true))
        }
        repository.saveFavoriteCurrencies(list)
    }

    fun getCurrencyRate(apiKey: String) {
        mutableStatus.postValue(Status.LOADING)
        repository.getRatesFromNetwork(apiKey)
            ?.enqueue(object : Callback<JsonRates?> {
                override fun onResponse(
                    call: Call<JsonRates?>,
                    response: Response<JsonRates?>
                ) {
                    if(response.body() != null){

                        ratesObject = response.body()

                        val currenciesNames = ArrayList<String>()

                        ratesObject?.getBaseCurrency()?.let { currenciesNames.add(it) }

                        ratesObject?.getRates()?.forEach(){
                            currenciesNames.add(it.key)
                        }
                        saveNames(currenciesNames)

                        mutableStatus.postValue(Status.DATA(null))

                    } else {
                        if (response.errorBody()!=null){
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
                    mutableStatus.postValue(Status.ERROR(t.message.toString(),0))
                }
            })
    }

    fun setApiKey(apiKey: String) {
        repository.setApiKey(apiKey)
    }
}
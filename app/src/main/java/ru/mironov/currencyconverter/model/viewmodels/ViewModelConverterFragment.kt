package ru.mironov.currencyconverter.model.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.model.ResponseRates
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.retrofit.ErrorUtil
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

open class ViewModelConverterFragment @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var repository: Repository

    var mutableStatus = MutableLiveData<Status>()

    private var ratesObject: ResponseRates? = null

    var responseCurrency: String? = null

    private val arrayRates = ArrayList<CurrencyRate>()

    fun isRatesEmpty(): Boolean {
        return arrayRates.isEmpty()
    }

    fun getCurrencyRate(name: String) {
        mutableStatus.postValue(Status.LOADING)
        repository.getRatesBaseSpecificFromNetwork(name)
            ?.enqueue(object : Callback<ResponseRates?> {
                override fun onResponse(
                    call: Call<ResponseRates?>,
                    response: Response<ResponseRates?>
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
                                        arrayRates.add(CurrencyRate(cur.key, cur.value.rate))
                                    }
                                }
                            } else {
                                synchronized(arrayRates) {
                                    arrayRates.forEach { it ->
                                        val currency = ratesObject?.getRates()?.get(it.name)
                                        if (currency != null) {
                                            it.rate = currency.rate
                                        }
                                    }
                                }
                            }
                            val array = ArrayList<CurrencyRate>()

                            arrayRates.forEach() {
                                array.add(it.clone() as CurrencyRate)
                            }

                            mutableStatus.postValue(Status.RESPONSE)
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

                override fun onFailure(call: Call<ResponseRates?>, t: Throwable) {
                    mutableStatus.postValue(Status.ERROR(t.message.toString(), 0))
                }
            })
    }

    fun swap(pos: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            synchronized(arrayRates) {
                val convRate = arrayRates[pos].rate

                Collections.swap(arrayRates,0,pos)

                arrayRates.forEach {
                    it.rate = it.rate / convRate
                }
            }
        }
    }

    fun calculateCurrencies(value: Double) {
        if (arrayRates.isNotEmpty()) {
            val changedRates = ArrayList<CurrencyRate>()

            arrayRates.forEach { it ->
                changedRates.add(CurrencyRate(it.name, it.rate * value))
            }
            changedRates[0].rate = value
            mutableStatus.postValue(Status.DATA(changedRates as ArrayList<Objects>))
        }
    }

    fun getFavoriteCurrencies(): ArrayList<CurrencyFavorite>? {
        return repository.getFavoriteCurrencies()
    }

    fun saveFavoriteCurrencies(favoriteCurrencies: ArrayList<CurrencyFavorite>) {
        repository.saveFavoriteCurrencies(favoriteCurrencies)
    }

    fun getFirstFavorite(): String? {
        getFavoriteCurrencies()?.forEach(){
            if(it.isFavorite){
                return it.name
            }
        }
        return ""
    }

    fun isInFavorite(name:String): Boolean {
        getFavoriteCurrencies()!!.forEach(){
            if(it.isFavorite){
                if(it.name==name){
                    return true
                }
            }
        }
        return false
    }

    fun getSelectedCurrency():String?{
        return repository.getSelectedCurrency()
    }

    fun saveSelectedCurrency(name:String){
        repository.saveSelectedCurrency(name)
    }
}
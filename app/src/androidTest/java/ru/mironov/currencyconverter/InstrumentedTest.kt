package ru.mironov.currencyconverter

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Response
import ru.mironov.currencyconverter.repository.EncryptedDataShared
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.model.ResponseRates
import javax.inject.Inject
import org.junit.Before
import ru.mironov.currencyconverter.di.DaggerTestAppComponent
import ru.mironov.currencyconverter.di.TestAppComponent
import ru.mironov.currencyconverter.model.viewmodels.ViewModelGraphFragment
import ru.mironov.currencyconverter.model.ResponseHistory

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    private lateinit var appComponent: TestAppComponent

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        appComponent = DaggerTestAppComponent.builder()
            .context(appContext)
            .build()
        appComponent.inject(this)

    }

    @Inject
    lateinit var retrofit: CurrencyApi

    @Inject
    lateinit var viwModel: ViewModelGraphFragment

    @Test
    fun encryptedSharedPreferencesTest() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val sharedPreferences = EncryptedDataShared(appContext, "secret_shared_prefs")

        val str = "Some string"

        //sharedPreferences.saveString(str)

        val value = sharedPreferences.getString("Key1")


        assertEquals(str, value)
    }

    @Test
    fun apiRatesTest() {
        val baseCurrency = "EUR"

        val call: Call<ResponseRates?> =
            retrofit.getRatesBySpecific("c5389740-6041-11ec-9110-35b9a6bda6ab", baseCurrency)

        val response: Response<ResponseRates?> = call!!.execute()
        var body = response.body() as ResponseRates

        Log.d("Test_tag",body.getRates().toString())

        assertEquals(true, body.getRates().isNotEmpty())
    }

    @Test
    fun apiHistoryTest() {
        val baseCurrency = "EUR"

        val call: Call<ResponseHistory?> =
            retrofit.getHistory("c5389740-6041-11ec-9110-35b9a6bda6ab", baseCurrency,"2021-10-27","2021-12-27")

        val response: Response<ResponseHistory?> = call!!.execute()
        var body = response.body() as ResponseHistory

        assertEquals(true, body.getBaseCurrency() == baseCurrency)
    }
}

package ru.mironov.currencyconverter


import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

import ru.mironov.currencyconverter.repository.EncryptedDataShared
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.retrofit.JsonObject
import javax.inject.Inject
import org.junit.Before
import ru.mironov.currencyconverter.di.AppComponent
import ru.mironov.currencyconverter.di.DaggerAppComponent
import ru.mironov.currencyconverter.di.DaggerTestAppComponent
import ru.mironov.currencyconverter.di.TestAppComponent


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
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
    fun apiTest() {
        val baseCurrency = "EUR"

        val call: Call<JsonObject?> =
            retrofit.getRates("c5389740-6041-11ec-9110-35b9a6bda6ab", baseCurrency)

        val response: Response<JsonObject?> = call!!.execute()
        var body = response.body() as JsonObject

        assertEquals(true, body.getBaseCurrency() == baseCurrency)
    }
}

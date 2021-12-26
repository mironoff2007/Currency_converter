package ru.mironov.currencyconverter


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


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    @Test
    fun encryptedSharedPreferencesTest() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val sharedPreferences = EncryptedDataShared(appContext,"secret_shared_prefs")

        val str="Some string"

        //sharedPreferences.saveString(str)

        val value = sharedPreferences.getString("Key1")

        assertEquals(str,value)
    }

    @Inject
    lateinit var retrofit: CurrencyApi

   fun retrofitTest(){
       val appContext = InstrumentationRegistry.getInstrumentation().targetContext
       appContext.appComponent.inject(this)

       val  call: Call<JsonObject?> =retrofit.getRates("c5389740-6041-11ec-9110-35b9a6bda6ab","EUR")

       val response: Response<JsonObject?> =call!!.execute()
       var body=response.body()

       assertEquals(true, true)

   }


}

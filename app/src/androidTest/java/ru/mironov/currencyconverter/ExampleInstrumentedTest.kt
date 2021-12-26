package ru.mironov.currencyconverter


import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

import ru.mironov.currencyconverter.repository.EncryptedDataShared


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


}

package ru.mironov.currencyconverter

import android.util.Base64
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import ru.mironov.currencyconverter.security.Cryptography



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val SAMPLE_ALIAS = "MYALIAS"
    private val TAG = "MY_tag"

    @Test
    fun useAppContext() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext


        val KEY_NAME:String = "Key1"

        val c = Cryptography(KEY_NAME);

        val encrypted = c.encrypt("plain text"); // returns base 64 data: 'BASE64_DATA,BASE64_IV'

        val decrypted = c.decrypt(encrypted);


        Log.d(TAG, decrypted.toString())

    }


}

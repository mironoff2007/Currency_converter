package ru.mironov.currencyconverter.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class DataShared(context: Context, dataName:String) {

    private val masterKey:MasterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences :SharedPreferences = EncryptedSharedPreferences.create(
        context,
        dataName,
        masterKey!!,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveString(str: String, strName: String) {
        editor.putString(strName, str).apply()
    }

    fun getString(strName: String): String? {
        return sharedPreferences.getString(strName, null)
    }

    fun clearPrefs() {
        editor.clear().commit()
    }
}
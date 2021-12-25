package ru.mironov.currencyconverter

import android.app.Application
import android.content.Context
import ru.mironov.currencyconverter.di.AppComponent

import ru.mironov.currencyconverter.di.DaggerAppComponent





class MainApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
    }


}

val Context.appComponent: AppComponent
    get() = when (this) {
        is MainApp -> appComponent
        else -> applicationContext.appComponent
    }
package ru.mironov.currencyconverter

import dagger.Component
import dagger.Module

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)

}

@Module()
class AppModule


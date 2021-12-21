package ru.mironov.currencyconverter

import dagger.Component
import dagger.Module

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    //fun inject(fragment: CurrenciesFragment)

}

@Module()
class AppModule


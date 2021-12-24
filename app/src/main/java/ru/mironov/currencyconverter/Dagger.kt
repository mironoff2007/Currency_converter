package ru.mironov.currencyconverter

import dagger.Component
import dagger.Module

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: TabsFragment)
    fun inject(fragment: SetKeyFragment)
    fun inject(fragment: CurrenciesFragment)

}

@Module()
class AppModule


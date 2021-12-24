package ru.mironov.currencyconverter

import dagger.Component
import dagger.Module
import dagger.Provides
import ru.mironov.currencyconverter.ui.CurrenciesFragment
import ru.mironov.currencyconverter.ui.SetKeyFragment
import ru.mironov.currencyconverter.ui.TabsFragment
import ru.mironov.currencyconverter.MyClass
import ru.mironov.currencyconverter.ui.GraphFragment

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: TabsFragment)
    fun inject(fragment: SetKeyFragment)
    fun inject(fragment: CurrenciesFragment)
    fun inject(fragment: GraphFragment)

}

@Module()
class AppModule{

    @Provides
    fun provideMyClass():MyClass{
        return MyClass()
    }
}


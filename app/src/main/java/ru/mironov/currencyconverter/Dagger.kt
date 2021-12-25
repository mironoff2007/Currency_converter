package ru.mironov.currencyconverter

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.mironov.currencyconverter.ui.CurrenciesFragment
import ru.mironov.currencyconverter.ui.SetKeyFragment
import ru.mironov.currencyconverter.ui.TabsFragment
import ru.mironov.currencyconverter.di.MultiViewModelFactory
import ru.mironov.currencyconverter.di.ViewModelKey
import ru.mironov.currencyconverter.ui.GraphFragment

@Component(modules = [AppModule::class,AppBindsModule::class])

interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: TabsFragment)
    fun inject(fragment: SetKeyFragment)
    fun inject(fragment: CurrenciesFragment)
    fun inject(fragment: GraphFragment)

    val factory: MultiViewModelFactory

    @Component.Builder
    interface Builder {

        fun build(): AppComponent
    }
}

@Module()
class AppModule{

    @Provides
    fun provideMyClass():MyClass{
        return MyClass()
    }
}

@Module()
interface AppBindsModule {

    @Binds
    @[IntoMap ViewModelKey(ViewModelSetKeyFragment::class)]
    fun provideViewModelSetKeyFragment(mainViewModel: ViewModelSetKeyFragment): ViewModel

}




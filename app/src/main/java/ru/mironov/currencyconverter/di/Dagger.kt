package ru.mironov.currencyconverter.di

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.*
import dagger.multibindings.IntoMap
import ru.mironov.currencyconverter.MainActivity
import ru.mironov.currencyconverter.MainApp
import ru.mironov.currencyconverter.MyClass
import ru.mironov.currencyconverter.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.ui.CurrenciesFragment
import ru.mironov.currencyconverter.ui.SetKeyFragment
import ru.mironov.currencyconverter.ui.TabsFragment
import ru.mironov.currencyconverter.ui.GraphFragment
import javax.inject.Singleton


@Component(modules = [AppModule::class, AppBindsModule::class])

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

        @BindsInstance
        fun context(context: Context): Builder
    }


}

@Module
class AppModule() {

    @Provides
    fun provideMyClass(): MyClass {
        return MyClass()
    }

}



@Module()
interface AppBindsModule {

    @Binds
    @[IntoMap ViewModelKey(ViewModelSetKeyFragment::class)]
    fun provideViewModelSetKeyFragment(mainViewModel: ViewModelSetKeyFragment): ViewModel

}




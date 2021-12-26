package ru.mironov.currencyconverter.di

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.*
import dagger.multibindings.IntoMap
import ru.mironov.currencyconverter.MainActivity
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.model.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.model.ViewModelMainActivity
import ru.mironov.currencyconverter.repository.DataShared
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.ui.CurrenciesFragment
import ru.mironov.currencyconverter.ui.SetKeyFragment
import ru.mironov.currencyconverter.ui.TabsFragment
import ru.mironov.currencyconverter.ui.GraphFragment


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
    fun provideRepository(dataShared:DataShared): Repository {
        return Repository(dataShared)
    }

    @Provides
    fun provideDataShared(context:Context): DataShared {
        return DataShared(context,"data_shared")
    }

}


@Module()
interface AppBindsModule {

    @Binds
    @[IntoMap ViewModelKey(ViewModelSetKeyFragment::class)]
    fun provideViewModelSetKeyFragment(viewModelSetKeyFragment: ViewModelSetKeyFragment): ViewModel

    @Binds
    @[IntoMap ViewModelKey(ViewModelMainActivity::class)]
    fun provideViewModelViewModelMainActivity(viewModelMainActivity: ViewModelMainActivity): ViewModel

    @Binds
    @[IntoMap ViewModelKey(ViewModelCurrenciesFragment::class)]
    fun provideViewModelCurrenciesFragment(viewModelCurrenciesFragment: ViewModelCurrenciesFragment): ViewModel

}




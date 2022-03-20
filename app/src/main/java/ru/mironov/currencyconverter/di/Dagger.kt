package ru.mironov.currencyconverter.di

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.*
import dagger.multibindings.IntoMap
import ru.mironov.currencyconverter.MainActivity
import ru.mironov.currencyconverter.model.viewmodels.ViewModelConverterFragment
import ru.mironov.currencyconverter.model.viewmodels.ViewModelGraphFragment
import ru.mironov.currencyconverter.model.viewmodels.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.model.viewmodels.ViewModelMainActivity
import ru.mironov.currencyconverter.repository.DataShared
import ru.mironov.currencyconverter.repository.EncryptedDataShared
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.ui.*


@Component(modules = [AppModule::class, AppBindsModule::class,RetrofitModule::class])

interface AppComponent  {
    fun inject(activity: MainActivity)
    fun inject(fragment: TabsFragment)
    fun inject(fragment: SetKeyFragment)
    fun inject(fragment: ConverterFragment)
    fun inject(fragment: GraphFragment)
    fun inject(fragment: CurrenciesFavoriteFragment)

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
    fun provideRepository(dataShared:DataShared,encryptedDataShared:EncryptedDataShared,retrofit:CurrencyApi): Repository {
        return Repository(dataShared,encryptedDataShared,retrofit)
    }

    @Provides
    fun provideEncryptedDataShared(context:Context): EncryptedDataShared {
        return EncryptedDataShared(context,"data_shared")
    }

    @Provides
    fun provideDataShared(context:Context): DataShared {
        return DataShared(context,"cache")
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
    @[IntoMap ViewModelKey(ViewModelConverterFragment::class)]
    fun provideViewModelConverterFragment(viewModelConverterFragment: ViewModelConverterFragment): ViewModel

    @Binds
    @[IntoMap ViewModelKey(ViewModelGraphFragment::class)]
    fun provideViewModelGraphFragment(viewModelGraphFragment: ViewModelGraphFragment): ViewModel
}






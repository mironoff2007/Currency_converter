package ru.mironov.currencyconverter.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.*
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mironov.currencyconverter.ExampleInstrumentedTest
import ru.mironov.currencyconverter.MainActivity
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.model.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.model.ViewModelMainActivity
import ru.mironov.currencyconverter.repository.EncryptedDataShared
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.ui.CurrenciesFragment
import ru.mironov.currencyconverter.ui.SetKeyFragment
import ru.mironov.currencyconverter.ui.TabsFragment
import ru.mironov.currencyconverter.ui.GraphFragment
import javax.inject.Singleton


@Component(modules = [AppModule::class, AppBindsModule::class,RetrofitModule::class])

interface AppComponent {

    fun inject(exampleInstrumentedTest: ExampleInstrumentedTest) {

    }

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
    fun provideRepository(encryptedDataShared:EncryptedDataShared,retrofit:CurrencyApi): Repository {
        return Repository(encryptedDataShared,retrofit)
    }

    @Provides
    fun provideDataShared(context:Context): EncryptedDataShared {
        return EncryptedDataShared(context,"data_shared")
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






package ru.mironov.currencyconverter.di

import android.content.Context
import dagger.*
import ru.mironov.currencyconverter.InstrumentedTest

@Component(modules = [AppModule::class, AppBindsModule::class,RetrofitModule::class])

interface TestAppComponent {

    fun inject(instrumentedTest: InstrumentedTest)

    val factory: MultiViewModelFactory

    @Component.Builder
    interface Builder {

        fun build(): TestAppComponent

        @BindsInstance
        fun context(context: Context): Builder
    }
}






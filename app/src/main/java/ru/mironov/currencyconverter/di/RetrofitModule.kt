package ru.mironov.currencyconverter.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import ru.mironov.currencyconverter.util.DateDeserializer
import java.util.*


@Module
object RetrofitModule {

    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://api.currencyapi.com/v3/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls()
                        .create()
                )
            )
    }

    @Provides
    fun provideCurrencyApi(retrofit: Retrofit.Builder): CurrencyApi {
        return retrofit
            .build()
            .create(CurrencyApi::class.java)
    }
}





















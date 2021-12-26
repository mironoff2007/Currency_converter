package ru.mironov.currencyconverter.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mironov.currencyconverter.retrofit.CurrencyApi
import javax.inject.Singleton

@Module
object RetrofitModule {



    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }


    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://freecurrencyapi.net/api/v2/")
            .addConverterFactory(GsonConverterFactory.create(gson))
    }


    @Provides
    fun provideBlogService(retrofit: Retrofit.Builder): CurrencyApi {
        return retrofit
            .build()
            .create(CurrencyApi::class.java)
    }

}





















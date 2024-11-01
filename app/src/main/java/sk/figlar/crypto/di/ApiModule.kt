package sk.figlar.crypto.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import sk.figlar.crypto.api.CryptoApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://data-api.binance.vision/")
//        .addConverterFactory(
//            MoshiConverterFactory.create(
//            Moshi.Builder()
//                .add(KotlinJsonAdapterFactory())
//                .build()))
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): CryptoApi =
        retrofit.create()
}
package com.hae.eforest

import android.content.Context
import com.hae.eforest.data.service.network.RemoteServiceIntercepter
import com.hae.eforest.data.service.network.SumSubServiceIntercepter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Named("BaseHttpClient")
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(RemoteServiceIntercepter())
            .build()
    }

    @Provides
    @Named("SumSubHttpClient")
    @Singleton
    fun provideSumSubHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(SumSubServiceIntercepter())
            .build()
    }

    @Singleton
    @Named("BaseRetrofit")
    @Provides
    fun provideBaseRetrofit(
        @Named("BaseHttpClient") okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.RELEASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Named("SumSubRetrofit")
    @Provides
    fun provideSumSubRetrofit(
        @Named("SumSubHttpClient") okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.SUMSUB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideRemoteServiceAPI(@Named("BaseRetrofit") retrofit: Retrofit): RemoteServiceAPI {
        return retrofit.create(RemoteServiceAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideSumSubServiceAPI(@Named("SumSubRetrofit") retrofit: Retrofit): SumSubServiceAPI {
        return retrofit.create(SumSubServiceAPI::class.java)
    }
}
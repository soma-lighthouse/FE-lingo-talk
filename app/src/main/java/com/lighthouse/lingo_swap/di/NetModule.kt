package com.lighthouse.lingo_swap.di

import com.google.gson.GsonBuilder
import com.lighthouse.android.data.api.DrivenApiService
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.api.interceptor.ContentInterceptor
import com.lighthouse.domain.response.server_driven.ViewTypeVO
import com.lighthouse.lingo_swap.BuildConfig
import com.lighthouse.lingo_swap.HeaderInterceptor
import com.lighthouse.lingo_swap.ViewTypeDeserializer
import com.lighthouse.lingo_swap.di.Annotation.Main
import com.lighthouse.lingo_swap.di.Annotation.Test
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetModule {
    @Provides
    @Singleton
    @Main
    fun provideLightHouseHttpClient(headerInterceptor: HeaderInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(headerInterceptor)
            .build()

    @Provides
    @Singleton
    @Main
    fun provideLightHouseRetrofit(@Main okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.LIGHTHOUSE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    @Test
    fun provideDrivenHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(ContentInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @Test
    fun provideDrivenRetrofit(@Test okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.TEST_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(ViewTypeVO::class.java, ViewTypeDeserializer())
                        .create()
                )
            )

            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Test
    fun provideDrivenAPIService(@Test retrofit: Retrofit): DrivenApiService =
        retrofit.create(DrivenApiService::class.java)

    @Provides
    @Singleton
    @Main
    fun provideHomeService(@Main retrofit: Retrofit): HomeApiService {
        return retrofit.create(HomeApiService::class.java)
    }
}

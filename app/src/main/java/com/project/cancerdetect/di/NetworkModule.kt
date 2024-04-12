package com.project.cancerdetect.di

import com.project.cancerdetect.util.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    @Provides
//    @Singleton
//    fun provideRetrofitInstance(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//}
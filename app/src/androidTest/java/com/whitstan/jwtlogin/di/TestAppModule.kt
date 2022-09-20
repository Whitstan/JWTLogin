package com.whitstan.jwtlogin.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.whitstan.jwtlogin.network.AuthApi
import com.whitstan.jwtlogin.repository.AuthRepository
import com.whitstan.jwtlogin.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, preferences: SharedPreferences): AuthRepository {
        return AuthRepositoryImpl(api, preferences)
    }

}
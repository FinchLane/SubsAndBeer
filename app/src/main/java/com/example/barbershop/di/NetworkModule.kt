package com.example.barbershop.di

import com.example.barbershop.Constants.BASE_URl
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.network.Authenticated
import com.example.barbershop.data.network.LocalDateAdapter
import com.example.barbershop.utils.jwtToken.EncryptedTokenManager
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: EncryptedTokenManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val invocation = request.tag(Invocation::class.java)
                val method = invocation?.method()
                if (method?.isAnnotationPresent(Authenticated::class.java) == true) {
                    val token = tokenManager.getToken() ?: ""
                    val newRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(newRequest)
                } else {
                    chain.proceed(request)
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
package com.example.barbershop.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.barbershop.AppSettings
import com.example.barbershop.utils.worker.CustomWorkerFactory
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.repository.SubRepository
import com.example.barbershop.utils.getIPAddress
import com.example.barbershop.utils.jwtToken.EncryptedTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): EncryptedTokenManager {
        return EncryptedTokenManager(context)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideIpAddress(@ApplicationContext context: Context): String{
        return getIPAddress(context)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideAppSettings(dataStore: DataStore<Preferences>): AppSettings{
        return AppSettings(dataStore)
    }

    @Provides
    @Singleton
    fun provideCustomWorkerFactory(
        subRepository: SubRepository,
        apiService: ApiService,
        appSettings: AppSettings
    ): CustomWorkerFactory {
        return CustomWorkerFactory(subRepository, apiService, appSettings)
    }

    @Provides
    fun provideNotificationManagerCompat(
        @ApplicationContext context: Context
    ): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }
}
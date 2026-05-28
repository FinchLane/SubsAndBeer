package com.example.barbershop.di

import com.example.barbershop.data.database.MessageDao
import com.example.barbershop.data.database.ProfileDao
import com.example.barbershop.data.database.family.FamilyDao
import com.example.barbershop.data.database.subscription.CategoryDao
import com.example.barbershop.data.database.subscription.CurrencyDao
import com.example.barbershop.data.database.subscription.NotificationDao
import com.example.barbershop.data.database.subscription.PaymentMethodDao
import com.example.barbershop.data.database.subscription.SubscriptionDao
import com.example.barbershop.data.database.subscription.SubscriptionNotificationDao
import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.repository.AuthRepository
import com.example.barbershop.data.repository.ProfileRepository
import com.example.barbershop.data.repository.SecurityRepository
import com.example.barbershop.data.repository.SubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideAuthRepository(apiService: ApiService): AuthRepository {
        return AuthRepository(apiService)
    }

    @Provides
    fun provideProfileRepository(apiService: ApiService, profileDao: ProfileDao, messageDao: MessageDao, familyDao: FamilyDao): ProfileRepository {
        return ProfileRepository(apiService, profileDao, messageDao, familyDao)
    }

    @Provides
    fun provideSecurityRepository(apiService: ApiService): SecurityRepository{
        return SecurityRepository(apiService)
    }

    @Provides
    fun provideSubRepository(
        categoryDao: CategoryDao,
        paymentMethodDao: PaymentMethodDao,
        subscriptionDao: SubscriptionDao,
        notificationDao: NotificationDao,
        subscriptionNotificationDao: SubscriptionNotificationDao,
        currencyDao: CurrencyDao,
        apiService: ApiService
    ): SubRepository{
        return SubRepository(
            categoryDao,
            paymentMethodDao,
            subscriptionDao,
            notificationDao,
            subscriptionNotificationDao,
            currencyDao,
            apiService
        )
    }
}
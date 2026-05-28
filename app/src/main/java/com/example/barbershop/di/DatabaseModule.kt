package com.example.barbershop.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.barbershop.data.database.AppDatabase
import com.example.barbershop.data.database.MessageDao
import com.example.barbershop.data.database.ProfileDao
import com.example.barbershop.data.database.family.FamilyDao
import com.example.barbershop.data.database.subscription.CategoryDao
import com.example.barbershop.data.database.subscription.CurrencyDao
import com.example.barbershop.data.database.subscription.NotificationDao
import com.example.barbershop.data.database.subscription.PaymentMethodDao
import com.example.barbershop.data.database.subscription.SubscriptionDao
import com.example.barbershop.data.database.subscription.SubscriptionNotificationDao
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.ExchangeRate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = provideDatabase(context).currencyDao()
                        val defaultCurrencies = listOf(
                            Currency("RUB", "Российский рубль", "₽", 1),
                            Currency("USD", "Доллар США", "$", 1),
                            Currency("EUR", "Евро", "€", 1)
                        )
                        val defaultRates = listOf(
                            ExchangeRate(fromCurrencyId = "RUB", toCurrencyId = "RUB", rate = 1.0, source = "default", date = LocalDateTime.now(), isCustom = false),
                            ExchangeRate(fromCurrencyId = "RUB", toCurrencyId = "USD", rate = 90.0, source = "default", date = LocalDateTime.now(), isCustom = false),
                            ExchangeRate(fromCurrencyId = "RUB", toCurrencyId = "EUR", rate = 100.0, source = "default", date = LocalDateTime.now(), isCustom = false)
                        )
                        dao.insertCurrencies(defaultCurrencies)
                        dao.insertExchangeRates(defaultRates)
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProfileDao(database: AppDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun providePaymentMethodDao(database: AppDatabase): PaymentMethodDao {
        return database.paymentMethodDao()
    }

    @Provides
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    fun provideSubscriptionNotificationDao(database: AppDatabase): SubscriptionNotificationDao {
        return database.subscriptionNotificationDao()
    }

    @Provides
    fun provideCurrencyDao(database: AppDatabase): CurrencyDao {
        return database.currencyDao()
    }

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideFamilyDao(database: AppDatabase): FamilyDao {
        return database.familyDao()
    }
}
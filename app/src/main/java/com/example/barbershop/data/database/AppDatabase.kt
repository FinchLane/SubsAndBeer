package com.example.barbershop.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.barbershop.data.database.family.FamilyDao
import com.example.barbershop.data.database.subscription.CategoryDao
import com.example.barbershop.data.database.subscription.CurrencyDao
import com.example.barbershop.data.database.subscription.NotificationDao
import com.example.barbershop.data.database.subscription.PaymentMethodDao
import com.example.barbershop.data.database.subscription.SubscriptionDao
import com.example.barbershop.data.database.subscription.SubscriptionNotificationDao
import com.example.barbershop.data.model.MessageEntity
import com.example.barbershop.data.model.ProfileEntity
import com.example.barbershop.data.model.family.FamilyEntity
import com.example.barbershop.data.model.family.FamilyInviteEntity
import com.example.barbershop.data.model.family.FamilyMemberEntity
import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.ExchangeRate
import com.example.barbershop.data.model.subscription.Notification
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.data.model.subscription.SubscriptionNotification

@Database(
    entities = [
        ProfileEntity::class, Category::class, PaymentMethod::class, Subscription::class,
        Notification::class, SubscriptionNotification::class, Currency::class, ExchangeRate::class,
        MessageEntity::class, FamilyEntity::class, FamilyInviteEntity::class, FamilyMemberEntity::class
               ],
    version = 22
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun profileDao(): ProfileDao
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun subscriptionNotificationDao(): SubscriptionNotificationDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun messageDao(): MessageDao
    abstract fun familyDao(): FamilyDao
}
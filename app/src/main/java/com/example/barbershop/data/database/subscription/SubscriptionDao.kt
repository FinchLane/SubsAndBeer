package com.example.barbershop.data.database.subscription

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.data.model.subscription.SubscriptionWithNotifications
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription)

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscription(id: String): Subscription?

    @Query("SELECT * FROM subscriptions")
    suspend fun getAllSubscription(): List<Subscription>?

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscription(id: String)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAllSubscription()

    @Transaction
    @Query("SELECT * FROM subscriptions")
    suspend fun getAllSubscriptionsWithNotifications(): List<SubscriptionWithNotifications>

    @Transaction
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionWithNotificationsById(id: String): SubscriptionWithNotifications?

    /** Синхронизация с сервером */

    @Query("SELECT * FROM subscriptions WHERE syncStatus = 'pending'")
    suspend fun getPendingSubscriptions(): List<Subscription>

    @Query("SELECT * FROM subscriptions WHERE operationType = 'delete' AND syncStatus = 'synced'")
    suspend fun getDeletedSubscription(): List<Subscription>

    @Query("UPDATE subscriptions SET syncStatus = :status WHERE id = :id")
    suspend fun updateSubscriptionSyncStatus(id: String, status: String)


    @Query("UPDATE subscriptions SET syncStatus = :status, lastServerSyncTime = :timestamp WHERE id = :id")
    suspend fun updateSubscriptionSyncStatusAndTime(id: String, status: String, timestamp: Long)

    @Query("SELECT MAX(lastServerSyncTime)\n" +
            "FROM (\n" +
            "    SELECT lastServerSyncTime FROM subscriptions WHERE syncStatus = 'synced'\n" +
            "    UNION ALL\n" +
            "    SELECT lastServerSyncTime FROM categories WHERE syncStatus = 'synced'\n" +
            "    UNION ALL\n" +
            "    SELECT lastServerSyncTime FROM payment_methods WHERE syncStatus = 'synced'\n" +
            ") AS combinedResults")
    suspend fun getLastServerSyncTime() : Long?

    @Query("UPDATE subscriptions SET lastServerSyncTime = 0")
    suspend fun clearSubscriptionTime()

    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptionFlow(): Flow<List<Subscription>>

}
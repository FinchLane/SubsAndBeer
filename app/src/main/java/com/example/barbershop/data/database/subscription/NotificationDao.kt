package com.example.barbershop.data.database.subscription

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.barbershop.data.model.subscription.Notification
import com.example.barbershop.data.model.subscription.SubscriptionWithNotifications

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: Notification)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifications(notifications: List<Notification>)

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotifications(): List<Notification>

    @Transaction
    @Query("SELECT * FROM subscriptions")
    suspend fun getAllWithNotifications(): List<SubscriptionWithNotifications>

    /** Синхронизация с сервером */

//    @Query("SELECT * FROM notifications WHERE syncStatus = 'pending'")
//    suspend fun getPendingNotification(): List<Notification>
//
//    @Query("UPDATE notifications SET syncStatus = :status WHERE id = :id")
//    suspend fun updateNotificationSyncStatus(id: Int, status: String)

}
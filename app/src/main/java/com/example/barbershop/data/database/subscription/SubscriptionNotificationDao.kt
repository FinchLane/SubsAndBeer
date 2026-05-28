package com.example.barbershop.data.database.subscription

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.barbershop.data.model.subscription.Notification
import com.example.barbershop.data.model.subscription.SubscriptionNotification

@Dao
interface SubscriptionNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptionNotification(subscriptionNotification: SubscriptionNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptionNotifications(subscriptionNotifications: List<SubscriptionNotification>)

    @Query("DELETE FROM subscription_notifications WHERE subscriptionId = :subscriptionId")
    suspend fun deleteNotificationsForSubscription(subscriptionId: String)

    @Query("""
        SELECT n.* FROM notifications n
        INNER JOIN subscription_notifications sn ON n.id = sn.notificationId
        WHERE sn.subscriptionId = :subscriptionId
    """)
    suspend fun getNotificationsForSubscription(subscriptionId: String): List<Notification>

    /** Синхронизация с сервером */

//    @Query("SELECT * FROM subscription_notifications WHERE syncStatus = 'pending'")
//    suspend fun getPendingSubscriptionNotification(): List<SubscriptionNotification>
//
//    @Query("UPDATE subscription_notifications SET syncStatus = :status WHERE subscriptionId = :subscriptionId AND notificationId = :notificationId")
//    suspend fun updateSyncStatusSubscriptionNotification(subscriptionId: Int, notificationId: Int, status: String)
}
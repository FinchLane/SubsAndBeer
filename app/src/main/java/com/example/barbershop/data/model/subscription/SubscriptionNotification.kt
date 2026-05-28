package com.example.barbershop.data.model.subscription

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "subscription_notifications",
    primaryKeys = ["subscriptionId", "notificationId"],
    foreignKeys = [
        ForeignKey(
            entity = Subscription::class,
            parentColumns = ["id"],
            childColumns = ["subscriptionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Notification::class,
            parentColumns = ["id"],
            childColumns = ["notificationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubscriptionNotification(
    val subscriptionId: String,
    val notificationId: Int
)
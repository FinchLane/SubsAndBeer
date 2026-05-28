package com.example.barbershop.data.model.subscription

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SubscriptionWithNotifications(
    @Embedded val subscription: Subscription,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(SubscriptionNotification::class, "subscriptionId", "notificationId")
    )
    val notifications: List<Notification>
)

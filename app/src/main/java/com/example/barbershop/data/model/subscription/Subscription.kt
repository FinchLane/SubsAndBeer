package com.example.barbershop.data.model.subscription

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PaymentMethod::class,
            parentColumns = ["id"],
            childColumns = ["paymentMethodId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["id"],
            childColumns = ["currency"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Subscription(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var iconSource: String,
    var backgroundColor: Int,
    var amount: Double,
    var currency: String = "RUB",
    var interval: Int = 1,
    var period: String,
    var startDate: LocalDate,
    var nextPaymentDate: LocalDate,
    var categoryId: String? = null,
    var paymentMethodId: String? = null,
    var cancelUrl: String? = null,
    val isArchive: Boolean,
    val syncStatus: String = "pending",
    val operationType: String = "create",
    val lastServerSyncTime: Long = 0L
)
// мб еще можно добавить комментарий к подписке
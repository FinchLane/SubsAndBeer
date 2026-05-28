package com.example.barbershop.data.network.response

import com.example.barbershop.data.model.subscription.Category
import com.example.barbershop.data.model.subscription.PaymentMethod
import com.example.barbershop.data.model.subscription.Subscription

data class SyncResponse(
    val message: String,
    val timestamp: Long?
)

data class SubscriptionDataResponse(
    val categories: List<Category>?,
    val paymentMethods: List<PaymentMethod>?,
    val subscriptions: List<Subscription>?,
    val deletedCategories: List<String>?,
    val deletedPaymentMethods: List<String>?,
    val deletedSubscriptions: List<String>?,
    val timestamp: Long
)
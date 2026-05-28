package com.example.barbershop.data.model.subscription

data class NotificationCheckbox(
    val id: Int,
    val text: String,
    val days: Int,
    val isChecked: Boolean
)

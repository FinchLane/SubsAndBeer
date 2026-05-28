package com.example.barbershop.data.model.subscription

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: Int,
    val text: String,
    val days: Int
)

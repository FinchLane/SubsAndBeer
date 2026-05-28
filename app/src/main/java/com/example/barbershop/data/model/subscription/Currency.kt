package com.example.barbershop.data.model.subscription

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val id: String,
    val name: String,
    val symbol: String,
    val nominal: Int
)

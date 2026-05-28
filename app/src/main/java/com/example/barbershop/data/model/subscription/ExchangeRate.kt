package com.example.barbershop.data.model.subscription

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "exchange_rates",
    primaryKeys = ["fromCurrencyId", "toCurrencyId"]
)
data class ExchangeRate(
    val fromCurrencyId: String,
    val toCurrencyId: String,
    val rate: Double,
    val source: String,
    val date: LocalDateTime,
    val isCustom: Boolean
)

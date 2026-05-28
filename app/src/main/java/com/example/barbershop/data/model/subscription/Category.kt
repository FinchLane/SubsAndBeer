package com.example.barbershop.data.model.subscription

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val syncStatus: String = "pending",
    val operationType: String = "create",
    val lastServerSyncTime: Long = 0L
)

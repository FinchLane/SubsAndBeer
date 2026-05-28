package com.example.barbershop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val screen: String?,
    val screenId: String?,
    val isChecked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

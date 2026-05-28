package com.example.barbershop.data.model.family

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "families")
data class FamilyEntity (
    @PrimaryKey
    val id: Int,
    val name: String,
    val createdAt: Long
)
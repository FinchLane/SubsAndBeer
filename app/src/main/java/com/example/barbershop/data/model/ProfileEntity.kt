package com.example.barbershop.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 0,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val email: String,
    val birthday: String,
    val photoPath: String? = null
)

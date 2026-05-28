package com.example.barbershop.data.model.family

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    "family_members",
    foreignKeys = [
        ForeignKey(
            entity = FamilyEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FamilyMemberEntity(
    @PrimaryKey
    val user: String,
    val familyId: Int,
    val name: String,
    val avatarUrl: String?,
    val role: String
)
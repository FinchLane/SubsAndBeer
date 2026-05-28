package com.example.barbershop.data.model.family

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "family_invites",
    foreignKeys = [
        ForeignKey(
            entity = FamilyEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("familyId")]
)
data class FamilyInviteEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val inviterUser: Int,
    val inviteeUser: Int,
    val familyId: Int,
    val status: String
)
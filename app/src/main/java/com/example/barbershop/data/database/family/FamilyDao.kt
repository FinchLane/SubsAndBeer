package com.example.barbershop.data.database.family

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.barbershop.data.model.family.FamilyEntity
import com.example.barbershop.data.model.family.FamilyMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {

    @Query("SELECT * FROM families")
    fun getAllFamily(): Flow<List<FamilyEntity>>

    @Query(value = "SELECT * FROM families WHERE id = :id")
    fun getFamily(id: Int): Flow<FamilyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: FamilyEntity)

    @Query("SELECT * FROM family_members")
    fun getFamilyMembers(): Flow<List<FamilyMemberEntity>>
}
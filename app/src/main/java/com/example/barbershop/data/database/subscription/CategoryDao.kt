package com.example.barbershop.data.database.subscription

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.barbershop.data.model.subscription.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategory(id: String): Category?

    @Query("SELECT * FROM categories")
    suspend fun getAllCategory(): List<Category>

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: String)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategory()

    /** Синхронизация с сервером */

    @Query("SELECT * FROM categories WHERE syncStatus = 'pending'")
    suspend fun getPendingCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE operationType = 'delete' AND syncStatus = 'synced'")
    suspend fun getDeletedCategory(): List<Category>

    @Query("UPDATE categories SET syncStatus = :status WHERE id = :id")
    suspend fun updateCategorySyncStatus(id: String, status: String)

    @Query("UPDATE categories SET syncStatus = :status, lastServerSyncTime = :timestamp WHERE id = :id")
    suspend fun updateCategorySyncStatusAndTime(id: String, status: String, timestamp: Long)

    @Query("UPDATE categories SET syncStatus = 0")
    suspend fun clearCategoryTime()

    @Query("SELECT * FROM categories")
    fun getAllCategoriesFlow(): Flow<List<Category>>
}